package com.hartwig.hmftools.bamtools.markdups;

import static java.lang.String.format;

import static com.hartwig.hmftools.bamtools.BmConfig.BM_LOGGER;
import static com.hartwig.hmftools.common.utils.ConfigUtils.setLogLevel;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.common.genome.chromosome.HumanChromosome;
import com.hartwig.hmftools.common.genome.refgenome.RefGenomeCoordinates;
import com.hartwig.hmftools.common.utils.PerformanceCounter;
import com.hartwig.hmftools.common.utils.TaskExecutor;
import com.hartwig.hmftools.common.utils.sv.ChrBaseRegion;
import com.hartwig.hmftools.common.utils.version.VersionInfo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

import htsjdk.samtools.SAMRecord;

public class MarkDuplicates
{
    private final MarkDupsConfig mConfig;

    public MarkDuplicates(final CommandLine cmd)
    {
        mConfig = new MarkDupsConfig(cmd);
    }

    public void run()
    {
        if(!mConfig.isValid())
            System.exit(1);

        BM_LOGGER.info("sample({}) starting mark duplicates", mConfig.SampleId);

        long startTimeMs = System.currentTimeMillis();

        List<ChromosomeReader> chromosomeReaders = Lists.newArrayList();

        RefGenomeCoordinates refGenomeCoordinates = mConfig.RefGenVersion.is37() ? RefGenomeCoordinates.COORDS_37 : RefGenomeCoordinates.COORDS_38;

        RecordWriter recordWriter = new RecordWriter(mConfig);
        GroupCombiner groupCombiner = new GroupCombiner(recordWriter);
        final List<Callable> callableList = Lists.newArrayList();

        for(HumanChromosome chromosome : HumanChromosome.values())
        {
            String chromosomeStr = mConfig.RefGenVersion.versionedChromosome(chromosome.toString());

            if(!mConfig.SpecificChromosomes.isEmpty() && !mConfig.SpecificChromosomes.contains(chromosomeStr))
                continue;

            ChrBaseRegion chrBaseRegion = new ChrBaseRegion(chromosomeStr, 1, refGenomeCoordinates.Lengths.get(chromosome));

            ChromosomeReader chromosomeReader = new ChromosomeReader(chrBaseRegion, mConfig, recordWriter, groupCombiner);
            chromosomeReaders.add(chromosomeReader);
            callableList.add(chromosomeReader);
        }

        if(!TaskExecutor.executeTasks(callableList, mConfig.Threads))
            System.exit(1);

        long totalProcessReads = chromosomeReaders.stream().mapToLong(x -> x.totalRecordCount()).sum();

        BM_LOGGER.debug("all chromosomes complete, reads processed({}) written({})", totalProcessReads, recordWriter.recordWriteCount());

        groupCombiner.handleRemaining();
        recordWriter.close();

        if(BM_LOGGER.isTraceEnabled())
        {
            Set<SAMRecord> recordsWritten = recordWriter.readsWritten();
            Set<SAMRecord> recordsProcessed = Sets.newHashSet();
            chromosomeReaders.forEach(x -> recordsProcessed.addAll(x.readsProcessed()));

            if(recordsWritten.size() != recordsProcessed.size())
            {
                for(SAMRecord readProcessed : recordsProcessed)
                {
                    if(recordsWritten.contains(readProcessed))
                    {
                        recordsWritten.remove(readProcessed);
                    }
                    else
                    {
                        BM_LOGGER.error("read({}) coords({}:{}-{}) processed but not written",
                                readProcessed.getReadName(), readProcessed.getContig(),
                                readProcessed.getAlignmentStart(), readProcessed.getAlignmentEnd());
                    }
                }
            }
        }

        PerformanceCounter combinedPerfCounter = chromosomeReaders.get(0).perfCounter();

        for(int i = 1; i < chromosomeReaders.size(); ++i)
        {
            combinedPerfCounter.merge(chromosomeReaders.get(i).perfCounter());
        }

        if(mConfig.PerfDebug)
            combinedPerfCounter.logIntervalStats(10);
        else
            combinedPerfCounter.logStats();

        long timeTakenMs = System.currentTimeMillis() - startTimeMs;
        double timeTakeMins = timeTakenMs / 60000.0;

        BM_LOGGER.info("Mark duplicates complete, mins({})", format("%.3f", timeTakeMins));
    }

    public static void main(@NotNull final String[] args)
    {
        final VersionInfo version = new VersionInfo("bam-tools.version");
        BM_LOGGER.info("BamTools version: {}", version.version());

        final Options options = MarkDupsConfig.createCmdLineOptions();

        try
        {
            final CommandLine cmd = createCommandLine(args, options);

            setLogLevel(cmd);

            MarkDuplicates markDuplicates = new MarkDuplicates(cmd);
            markDuplicates.run();
        }
        catch(ParseException e)
        {
            BM_LOGGER.warn(e);
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("MarkDuplicates", options);
            System.exit(1);
        }
    }

    @NotNull
    private static CommandLine createCommandLine(@NotNull final String[] args, @NotNull final Options options) throws ParseException
    {
        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
