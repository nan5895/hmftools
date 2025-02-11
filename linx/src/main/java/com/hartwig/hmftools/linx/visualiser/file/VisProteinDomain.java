package com.hartwig.hmftools.linx.visualiser.file;

import static java.util.stream.Collectors.toList;

import static com.hartwig.hmftools.linx.visualiser.file.VisCopyNumber.DELIMITER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.genome.region.GenomeRegion;

import org.jetbrains.annotations.NotNull;

public class VisProteinDomain implements GenomeRegion
{
    public final String SampleId;
    public final int ClusterId;
    public final String Transcript;
    public String Chromosome;
    public int Start;
    public int End;
    public final String Info;

    public static final String PD_FIVE_PRIME_UTR = "5-Prime UTR";
    public static final String PD_THREE_PRIME_UTR = "3-Prime UTR";
    public static final String PD_NON_CODING = "Non Coding";

    public static final String UTR = "UTR/Non-coding";

    public VisProteinDomain(final String sampleId, int clusterId, final String transcript,
            final String chromosome, int start, int end, final String info)
    {
        SampleId = sampleId;
        ClusterId = clusterId;
        Transcript = transcript;
        Chromosome = chromosome;
        Info = info;
        Start = start;
        End = end;
    }

    public static VisProteinDomain from(final VisProteinDomain other)
    {
        return new VisProteinDomain(other.SampleId, other.ClusterId, other.Transcript, other.Chromosome, other.Start, other.End, other.Info);
    }

    @Override
    public String chromosome() { return Chromosome; }

    @Override
    public int start() { return Start; }

    @Override
    public int end() { return End; }

    public String name()
    {
        return Info.equals(PD_FIVE_PRIME_UTR) || Info.equals(PD_FIVE_PRIME_UTR) || Info.equals(PD_NON_CODING) ? UTR : Info;
    }

    private static final String FILE_EXTENSION = ".linx.vis_protein_domain.tsv";

    @NotNull
    public static String generateFilename(@NotNull final String basePath, @NotNull final String sample)
    {
        return basePath + File.separator + sample + FILE_EXTENSION;
    }

    @NotNull
    public static List<VisProteinDomain> read(final String filePath) throws IOException
    {
        return fromLines(Files.readAllLines(new File(filePath).toPath()));
    }

    public static void write(@NotNull final String filename, @NotNull List<VisProteinDomain> cnDataList) throws IOException
    {
        Files.write(new File(filename).toPath(), toLines(cnDataList));
    }

    @NotNull
    static List<String> toLines(@NotNull final List<VisProteinDomain> cnDataList)
    {
        final List<String> lines = Lists.newArrayList();
        lines.add(header());
        cnDataList.stream().map(x -> toString(x)).forEach(lines::add);
        return lines;
    }

    @NotNull
    static List<VisProteinDomain> fromLines(@NotNull List<String> lines)
    {
        return lines.stream().filter(x -> !x.startsWith("SampleId")).map(VisProteinDomain::fromString).collect(toList());
    }

    @NotNull
    public static String header()
    {
        return new StringJoiner(DELIMITER)
                .add("SampleId")
                .add("ClusterId")
                .add("Transcript")
                .add("Chromosome")
                .add("Start")
                .add("End")
                .add("Info")
                .toString();
    }

    public static String toString(final VisProteinDomain proteinData)
    {
        return new StringJoiner(DELIMITER)
                .add(String.valueOf(proteinData.SampleId))
                .add(String.valueOf(proteinData.ClusterId))
                .add(String.valueOf(proteinData.Transcript))
                .add(String.valueOf(proteinData.Chromosome))
                .add(String.valueOf(proteinData.Start))
                .add(String.valueOf(proteinData.End))
                .add(String.valueOf(proteinData.Info))
                .toString();
    }

    private static VisProteinDomain fromString(final String line)
    {
        String[] values = line.split(DELIMITER);

        int index = 0;

        return new VisProteinDomain(
                values[index++],
                Integer.parseInt(values[index++]),
                values[index++],
                values[index++],
                Integer.parseInt(values[index++]),
                Integer.parseInt(values[index++]),
                values[index++]);
    }
}
