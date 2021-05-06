package com.hartwig.hmftools.lilac.candidates;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hartwig.hmftools.lilac.fragment.NucleotideFragment;
import com.hartwig.hmftools.lilac.seq.HlaSequenceLoci;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NucleotideFiltering
{
    private final int mMinNucleotideCount;
    private final Set<Integer> mAminoAcidBoundaries;

    public NucleotideFiltering(int minNucleotideCount, final Set<Integer> aminoAcidBoundaries)
    {
        mMinNucleotideCount = minNucleotideCount;
        mAminoAcidBoundaries = aminoAcidBoundaries;
    }

    public List<HlaSequenceLoci> filterCandidatesOnAminoAcidBoundaries(
            final List<HlaSequenceLoci> candidates, final List<NucleotideFragment> fragments)
    {
        List<HlaSequenceLoci> results = Lists.newArrayList();
        results.addAll(candidates);

        for(int boundary : mAminoAcidBoundaries)
        {
            int nucleotideStart = boundary * 3;
            final List<String> startSequences = nucleotideSequence(fragments, Sets.newHashSet(nucleotideStart));
            final List<String> endSequences = nucleotideSequence(fragments, Sets.newHashSet(nucleotideStart + 1, nucleotideStart + 2));

            results = results.stream()
                    .filter(x -> consistentWithAny(x, nucleotideStart, startSequences, endSequences))
                    .collect(Collectors.toList());
        }

        return results;
    }

    private static boolean consistentWithAny(
            final HlaSequenceLoci seqLoci, int startLoci, final List<String> startSequences, final List<String> endSequences)
    {
        return seqLoci.consistentWithAny(startSequences, Sets.newHashSet(startLoci))
            && seqLoci.consistentWithAny(endSequences, Sets.newHashSet(startLoci + 1, startLoci + 2));
    }

    private final List<String> nucleotideSequence(final List<NucleotideFragment> fragments, final Set<Integer> nucleotideIndices)
    {
        Map<String,Integer> sequenceCounts = Maps.newHashMap();

        for(NucleotideFragment fragment : fragments)
        {
            if(!fragment.containsAllNucleotides(nucleotideIndices))
                continue;

            String nucleotides = fragment.nucleotides(nucleotideIndices);

            Integer count = sequenceCounts.get(nucleotides);
            sequenceCounts.put(nucleotides, count != null ? count + 1 : 1);
        }

        return sequenceCounts.entrySet().stream()
                .filter(x -> x.getValue() >= mMinNucleotideCount)
                .map(x -> x.getKey())
                .collect(Collectors.toList());

    }

}
