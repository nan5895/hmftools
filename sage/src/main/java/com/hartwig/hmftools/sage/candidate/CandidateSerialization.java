package com.hartwig.hmftools.sage.candidate;

import static com.hartwig.hmftools.common.sage.SageMetaData.TIER;
import static com.hartwig.hmftools.sage.vcf.SageVCF.RAW_DEPTH;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_EVENTS;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_INDEX;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_LEFT_FLANK;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_MICRO_HOMOLOGY;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_REPEAT_COUNT;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_REPEAT_SEQUENCE;
import static com.hartwig.hmftools.sage.vcf.SageVCF.READ_CONTEXT_RIGHT_FLANK;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.variant.hotspot.ImmutableVariantHotspotImpl;
import com.hartwig.hmftools.common.variant.hotspot.VariantHotspot;
import com.hartwig.hmftools.sage.read.IndexedBases;
import com.hartwig.hmftools.sage.read.ReadContext;
import com.hartwig.hmftools.sage.variant.SageVariantTier;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

public class CandidateSerialization {

    @NotNull
    public static Candidate fromContext(@NotNull final VariantContext context, @NotNull final IndexedBases refBases) {
        final int position = context.getStart();

        final VariantHotspot variant = ImmutableVariantHotspotImpl.builder()
                .chromosome(context.getContig())
                .position(position)
                .ref(context.getReference().getDisplayString())
                .alt(context.getAlternateAllele(0).getDisplayString())
                .build();

        final SageVariantTier tier = SageVariantTier.valueOf(context.getAttributeAsString(TIER, "LOW_CONFIDENCE"));
        final String leftFlank = context.getAttributeAsString(READ_CONTEXT_LEFT_FLANK, Strings.EMPTY);
        final String core = context.getAttributeAsString(READ_CONTEXT, Strings.EMPTY);
        final String rightFlank = context.getAttributeAsString(READ_CONTEXT_RIGHT_FLANK, Strings.EMPTY);
        final int readContextIndex = context.getAttributeAsInt(READ_CONTEXT_INDEX, 0);

        final int leftCoreIndex = leftFlank.length();
        final int rightFlankIndex = leftFlank.length() + core.length();
        final int rightCoreIndex = rightFlankIndex - 1;
        final byte[] bases = new byte[leftFlank.length() + core.length() + rightFlank.length()];
        System.arraycopy(leftFlank.getBytes(), 0, bases, 0, leftFlank.length());
        System.arraycopy(core.getBytes(), 0, bases, leftCoreIndex, core.length());
        System.arraycopy(rightFlank.getBytes(), 0, bases, rightFlankIndex, rightFlank.length());

        final int repeatCount = context.getAttributeAsInt(READ_CONTEXT_REPEAT_COUNT, 0);
        final String repeat = context.getAttributeAsString(READ_CONTEXT_REPEAT_SEQUENCE, Strings.EMPTY);
        final String mh = context.getAttributeAsString(READ_CONTEXT_MICRO_HOMOLOGY, Strings.EMPTY);

        final IndexedBases readBases = new IndexedBases(position,
                readContextIndex,
                leftFlank.length(),
                rightCoreIndex,
                Math.max(leftFlank.length(), rightFlank.length()),
                bases);

        final ReadContext readContext = new ReadContext(refBases, readBases, repeatCount, repeat, mh);

        int maxDepth = 0;
        for (Genotype genotype : context.getGenotypes()) {
            maxDepth = Math.max(maxDepth, genotype.getDP());
            maxDepth = Math.max(maxDepth, (int) genotype.getExtendedAttribute(RAW_DEPTH, 0));
        }

        return new Candidate(tier, variant, readContext, maxDepth, context.getAttributeAsInt(READ_CONTEXT_EVENTS, 0));
    }

    @NotNull
    public static VariantContextBuilder toContext(@NotNull final Candidate candidate) {
        final List<Allele> alleles = createAlleles(candidate.variant());
        final ReadContext readContext = candidate.readContext();

        final VariantContextBuilder builder = new VariantContextBuilder().chr(candidate.chromosome())
                .start(candidate.position())
                .attribute(TIER, candidate.tier())
                .attribute(READ_CONTEXT, candidate.readContext().toString())
                .attribute(READ_CONTEXT_LEFT_FLANK, candidate.readContext().leftFlankString())
                .attribute(READ_CONTEXT_RIGHT_FLANK, candidate.readContext().rightFlankString())
                .attribute(READ_CONTEXT_INDEX, readContext.readBasesPositionIndex())
                .attribute(READ_CONTEXT_EVENTS, candidate.minNumberOfEvents())
                .computeEndFromAlleles(alleles, (int) candidate.position())
                .alleles(alleles);

        if (!readContext.microhomology().isEmpty()) {
            builder.attribute(READ_CONTEXT_MICRO_HOMOLOGY, readContext.microhomology());
        }

        if (readContext.repeatCount() > 0) {
            builder.attribute(READ_CONTEXT_REPEAT_COUNT, readContext.repeatCount())
                    .attribute(READ_CONTEXT_REPEAT_SEQUENCE, readContext.repeat());
        }

        return builder;
    }

    @NotNull
    private static List<Allele> createAlleles(@NotNull final VariantHotspot variant) {
        final Allele ref = Allele.create(variant.ref(), true);
        final Allele alt = Allele.create(variant.alt(), false);
        return Lists.newArrayList(ref, alt);
    }

}
