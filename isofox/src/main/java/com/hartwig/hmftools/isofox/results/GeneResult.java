package com.hartwig.hmftools.isofox.results;

import static com.hartwig.hmftools.isofox.common.GeneMatchType.ALT;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.CHIMERIC;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.DUPLICATE;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.READ_THROUGH;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.TOTAL;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.TRANS_SUPPORTING;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.UNSPLICED;
import static com.hartwig.hmftools.isofox.common.GeneMatchType.typeAsInt;
import static com.hartwig.hmftools.isofox.results.ResultsWriter.DELIMITER;
import static com.hartwig.hmftools.isofox.results.ResultsWriter.FLD_GENE_ID;
import static com.hartwig.hmftools.isofox.results.ResultsWriter.FLD_GENE_NAME;
import static com.hartwig.hmftools.isofox.results.ResultsWriter.FLD_TRANS_ID;
import static com.hartwig.hmftools.isofox.results.ResultsWriter.FLD_TRANS_NAME;

import java.util.StringJoiner;

import com.hartwig.hmftools.common.ensemblcache.EnsemblGeneData;
import com.hartwig.hmftools.isofox.common.GeneCollection;
import com.hartwig.hmftools.isofox.common.GeneReadData;

import org.immutables.value.Value;

@Value.Immutable
public abstract class GeneResult
{
    public abstract EnsemblGeneData geneData();
    public abstract String collectionId();
    public abstract int intronicLength();
    public abstract int transCount();
    public abstract int totalFragments();
    public abstract int supportingTrans();
    public abstract int altFragments();
    public abstract int unsplicedFragments();
    public abstract int readThroughFragments();
    public abstract int chimericFragments();
    public abstract int duplicates();

    private double mUnsplicedAlloc;
    private double mFitResiduals;
    private double mMedianExpectedGcRatio;
    private double mMedianActualGcRatio;

    public static GeneResult createGeneResults(final GeneCollection geneCollection, final GeneReadData geneReadData)
    {
        final EnsemblGeneData geneData = geneReadData.GeneData;

        long geneLength = geneData.length();
        long exonicLength = geneReadData.calcExonicRegionLength();

        final int[] fragmentCounts = geneReadData.getCounts();

        GeneResult result = ImmutableGeneResult.builder()
                .geneData(geneData)
                .collectionId(geneCollection.chrId())
                .intronicLength((int)(geneLength - exonicLength))
                .transCount(geneReadData.getTranscripts().size())
                .totalFragments(fragmentCounts[typeAsInt(TOTAL)])
                .supportingTrans(fragmentCounts[typeAsInt(TRANS_SUPPORTING)])
                .altFragments(fragmentCounts[typeAsInt(ALT)])
                .unsplicedFragments(fragmentCounts[typeAsInt(UNSPLICED)])
                .readThroughFragments(fragmentCounts[typeAsInt(READ_THROUGH)])
                .chimericFragments(fragmentCounts[typeAsInt(CHIMERIC)])
                .duplicates(fragmentCounts[typeAsInt(DUPLICATE)])
                .build();

        result.setFitResiduals(0);
        result.setUnsplicedAllocation(0);
        result.setMedianExpectedGcRatio(0);
        result.setMedianActualGcRatio(0);
        return result;
    }

    public double getUnsplicedAlloc() { return mUnsplicedAlloc; }
    public void setUnsplicedAllocation(double unsplicedAlloc) { mUnsplicedAlloc = unsplicedAlloc; }

    public void setFitResiduals(double residuals) { mFitResiduals = residuals; }
    public double getFitResiduals() { return mFitResiduals; }

    public void setMedianExpectedGcRatio(double median) { mMedianExpectedGcRatio = median; }
    public void setMedianActualGcRatio(double median) { mMedianActualGcRatio = median; }

    public static String csvHeader()
    {
        return new StringJoiner(DELIMITER)
                .add(FLD_GENE_ID)
                .add(FLD_GENE_NAME)
                .add("Chromosome").add("GeneLength").add("IntronicLength").add("TransCount")
                .add("TotalFragments").add("SupportingTrans").add("Alt").add("Unspliced").add("ReadThrough")
                .add("Chimeric").add("Duplicates")
                .add("UnsplicedAlloc").add("FitResiduals")
                .add("MedianExpectedGcRatio").add("MedianActualGcRatio")
                .add("GeneSet")
                .toString();
    }

    public String toCsv()
    {
        return new StringJoiner(DELIMITER)
                .add(geneData().GeneId)
                .add(geneData().GeneName)
                .add(geneData().Chromosome)
                .add(String.valueOf(geneData().length()))
                .add(String.valueOf(intronicLength()))
                .add(String.valueOf(transCount()))
                .add(String.valueOf(totalFragments()))
                .add(String.valueOf(supportingTrans()))
                .add(String.valueOf(altFragments()))
                .add(String.valueOf(unsplicedFragments()))
                .add(String.valueOf(readThroughFragments()))
                .add(String.valueOf(chimericFragments()))
                .add(String.valueOf(duplicates()))
                .add(String.format("%.1f", getUnsplicedAlloc()))
                .add(String.format("%.1f", getFitResiduals()))
                .add(String.format("%.2f", mMedianExpectedGcRatio))
                .add(String.format("%.2f", mMedianActualGcRatio))
                .add(collectionId())
                .toString();
    }
}
