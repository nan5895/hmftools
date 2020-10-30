package com.hartwig.hmftools.cobalt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.hartwig.hmftools.common.cobalt.CobaltRatio;
import com.hartwig.hmftools.common.cobalt.CobaltRatioFile;
import com.hartwig.hmftools.common.cobalt.MedianRatio;
import com.hartwig.hmftools.common.cobalt.MedianRatioFactory;
import com.hartwig.hmftools.common.genome.chromosome.Chromosome;
import com.hartwig.hmftools.common.genome.chromosome.CobaltChromosome;
import com.hartwig.hmftools.common.genome.chromosome.CobaltChromosomes;
import com.hartwig.hmftools.common.genome.chromosome.HumanChromosome;
import com.hartwig.hmftools.common.genome.position.GenomePosition;
import com.hartwig.hmftools.common.genome.position.GenomePositions;
import com.hartwig.hmftools.common.purple.gender.Gender;
import com.hartwig.hmftools.common.utils.Doubles;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class CountBamLinesDiploidCount implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(CountBamLinesDiploidCount.class);
    private static final double MIN_DIPLOID = 0.85;
    private static final double MAX_DIPLOID = 1.15;

    private static final String DELIMITER = "\t";

    public static void main(String[] args) throws ParseException {
        final Options options = createOptions();
        final CommandLine cmd = new DefaultParser().parse(options, args);
        final String inputFile = cmd.getOptionValue("in");
        final String outputFile = cmd.getOptionValue("out");

        try (CountBamLinesDiploidCount app = new CountBamLinesDiploidCount(inputFile, outputFile)) {
            app.run();
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private final String inputFile;
    private final String outputFile;
    private final long timestamp = System.currentTimeMillis();

    public CountBamLinesDiploidCount(final String inputFile, final String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void run() throws IOException {
        boolean outputFileExists = new File(outputFile).exists();
        final Map<GenomePosition, DiploidCount> diploidCountMap;
        if (outputFileExists) {
            LOGGER.info("Reading existing output: {}", outputFile);
            diploidCountMap = readDiploidCount(outputFile);
        } else {
            diploidCountMap = Maps.newHashMap();
        }

        LOGGER.info("Reading input file {}", inputFile);
        final ListMultimap<Chromosome, CobaltRatio> ratios = CobaltRatioFile.read(inputFile);
        final List<MedianRatio> medianRatios = MedianRatioFactory.create(ratios);
        final CobaltChromosomes chromosomes = new CobaltChromosomes(medianRatios);
        if (chromosomes.hasGermlineAberrations()) {
            LOGGER.warn("Excluding {} because of germline abberations", inputFile);
            return;
        }

        LOGGER.info("Processing diploid regions");
        Collection<CobaltChromosome> selectedChromosomes =
                chromosomes.gender().equals(Gender.MALE) ? Collections.singleton(chromosomes.get("Y")) : chromosomes.chromosomes();

        for (CobaltChromosome selectedChromosome : selectedChromosomes) {
            Collection<CobaltRatio> selectedRatios = ratios.get(HumanChromosome.fromString(selectedChromosome.contig()));
            for (CobaltRatio ratio : selectedRatios) {
                GenomePosition position = GenomePositions.create(ratio.chromosome(), ratio.position());
                DiploidCount count = diploidCountMap.computeIfAbsent(position, x -> new DiploidCount(x, 0, 0));
                count.incrementTotal();
                if (isDiploid(selectedChromosome, ratio)) {
                    count.incrementDiploid();
                }
            }
        }


        LOGGER.info("Writing output: {}", outputFile);
        Files.write(new File(outputFile).toPath(),
                diploidCountMap.values().stream().sorted().map(DiploidCount::toString).collect(Collectors.toList()));

    }

    public boolean isDiploid(final CobaltChromosome chromosome, CobaltRatio ratio) {
        double value = ratio.referenceGCDiploidRatio();
        return (Doubles.greaterOrEqual(value, MIN_DIPLOID * chromosome.actualRatio()) && Doubles.lessOrEqual(value,
                MAX_DIPLOID * chromosome.actualRatio()));
    }

    @NotNull
    public static Map<GenomePosition, DiploidCount> readDiploidCount(final String outputFile) throws IOException {
        return Files.readAllLines(new File(outputFile).toPath())
                .stream()
                .map(DiploidCount::fromLine)
                .collect(Collectors.toMap(x -> x.position, x -> x));
    }

    @Override
    public void close() {
        LOGGER.info("Complete in {} seconds", (System.currentTimeMillis() - timestamp) / 1000);
    }

    static class DiploidCount implements Comparable<DiploidCount> {
        final GenomePosition position;
        int diploid;
        int count;

        static DiploidCount fromLine(String line) {
            final String[] values = line.split(DELIMITER);
            return new DiploidCount(GenomePositions.create(values[0], Long.parseLong(values[1])),
                    Integer.parseInt(values[2]),
                    Integer.parseInt(values[3]));
        }

        DiploidCount(final GenomePosition position, final int diploid, final int count) {
            this.position = position;
            this.diploid = diploid;
            this.count = count;
        }

        void incrementTotal() {
            count++;
        }

        void incrementDiploid() {
            diploid++;
        }

        @NotNull
        @Override
        public String toString() {
            return new StringJoiner(DELIMITER).add(position.chromosome())
                    .add(String.valueOf(position.position()))
                    .add(String.valueOf(diploid))
                    .add(String.valueOf(count))
                    .toString();
        }

        @Override
        public int compareTo(@NotNull final DiploidCount o) {
            return position.compareTo(o.position);
        }
    }

    @NotNull
    static Options createOptions() {
        final Options options = new Options();
        options.addOption("in", true, "Input ratios");
        options.addOption("out", true, "Output count");
        return options;
    }

}
