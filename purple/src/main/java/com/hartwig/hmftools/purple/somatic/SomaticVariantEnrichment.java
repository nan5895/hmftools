package com.hartwig.hmftools.purple.somatic;

import static com.hartwig.hmftools.purple.config.PurpleConstants.CLONALITY_BIN_WIDTH;

import java.util.List;

import com.hartwig.hmftools.common.variant.SageVcfTags;
import com.hartwig.hmftools.purple.purity.PurityAdjuster;
import com.hartwig.hmftools.common.purple.PurpleCopyNumber;
import com.hartwig.hmftools.purple.region.ObservedRegion;
import com.hartwig.hmftools.purple.config.ReferenceData;
import com.hartwig.hmftools.purple.fitting.PeakModel;
import com.hartwig.hmftools.common.variant.enrich.SomaticRefContextEnrichment;

import htsjdk.variant.vcf.VCFHeader;

public class SomaticVariantEnrichment
{
    private final SomaticPurityEnrichment mPurityEnrichment;
    private final KataegisEnrichment mKataegisEnrichment;
    private final SomaticRefContextEnrichment mSomaticRefContextEnrichment;
    private final SubclonalLikelihoodEnrichment mSubclonalLikelihoodEnrichment;
    private final SomaticGenotypeEnrichment mGenotypeEnrichment;

    public SomaticVariantEnrichment(
            final String purpleVersion, final String referenceId, final String tumorSample, final ReferenceData refData,
            final PurityAdjuster purityAdjuster, final List<PurpleCopyNumber> copyNumbers, final List<ObservedRegion> fittedRegions,
            final List<PeakModel> peakModel)
    {
        mGenotypeEnrichment = new SomaticGenotypeEnrichment(referenceId, tumorSample);

        mSubclonalLikelihoodEnrichment = new SubclonalLikelihoodEnrichment(CLONALITY_BIN_WIDTH, peakModel);

        mPurityEnrichment = new SomaticPurityEnrichment(purpleVersion, purityAdjuster, copyNumbers, fittedRegions);

        mKataegisEnrichment = new KataegisEnrichment();

        mSomaticRefContextEnrichment = new SomaticRefContextEnrichment(refData.RefGenome, null);
    }

    public void enrich(final SomaticVariant variant)
    {
        mSomaticRefContextEnrichment.processVariant(variant.context());

        mKataegisEnrichment.processVariant(variant);

        mSubclonalLikelihoodEnrichment.processVariant(variant);

        mGenotypeEnrichment.processVariant(variant);
    }

    public void flush()
    {
        mKataegisEnrichment.flush();
    }

    public VCFHeader populateHeader(final VCFHeader template)
    {
        VCFHeader header = SageVcfTags.addRefContextHeader(template);
        header = KataegisEnrichment.enrichHeader(header);
        header = SubclonalLikelihoodEnrichment.enrichHeader(header);
        header = HotspotEnrichment.enrichHeader(header);
        return mPurityEnrichment.enrichHeader(header);
    }

}
