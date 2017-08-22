package com.hartwig.hmftools.patientreporter.algo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.hartwig.hmftools.common.copynumber.CopyNumber;
import com.hartwig.hmftools.common.ecrf.CpctEcrfModel;
import com.hartwig.hmftools.common.exception.HartwigException;
import com.hartwig.hmftools.common.gene.GeneCopyNumber;
import com.hartwig.hmftools.common.lims.LimsModel;
import com.hartwig.hmftools.common.numeric.Doubles;
import com.hartwig.hmftools.common.purple.copynumber.PurpleCopyNumber;
import com.hartwig.hmftools.common.purple.purity.FittedPurity;
import com.hartwig.hmftools.common.purple.purity.FittedPurityScore;
import com.hartwig.hmftools.common.purple.purity.FittedPurityStatus;
import com.hartwig.hmftools.common.purple.purity.PurityContext;
import com.hartwig.hmftools.common.variant.structural.StructuralVariant;
import com.hartwig.hmftools.common.variant.structural.StructuralVariantFileLoader;
import com.hartwig.hmftools.common.variant.vcf.VCFSomaticFile;
import com.hartwig.hmftools.patientreporter.PatientReport;
import com.hartwig.hmftools.patientreporter.copynumber.CopyNumberAnalysis;
import com.hartwig.hmftools.patientreporter.copynumber.FreecCopyNumberAnalyzer;
import com.hartwig.hmftools.patientreporter.purple.ImmutablePurpleAnalysis;
import com.hartwig.hmftools.patientreporter.purple.PurpleAnalysis;
import com.hartwig.hmftools.patientreporter.util.PatientReportFormat;
import com.hartwig.hmftools.patientreporter.variants.StructuralVariantAnalysis;
import com.hartwig.hmftools.patientreporter.variants.StructuralVariantAnalyzer;
import com.hartwig.hmftools.patientreporter.variants.VariantAnalysis;
import com.hartwig.hmftools.patientreporter.variants.VariantAnalyzer;
import com.hartwig.hmftools.patientreporter.variants.VariantReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PatientReporter {
    private static final Logger LOGGER = LogManager.getLogger(PatientReporter.class);

    @NotNull
    private final CpctEcrfModel cpctEcrfModel;
    @NotNull
    private final LimsModel limsModel;
    @NotNull
    private final VariantAnalyzer variantAnalyzer;
    @NotNull
    private final StructuralVariantAnalyzer structuralVariantAnalyzer;
    @NotNull
    private final FreecCopyNumberAnalyzer copyNumberAnalyzer;
    private final boolean useFreec;
    private final boolean doSV;

    public PatientReporter(@NotNull final CpctEcrfModel cpctEcrfModel, @NotNull final LimsModel limsModel,
            @NotNull final VariantAnalyzer variantAnalyzer, @NotNull final StructuralVariantAnalyzer structuralVariantAnalyzer,
            @NotNull final FreecCopyNumberAnalyzer copyNumberAnalyzer, final boolean useFreec, final boolean doSV) {
        this.cpctEcrfModel = cpctEcrfModel;
        this.limsModel = limsModel;
        this.variantAnalyzer = variantAnalyzer;
        this.structuralVariantAnalyzer = structuralVariantAnalyzer;
        this.copyNumberAnalyzer = copyNumberAnalyzer;
        this.useFreec = useFreec;
        this.doSV = doSV;
    }

    @NotNull
    public PatientReport run(@NotNull final String runDirectory) throws IOException, HartwigException {
        final GenomeAnalysis genomeAnalysis = analyseGenomeData(runDirectory);

        final String sample = genomeAnalysis.sample();
        final VariantAnalysis variantAnalysis = genomeAnalysis.variantAnalysis();
        final CopyNumberAnalysis copyNumberAnalysis = genomeAnalysis.copyNumberAnalysis();
        final PurpleAnalysis purpleAnalysis = genomeAnalysis.purpleAnalysis();
        final StructuralVariantAnalysis svAnalysis = genomeAnalysis.structuralVariantAnalysis();

        final int passedCount = variantAnalysis.passedVariants().size();
        final int consensusPassedCount = variantAnalysis.consensusPassedVariants().size();
        final int mutationalLoad = variantAnalysis.mutationalLoad();
        final int consequentialVariantCount = variantAnalysis.consequentialVariants().size();
        final int potentialMNVCount = variantAnalysis.potentialConsequentialMNVs().size();
        final int svCount = svAnalysis.getAnnotations().size();

        LOGGER.info(" Printing analysis results:");
        LOGGER.info("  Number of variants after applying pass-only filter : " + Integer.toString(passedCount));
        LOGGER.info("  Number of variants after applying consensus rule : " + Integer.toString(consensusPassedCount));
        LOGGER.info("  Number of missense variants in consensus rule (mutational load) : " + Integer.toString(mutationalLoad));
        LOGGER.info("  Number of consequential variants to report : " + Integer.toString(consequentialVariantCount));
        LOGGER.info("  Number of potential consequential MNVs : " + Integer.toString(potentialMNVCount));
        if (potentialMNVCount > 0) {
            LOGGER.warn(" !! Non-zero number of potentials MNV ");
            LOGGER.warn(variantAnalysis.potentialConsequentialMNVs());
        }
        LOGGER.info("  Determined copy number stats for " + Integer.toString(copyNumberAnalysis.genePanelSize()) + " genes which led to "
                + Integer.toString(copyNumberAnalysis.findings().size()) + " findings.");
        LOGGER.info("  Number of raw structural variants : " + Integer.toString(svCount));

        final String tumorType = PatientReporterHelper.extractTumorType(cpctEcrfModel, sample);
        final Double tumorPercentage = limsModel.findTumorPercentageForSample(sample);
        final List<VariantReport> purpleEnrichedVariants = purpleAnalysis.enrich(variantAnalysis.findings());
        return new PatientReport(sample, purpleEnrichedVariants, svAnalysis.getAnnotations(), copyNumberAnalysis.findings(), mutationalLoad,
                tumorType, tumorPercentage, purpleAnalysis.fittedPurity());
    }

    @NotNull
    private GenomeAnalysis analyseGenomeData(@NotNull final String runDirectory) throws IOException, HartwigException {
        LOGGER.info(" Loading somatic variants...");
        final VCFSomaticFile variantFile = PatientReporterHelper.loadVariantFile(runDirectory);
        final String sample = variantFile.sample();
        LOGGER.info("  " + variantFile.variants().size() + " somatic variants loaded for sample " + sample);

        LOGGER.info(" Loading purity numbers...");
        final PurityContext context = PatientReporterHelper.loadPurity(runDirectory, sample);
        if (context.status().equals(FittedPurityStatus.NO_TUMOR)) {
            LOGGER.warn("PURPLE DID NOT DETECT A TUMOR. Proceed with utmost caution!");
        }

        final FittedPurity purity = context.bestFit();
        final FittedPurityScore purityScore = context.score();
        final List<PurpleCopyNumber> purpleCopyNumbers = PatientReporterHelper.loadPurpleCopyNumbers(runDirectory, sample);
        final List<GeneCopyNumber> geneCopyNumbers = PatientReporterHelper.loadPurpleGeneCopyNumbers(runDirectory, sample);
        LOGGER.info("  " + purpleCopyNumbers.size() + " purple copy number regions loaded for sample " + sample);
        final PurpleAnalysis purpleAnalysis = ImmutablePurpleAnalysis.of(purity, purityScore, purpleCopyNumbers, geneCopyNumbers);
        if (Doubles.greaterThan(purpleAnalysis.purityUncertainty(), 0.05)) {
            LOGGER.warn("Purity uncertainty (" + PatientReportFormat.formatPercent(purpleAnalysis.purityUncertainty())
                    + ") range exceeds 5%. Proceed with caution.");
        }

        final CopyNumberAnalysis copyNumberAnalysis;
        if (useFreec) {
            LOGGER.info(" Loading freec somatic copy numbers...");
            final List<CopyNumber> copyNumbers = PatientReporterHelper.loadFreecCopyNumbers(runDirectory, sample);
            LOGGER.info("  " + copyNumbers.size() + " freec copy number regions loaded for sample " + sample);
            LOGGER.info(" Analyzing freec somatic copy numbers...");
            copyNumberAnalysis = copyNumberAnalyzer.run(copyNumbers);
        } else {
            LOGGER.info(" Analyzing purple somatic copy numbers...");
            copyNumberAnalysis = purpleAnalysis.copyNumberAnalysis();
        }

        LOGGER.info(" Analyzing somatics....");
        final VariantAnalysis variantAnalysis = variantAnalyzer.run(variantFile.variants());

        final Path mantaVcfPath = PatientReporterHelper.findMantaVCF(runDirectory);
        final StructuralVariantAnalysis svAnalysis;
        if (doSV && mantaVcfPath != null) {
            LOGGER.info("Loading structural variants...");
            final List<StructuralVariant> structuralVariants = StructuralVariantFileLoader.fromFile(mantaVcfPath.toString());
            LOGGER.info("Annotating structural variants...");
            svAnalysis = structuralVariantAnalyzer.run(structuralVariants);
        } else {
            if (doSV) {
                LOGGER.warn("Could not find Manta VCF!");
            }
            svAnalysis = new StructuralVariantAnalysis(Collections.emptyList());
        }

        return new GenomeAnalysis(sample, variantAnalysis, copyNumberAnalysis, purpleAnalysis, svAnalysis);
    }
}
