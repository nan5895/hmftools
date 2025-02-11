package com.hartwig.hmftools.cobalt.ratio;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.hartwig.hmftools.cobalt.Chromosome;
import com.hartwig.hmftools.common.cobalt.ImmutableReadRatio;
import com.hartwig.hmftools.common.cobalt.ReadRatio;
import com.hartwig.hmftools.common.genome.chromosome.HumanChromosome;
import com.hartwig.hmftools.common.genome.gc.GCMedianReadCount;
import com.hartwig.hmftools.cobalt.count.GCMedianReadCountBuilder;
import com.hartwig.hmftools.common.genome.gc.GCProfile;
import com.hartwig.hmftools.common.genome.position.GenomePosition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GCRatioNormalization
{
    private final GCMedianReadCountBuilder mMedianReadCountBuilder;
    private final Multimap<Chromosome, ReadCountWithGCContent> mEntries;

    public GCRatioNormalization()
    {
        mMedianReadCountBuilder = new GCMedianReadCountBuilder();
        mEntries = ArrayListMultimap.create();
    }

    void addPosition(@NotNull final Chromosome chromosome, @NotNull final GCProfile gcProfile, final int readCount)
    {
        final ReadCountWithGCContent readCountWithGCContent = new ReadCountWithGCContent(readCount, gcProfile);
        mEntries.put(chromosome, readCountWithGCContent);

        // TODO: TEST With/without isMappable
        if(HumanChromosome.fromString(chromosome.contig).isAutosome() && readCountWithGCContent.isMappable() && readCount > 0)
        {
            mMedianReadCountBuilder.add(gcProfile, readCount);
        }
    }

    public GCMedianReadCount gcMedianReadCount()
    {
        return mMedianReadCountBuilder.build();
    }

    public ArrayListMultimap<Chromosome, ReadRatio> build(@NotNull final GCMedianReadCount gcMedianReadCount)
    {
        final ArrayListMultimap<Chromosome, ReadRatio> result = ArrayListMultimap.create();
        for(Chromosome chromosome : mEntries.keySet())
        {
            final List<ReadRatio> normalisedRatio =
                    mEntries.get(chromosome).stream().map(x -> create(gcMedianReadCount, x))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
            result.replaceValues(chromosome, normalisedRatio);
        }

        return result;
    }

    @Nullable
    private static ReadRatio create(@NotNull final GCMedianReadCount medians, @NotNull final ReadCountWithGCContent readCount)
    {
        double gcMedianCount = medians.medianReadCount(readCount.gcProfile());
        final double ratio;

        double medianNormalisation = medians.medianReadCount() / medians.meanReadCount();

        if(gcMedianCount == -1 || !readCount.isMappable() || gcMedianCount == 0)
        {
            return null;
        }
        else
        {
            ratio = medianNormalisation * readCount.readCount() / gcMedianCount;
        }

        return ImmutableReadRatio.builder().from(readCount).ratio(ratio).build();
    }

    private static class ReadCountWithGCContent implements GenomePosition
    {
        public final GCProfile GcProfile;
        public final int ReadCount;

        private ReadCountWithGCContent(final int readCount, @NotNull final GCProfile gcProfile)
        {
            ReadCount = readCount;
            GcProfile = gcProfile;
        }

        @NotNull
        @Override
        public String chromosome()
        {
            return GcProfile.chromosome();
        }

        @Override
        public int position()
        {
            return GcProfile.start();
        }

        private int readCount()
        {
            return ReadCount;
        }

        GCProfile gcProfile()
        {
            return GcProfile;
        }

        private boolean isMappable()
        {
            return GcProfile.isMappable();
        }
    }
}
