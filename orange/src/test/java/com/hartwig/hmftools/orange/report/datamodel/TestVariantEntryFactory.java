package com.hartwig.hmftools.orange.report.datamodel;

import com.hartwig.hmftools.common.genotype.GenotypeStatus;
import com.hartwig.hmftools.common.variant.Hotspot;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class TestVariantEntryFactory {

    private TestVariantEntryFactory() {
    }

    @NotNull
    public static ImmutableVariantEntry.Builder builder() {
        return ImmutableVariantEntry.builder()
                .gene(Strings.EMPTY)
                .isCanonical(false)
                .affectedCodon(null)
                .impact(Strings.EMPTY)
                .variantCopyNumber(0D)
                .totalCopyNumber(0D)
                .minorAlleleCopyNumber(0D)
                .biallelic(false)
                .hotspot(Hotspot.NON_HOTSPOT)
                .driverLikelihood(null)
                .clonalLikelihood(0D)
                .localPhaseSets(null)
                .rnaDepth(null)
                .genotypeStatus(GenotypeStatus.UNKNOWN);
    }
}
