package com.hartwig.hmftools.protect.cna;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.purple.copynumber.CopyNumberInterpretation;
import com.hartwig.hmftools.common.purple.copynumber.ReportableGainLoss;
import com.hartwig.hmftools.protect.serve.ServeEvidenceItem;
import com.hartwig.hmftools.protect.serve.ServeEvidenceItemFactory;
import com.hartwig.hmftools.serve.actionability.gene.ActionableGene;

import org.jetbrains.annotations.NotNull;

public class CopyNumberEvidence {

    private final List<ActionableGene> actionableGenes;

    public CopyNumberEvidence(final List<ActionableGene> actionableGenes) {
        this.actionableGenes = actionableGenes;
    }

    @NotNull
    public List<ServeEvidenceItem> evidence(@NotNull Set<String> doid, @NotNull List<ReportableGainLoss> reportables) {
        List<ServeEvidenceItem> result = Lists.newArrayList();
        for (ReportableGainLoss reportable : reportables) {
            result.addAll(evidence(doid, reportable));
        }
        return result;
    }

    @NotNull
    public List<ServeEvidenceItem> evidence(@NotNull Set<String> doid, @NotNull ReportableGainLoss reportable) {
        List<ServeEvidenceItem> result = Lists.newArrayList();
        for (ActionableGene actionable : actionableGenes) {
            if (actionable.gene().equals(reportable.gene()) && isTypeMatch(actionable, reportable)) {
                ServeEvidenceItem evidence = ServeEvidenceItemFactory.create(actionable.genomicEvent(), doid, actionable);
                result.add(evidence);
            }
        }
        return ServeEvidenceItemFactory.doNotReportInsignificantEvidence(result);
    }

    private boolean isTypeMatch(@NotNull ActionableGene actionable, @NotNull ReportableGainLoss reportable) {
        switch (actionable.event()) {
            case AMPLIFICATION:
                return reportable.interpretation().equals(CopyNumberInterpretation.GAIN);
            case DELETION:
                return !reportable.interpretation().equals(CopyNumberInterpretation.GAIN);
            default:
                return false;
        }
    }

}
