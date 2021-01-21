package com.hartwig.hmftools.ckb.therapy;

import java.util.List;

import org.immutables.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Value.Style(passAnnotations = { NotNull.class, Nullable.class })
public abstract class TherapyEvidence {

    @NotNull
    public abstract String id();

    @NotNull
    public abstract String approvalStatus();

    @NotNull
    public abstract String evidenceType();

    @NotNull
    public abstract String efficacyEvidence();

    @NotNull
    public abstract TherapyMolecularProfile molecularProfile();

    @NotNull
    public abstract TherapyTherapy therapy();

    @NotNull
    public abstract TherapyIndication indication();

    @NotNull
    public abstract String responseType();

    @NotNull
    public abstract List<TherapyReference> reference();

    @NotNull
    public abstract String ampCapAscoEvidenceLevel();

    @NotNull
    public abstract String ampCapAscoInferredTier();
}
