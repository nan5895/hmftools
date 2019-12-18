package com.hartwig.hmftools.patientreporter.copynumber;

import java.util.List;
import java.util.Map;

import com.hartwig.hmftools.common.actionability.ActionabilityAnalyzer;
import com.hartwig.hmftools.common.actionability.EvidenceItem;
import com.hartwig.hmftools.common.ecrf.projections.PatientTumorLocation;
import com.hartwig.hmftools.common.purple.CheckPurpleQuality;
import com.hartwig.hmftools.common.purple.gene.GeneCopyNumber;
import com.hartwig.hmftools.common.purple.purity.FittedPurity;
import com.hartwig.hmftools.common.purple.purity.PurityContext;
import com.hartwig.hmftools.common.purple.qc.PurpleQC;
import com.hartwig.hmftools.patientreporter.actionability.ReportableEvidenceItemFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CopyNumberAnalyzer {

    private CopyNumberAnalyzer() {
    }

    @NotNull
    public static CopyNumberAnalysis run(@NotNull PurityContext purityContext, @NotNull PurpleQC purpleQC,
            @NotNull List<GeneCopyNumber> exomeGeneCopyNumbers, @NotNull ActionabilityAnalyzer actionabilityAnalyzer,
            @Nullable PatientTumorLocation patientTumorLocation) {
        FittedPurity bestFit = purityContext.bestFit();
        List<ReportableGainLoss> reportableGainsAndLosses =
                ExtractReportableGainsAndLosses.toReportableGainsAndLosses(exomeGeneCopyNumbers, bestFit.ploidy());
        String primaryTumorLocation = patientTumorLocation != null ? patientTumorLocation.primaryTumorLocation() : null;
        Map<GeneCopyNumber, List<EvidenceItem>> evidencePerGeneCopyNumber =
                actionabilityAnalyzer.evidenceForCopyNumbers(exomeGeneCopyNumbers, primaryTumorLocation, bestFit.ploidy());

        Map<GeneCopyNumber, List<EvidenceItem>> filteredEvidenceItemMap =
                CheckEvidenceCnv.checkAndFilterForEvidenceInDriverCatalog(reportableGainsAndLosses, evidencePerGeneCopyNumber);

        List<EvidenceItem> filteredEvidenceItem = ReportableEvidenceItemFactory.toReportableFlatList(filteredEvidenceItemMap);

        return ImmutableCopyNumberAnalysis.builder()
                .purity(bestFit.purity())
                .hasReliablePurity(CheckPurpleQuality.checkHasReliablePurity(purityContext))
                .hasReliableQuality(CheckPurpleQuality.checkHasReliableQuality(purpleQC))
                .ploidy(bestFit.ploidy())
                .exomeGeneCopyNumbers(exomeGeneCopyNumbers)
                .reportableGainsAndLosses(reportableGainsAndLosses)
                .evidenceItems(filteredEvidenceItem)
                .build();
    }
}
