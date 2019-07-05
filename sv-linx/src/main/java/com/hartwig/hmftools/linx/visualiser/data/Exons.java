package com.hartwig.hmftools.linx.visualiser.data;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hartwig.hmftools.common.region.GenomeRegion;
import com.hartwig.hmftools.common.region.GenomeRegions;
import com.hartwig.hmftools.linx.visualiser.file.VisGeneExonFile;

import org.jetbrains.annotations.NotNull;

public class Exons
{

    private static final Comparator<Exon> RANKED = Comparator.comparingInt(Exon::rank);

    @NotNull
    public static List<Exon> upstreamExons(@NotNull final Fusion fusion, @NotNull final List<Exon> exons)
    {
        return exons.stream()
                .filter(x -> x.gene().equals(fusion.geneUp()))
                .sorted(RANKED)
                .filter(x -> x.rank() <= fusion.exonUp())
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<Exon> downstreamExons(@NotNull final List<Fusion> fusions, @NotNull final List<Exon> exons)
    {
        final List<Exon> result = Lists.newArrayList();
        for (Fusion fusion : fusions)
        {
            result.addAll(downstreamExons(fusion, exons));
        }
        return result;
    }

    @NotNull
    public static List<Exon> downstreamExons(@NotNull final Fusion fusion, @NotNull final List<Exon> exons)
    {
        return exons.stream()
                .filter(x -> x.gene().equals(fusion.geneDown()))
                .sorted(RANKED)
                .filter(x -> x.rank() >= fusion.exonDown())
                .collect(Collectors.toList());
    }

    public static Collection<GenomeRegion> geneSpanPerChromosome(@NotNull final List<Exon> exons)
    {
        final Map<String, GenomeRegion> resultMap = Maps.newHashMap();
        for (Exon exon : exons)
        {
            final String contig = exon.chromosome();

            final GenomeRegion currentGene = resultMap.computeIfAbsent(contig, x -> exon);
            final GenomeRegion newGene =
                    GenomeRegions.create(contig, Math.min(currentGene.start(), exon.start()), Math.max(currentGene.end(), exon
                            .end()));
            resultMap.put(contig, newGene);

        }

        return resultMap.values();
    }

    @NotNull
    public static List<Exon> readExons(@NotNull final String fileName) throws IOException
    {
        return VisGeneExonFile.read(fileName).stream().map(Exons::fromVis).collect(Collectors.toList());
    }

    @NotNull
    private static Exon fromVis(@NotNull final VisGeneExonFile file)
    {
        return ImmutableExon.builder()
                .sampleId(file.SampleId)
                .clusterId(file.ClusterId)
                .gene(file.Gene)
                .chromosome(file.Chromosome)
                .rank(file.ExonRank)
                .start(file.ExonStart)
                .end(file.ExonEnd)
                .build();

    }
}
