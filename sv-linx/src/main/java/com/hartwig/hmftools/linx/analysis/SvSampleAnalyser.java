package com.hartwig.hmftools.linx.analysis;

import static com.hartwig.hmftools.common.io.FileWriterUtils.closeBufferedWriter;
import static com.hartwig.hmftools.common.io.FileWriterUtils.createBufferedWriter;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.BND;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DEL;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DUP;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.INS;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.INV;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.SGL;
import static com.hartwig.hmftools.linx.analysis.SvClassification.getSuperType;
import static com.hartwig.hmftools.linx.analysis.SvUtilities.CHROMOSOME_ARM_P;
import static com.hartwig.hmftools.linx.analysis.SvUtilities.appendStr;
import static com.hartwig.hmftools.linx.analysis.SvUtilities.formatPloidy;
import static com.hartwig.hmftools.linx.analysis.SvUtilities.getChromosomalArm;
import static com.hartwig.hmftools.linx.annotators.ViralInsertAnnotator.VH_ID;
import static com.hartwig.hmftools.linx.annotators.ViralInsertAnnotator.VH_NAME;
import static com.hartwig.hmftools.linx.gene.SvGeneTranscriptCollection.PSEUDO_GENE_DATA_EXON_RANK;
import static com.hartwig.hmftools.linx.gene.SvGeneTranscriptCollection.PSEUDO_GENE_DATA_TRANS_ID;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_COMPLEX_FOLDBACK;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_COMPLEX_LINE;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_COMPLEX_OTHER;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_DSB;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_FOLDBACK;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_FOLDBACK_DSB;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_ISOLATED_BE;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_SIMPLE_DUP;
import static com.hartwig.hmftools.linx.types.SvArmCluster.ARM_CL_TI_ONLY;
import static com.hartwig.hmftools.linx.types.SvArmCluster.getArmClusterData;
import static com.hartwig.hmftools.linx.types.SvVarData.SE_END;
import static com.hartwig.hmftools.linx.types.SvVarData.SE_START;
import static com.hartwig.hmftools.linx.types.SvVarData.isStart;
import static com.hartwig.hmftools.linx.types.SvaConstants.NO_DB_MARKER;
import static com.hartwig.hmftools.linx.types.SvaConstants.SHORT_TI_LENGTH;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.utils.PerformanceCounter;
import com.hartwig.hmftools.common.variant.structural.StructuralVariantData;
import com.hartwig.hmftools.common.variant.structural.annotation.GeneAnnotation;
import com.hartwig.hmftools.common.variant.structural.linx.ImmutableLinxCluster;
import com.hartwig.hmftools.common.variant.structural.linx.ImmutableLinxLink;
import com.hartwig.hmftools.common.variant.structural.linx.ImmutableLinxSvData;
import com.hartwig.hmftools.common.variant.structural.linx.LinxCluster;
import com.hartwig.hmftools.common.variant.structural.linx.LinxClusterFile;
import com.hartwig.hmftools.common.variant.structural.linx.LinxLink;
import com.hartwig.hmftools.common.variant.structural.linx.LinxLinkFile;
import com.hartwig.hmftools.common.variant.structural.linx.LinxSvData;
import com.hartwig.hmftools.common.variant.structural.linx.LinxSvDataFile;
import com.hartwig.hmftools.common.variant.structural.linx.LinxViralInsertFile;
import com.hartwig.hmftools.linx.annotators.FragileSiteAnnotator;
import com.hartwig.hmftools.linx.annotators.LineElementAnnotator;
import com.hartwig.hmftools.linx.annotators.ReplicationOriginAnnotator;
import com.hartwig.hmftools.linx.annotators.ViralInsertAnnotator;
import com.hartwig.hmftools.linx.chaining.ChainMetrics;
import com.hartwig.hmftools.linx.chaining.LinkFinder;
import com.hartwig.hmftools.linx.cn.CnDataLoader;
import com.hartwig.hmftools.linx.cn.PloidyCalcData;
import com.hartwig.hmftools.linx.cn.SvCNData;
import com.hartwig.hmftools.linx.gene.SvGeneTranscriptCollection;
import com.hartwig.hmftools.linx.types.ResolvedType;
import com.hartwig.hmftools.linx.types.SvArmCluster;
import com.hartwig.hmftools.linx.types.SvBreakend;
import com.hartwig.hmftools.linx.chaining.SvChain;
import com.hartwig.hmftools.linx.types.SvCluster;
import com.hartwig.hmftools.linx.types.SvLinkedPair;
import com.hartwig.hmftools.linx.types.SvVarData;
import com.hartwig.hmftools.linx.LinxConfig;
import com.hartwig.hmftools.linx.visualiser.file.VisualiserWriter;
import com.hartwig.hmftools.patientdb.dao.DatabaseAccess;
import com.hartwig.hmftools.patientdb.dao.DatabaseUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SvSampleAnalyser {

    private final LinxConfig mConfig;
    private final ClusterAnalyser mAnalyser;

    // data per run (ie sample)
    private String mSampleId;
    private List<SvVarData> mAllVariants; // the original list to analyse

    private BufferedWriter mSvFileWriter;
    private BufferedWriter mClusterFileWriter;
    private BufferedWriter mLinksFileWriter;
    private VisualiserWriter mVisWriter;

    private FragileSiteAnnotator mFragileSiteAnnotator;
    private LineElementAnnotator mLineElementAnnotator;
    private ReplicationOriginAnnotator mReplicationOriginAnnotator;
    private ViralInsertAnnotator mViralInsertAnnotator;
    private SvClusteringMethods mClusteringMethods;
    private CnDataLoader mCnDataLoader;

    private boolean mIsValid;

    private PerformanceCounter mPcPrep;
    private PerformanceCounter mPcClusterAnalyse;
    private PerformanceCounter mPcWrite;

    private static final Logger LOGGER = LogManager.getLogger(SvSampleAnalyser.class);

    public SvSampleAnalyser(final LinxConfig config)
    {
        mConfig = config;
        mSampleId = "";

        mClusteringMethods = new SvClusteringMethods(mConfig.ProximityDistance);
        mAnalyser = new ClusterAnalyser(config, mClusteringMethods);
        mVisWriter = new VisualiserWriter(config.OutputDataPath, config.WriteVisualisationData, config.hasMultipleSamples());

        mAnalyser.setUseAllelePloidies(true);

        mAllVariants = Lists.newArrayList();
        mCnDataLoader = null;

        mIsValid = true;

        mSvFileWriter = null;
        mLinksFileWriter = null;
        mClusterFileWriter = null;

        mFragileSiteAnnotator = new FragileSiteAnnotator();
        mFragileSiteAnnotator.loadFragileSitesFile(mConfig.FragileSiteFile);

        mLineElementAnnotator = new LineElementAnnotator();
        mLineElementAnnotator.loadLineElementsFile(mConfig.LineElementFile);

        mReplicationOriginAnnotator = new ReplicationOriginAnnotator();
        mReplicationOriginAnnotator.loadReplicationOrigins(mConfig.ReplicationOriginsFile);

        mViralInsertAnnotator = new ViralInsertAnnotator();
        mViralInsertAnnotator.loadViralHostData(mConfig.ViralHostsFile);

        mPcPrep = new PerformanceCounter("Preparation");
        mPcClusterAnalyse = new PerformanceCounter("ClusterAndAnalyse");
        mPcWrite = new PerformanceCounter("WriteCSV");

        createOutputFiles();
    }

    public final List<SvVarData> getVariants() { return mAllVariants; }
    public final List<SvCluster> getClusters() { return mAnalyser.getClusters(); }
    public boolean inValidState() { return mIsValid; }
    public final Map<String, List<SvBreakend>> getChrBreakendMap() { return mClusteringMethods.getChrBreakendMap(); }
    public final VisualiserWriter getVisWriter() { return mVisWriter; }

    public void setCnDataLoader(CnDataLoader cnAnalyser)
    {
        mCnDataLoader = cnAnalyser;
        mAnalyser.setCnDataLoader(cnAnalyser);
        mClusteringMethods.setSampleCnEventData(mCnDataLoader.getLohData(), mCnDataLoader.getHomLossData());
        mClusteringMethods.setChrCopyNumberMap(mCnDataLoader.getChrCopyNumberMap());
    }

    public void setGeneCollection(SvGeneTranscriptCollection geneCollection) { mAnalyser.setGeneCollection(geneCollection); }

    private void clearState()
    {
        if(mSampleId.isEmpty())
            return;

        // no longer required since list items are purged by their owner
        // mClusteringMethods.clearLOHBreakendData(mSampleId);

        mSampleId = "";
        mAllVariants.clear();
    }

    public void setSampleSVs(final String sampleId, final List<SvVarData> variants)
    {
        clearState();

        if (variants.isEmpty())
            return;

        mSampleId = sampleId;
        mAllVariants = Lists.newArrayList(variants);
        mVisWriter.setSampleId(sampleId);

        // look-up and cache relevant CN data into each SV
        setSvCopyNumberData(
                mAllVariants,
                mCnDataLoader.getSvPloidyCalcMap(),
                mCnDataLoader.getSvIdCnDataMap(),
                mCnDataLoader.getChrCnDataMap());

        LOGGER.debug("loaded {} SVs", mAllVariants.size());
    }

    public static void setSvCopyNumberData(List<SvVarData> svList, final Map<Integer,PloidyCalcData> svPloidyCalcDataMap,
            final Map<Integer,SvCNData[]> svIdCnDataMap, final Map<String,List<SvCNData>> chrCnDataMap)
    {
        if((svPloidyCalcDataMap == null || svPloidyCalcDataMap.isEmpty()) && svIdCnDataMap.isEmpty())
            return;

        List<SvCNData> cnDataList = null;
        String currentChromosome = "";
        for(final SvVarData var : svList)
        {
            if(svPloidyCalcDataMap != null)
            {
                final PloidyCalcData ploidyData = svPloidyCalcDataMap.get(var.dbId());
                if (ploidyData != null)
                {
                    double estPloidy = ploidyData.PloidyEstimate;
                    double estUncertainty = ploidyData.PloidyUncertainty;
                    var.setPloidyRecalcData(estPloidy - estUncertainty, estPloidy + estUncertainty);
                }
            }

            final SvCNData[] cnDataPair = svIdCnDataMap.get(var.dbId());

            if(cnDataPair == null)
                continue;

            for(int be = SE_START; be <= SE_END; ++be)
            {
                if(var.isNullBreakend() && be == SE_END)
                    continue;

                boolean isStart = isStart(be);

                if(!currentChromosome.equals(var.chromosome(isStart)))
                {
                    currentChromosome = var.chromosome(isStart);
                    cnDataList = chrCnDataMap.get(currentChromosome);
                }

                SvCNData cnDataPost = cnDataPair[be];

                if(cnDataList == null || cnDataPost == null)
                    continue;

                SvCNData cnDataPrev = cnDataList.get(cnDataPost.getIndex() - 1);

                var.setCopyNumberData(isStart, cnDataPrev, cnDataPost);
            }
        }
    }

    public void analyse()
    {
        if(mAllVariants.isEmpty())
            return;

        LOGGER.debug("sample({}) clustering {} variants", mSampleId, mAllVariants.size());

        mPcPrep.start();
        annotateAndFilterVariants();
        mClusteringMethods.populateChromosomeBreakendMap(mAllVariants);
        mClusteringMethods.annotateNearestSvData();
        LinkFinder.findDeletionBridges(mClusteringMethods.getChrBreakendMap());
        mClusteringMethods.setSimpleVariantLengths(mSampleId);
        mReplicationOriginAnnotator.setReplicationOrigins(mClusteringMethods.getChrBreakendMap());
        mPcPrep.stop();

        mPcClusterAnalyse.start();

        mAnalyser.setSampleData(mSampleId, mAllVariants);

        mIsValid = mAnalyser.clusterAndAnalyse();

        mPcClusterAnalyse.stop();
    }

    public void writeOutput(final DatabaseAccess dbAccess)
    {
        // if processing a single sample, write flat-files and optionally load the same data to the DB
        // if running in batch mode, skip flat-file generation and DB load, and instead write verbose batch output files
        if(mConfig.OutputDataPath.isEmpty())
            return;

        mPcWrite.start();

        boolean prepareSampleData = mConfig.isSingleSample() || mConfig.UploadToDB;

        List<LinxSvData> linxSvData = prepareSampleData ? Lists.newArrayList() : null;
        List<LinxCluster> clusterData = prepareSampleData ? Lists.newArrayList() : null;
        List<LinxLink> linksData = prepareSampleData ? Lists.newArrayList() : null;
        List<LinxViralInsertFile> viralInserts = prepareSampleData ? generateViralInserts() : null;

        generateSvDataOutput(linxSvData);
        generateClusterOutput(clusterData);
        generateLinksOutput(linksData);

        mVisWriter.writeOutput(mAnalyser.getClusters(), mAllVariants, mCnDataLoader.getChrCnDataMap());

        if(mConfig.isSingleSample())
        {
            try
            {
                // write per-sample DB-style output
                LinxSvDataFile.write(LinxSvDataFile.generateFilename(mConfig.OutputDataPath, mSampleId), linxSvData);
                LinxClusterFile.write(LinxClusterFile.generateFilename(mConfig.OutputDataPath, mSampleId), clusterData);
                LinxLinkFile.write(LinxLinkFile.generateFilename(mConfig.OutputDataPath, mSampleId), linksData);
                LinxViralInsertFile.write(LinxViralInsertFile.generateFilename(mConfig.OutputDataPath, mSampleId), viralInserts);

            } catch (IOException e)
            {
                LOGGER.error("failed to write sample SV data: {}", e.toString());
            }
        }


        if(mConfig.UploadToDB && dbAccess != null)
        {
            dbAccess.writeSvLinxData(mSampleId, linxSvData);
            dbAccess.writeSvClusters(mSampleId, clusterData);
            dbAccess.writeSvLinks(mSampleId, linksData);
            dbAccess.writeSvViralInserts(mSampleId, viralInserts);
        }

        mPcWrite.stop();
    }

    private void annotateAndFilterVariants()
    {
        int currentIndex = 0;

        while(currentIndex < mAllVariants.size())
        {
            SvVarData var = mAllVariants.get(currentIndex);

            var.setFragileSites(
                    mFragileSiteAnnotator.isFragileSite(var, true),
                    mFragileSiteAnnotator.isFragileSite(var, false));

            var.setLineElement(mLineElementAnnotator.isLineElement(var, true), true);
            var.setLineElement(mLineElementAnnotator.isLineElement(var, false), false);

            String startArm = getChromosomalArm(var.chromosome(true), var.position(true));

            String endArm;
            if(!var.isNullBreakend())
                endArm = getChromosomalArm(var.chromosome(false), var.position(false));
            else
                endArm = CHROMOSOME_ARM_P;

            var.setChromosomalArms(startArm, endArm);

            ++currentIndex;
        }
    }

    public void annotateWithGeneData(SvGeneTranscriptCollection geneTransCache)
    {
        checkPseudoGeneAnnotations(geneTransCache);
    }

    private void checkPseudoGeneAnnotations(SvGeneTranscriptCollection geneTransCache)
    {
        for(final SvCluster cluster : getClusters())
        {
            // isSpecificCluster(cluster);

            GeneAnnotation pseudoGene = null;
            String transcriptId = "";

            for(final SvLinkedPair pair : cluster.getLinkedPairs())
            {
                if(pair.length() > SHORT_TI_LENGTH * 8)
                    continue;

                final SvBreakend lower = pair.getBreakend(true);
                final SvBreakend upper = pair.getBreakend(false);

                // for any TI falling within the same gene, check for an exon boundary match
                if(lower.getSV().getGenesList(lower.usesStart()).isEmpty() || upper.getSV().getGenesList(upper.usesStart()).isEmpty())
                    continue;

                for(final GeneAnnotation gene1 : lower.getSV().getGenesList(lower.usesStart()))
                {
                    for(final GeneAnnotation gene2 : upper.getSV().getGenesList(upper.usesStart()))
                    {
                        if(!gene1.GeneName.equals(gene2.GeneName))
                            continue;

                        final String exonData[] = geneTransCache.getExonDetailsForPosition(gene1, lower.position(), upper.position());

                        if(exonData[PSEUDO_GENE_DATA_TRANS_ID] != null)
                        {
                            pseudoGene = gene1;
                            transcriptId = exonData[PSEUDO_GENE_DATA_TRANS_ID];

                            String exonMatchData = String.format("%s:%s;%s",
                                    gene1.GeneName, transcriptId, exonData[PSEUDO_GENE_DATA_EXON_RANK]);


                            pair.setExonMatchData(exonMatchData);
                        }
                    }
                }
            }

            if(pseudoGene != null)
            {
                mVisWriter.addGeneExonData(cluster.id(), pseudoGene.StableId, pseudoGene.GeneName,
                        transcriptId, 0, pseudoGene.chromosome(), "PSEUDO");
            }
        }
    }

    private void createOutputFiles()
    {
        if(!mConfig.hasMultipleSamples())
            return;

        if(mSvFileWriter != null || mClusterFileWriter != null || mLinksFileWriter != null)
            return;

        // open and write headers for multi-sample output files
        createSvDataFile();
        createClusterFile();
        createLinksFile();
    }

    private void createSvDataFile()
    {
        try
        {
            String outputFileName = mConfig.OutputDataPath + "SVA_SVS.csv";

            mSvFileWriter = createBufferedWriter(outputFileName, false);

            // definitional fields
            mSvFileWriter.write("SampleId,Id,Type,ClusterId,ClusterCount");
            mSvFileWriter.write(",ChrStart,PosStart,OrientStart,ArmStart,ChrEnd,PosEnd,OrientEnd,ArmEnd");

            // position and copy number
            mSvFileWriter.write(",AFStart,CNStart,CNChgStart,AFEnd,CNEnd,CNChgEnd,Ploidy,PloidyMin,PloidyMax");

            // cluster info
            mSvFileWriter.write(",ClusterReason,ClusterDesc,IsResolved,ResolvedType,Consistency,ArmCount");

            // SV info
            mSvFileWriter.write(",Homology,InexactHOStart,InexactHOEnd,InsertSeq,Imprecise,QualScore");
            mSvFileWriter.write(",RefContextStart,RefContextEnd,InsSeqAlignments,Recovered");

            mSvFileWriter.write(",FSStart,FSEnd,LEStart,LEEnd");

            // linked pair info
            mSvFileWriter.write(",LnkSvStart,LnkLenStart,LnkSvEnd,LnkLenEnd");
            mSvFileWriter.write(",AsmbStart,AsmbEnd,AsmbMatchStart,AsmbMatchEnd");

            // chain info
            mSvFileWriter.write(",ChainId,ChainCount,ChainIndex");

            // proximity info and other link info
            mSvFileWriter.write(",NearestLen,NearestType,DBLenStart,DBLenEnd");

            // proximity info and other link info
            mSvFileWriter.write(",FoldbackLnkStart,FoldbackLenStart,FoldbackInfoStart,FoldbackLnkEnd,FoldbackLenEnd,FoldbackInfoEnd");

            // local topology from arm cluster
            mSvFileWriter.write(",LocTopIdStart,LocTopTypeStart,LocTopTIStart,LocTopIdEnd,LocTopTypeEnd,LocTopTIEnd");

            // gene & replication info
            mSvFileWriter.write(",GeneStart,GeneEnd,RepOriginStart,RepOriginEnd");

            // repeat class info
            mSvFileWriter.write(",RepeatClass,RepeatType,VirusName");

            // extra copy number info
            mSvFileWriter.write(",MinorAPStartPrev,MinorAPStartPost,MinorAPEndPrev,MinorAPEndPost");

            mSvFileWriter.newLine();
        }
        catch(IOException e)
        {
            LOGGER.error("failed to open and write output file headers");
        }
    }

    private void generateSvDataOutput(@Nullable  List<LinxSvData> linxSvData)
    {
        try
        {
            for(final SvVarData var : mAllVariants)
            {
                final SvCluster cluster = var.getCluster();

                if(cluster == null)
                {
                    LOGGER.error("SV({}) not assigned to any cluster", var.posId());
                    continue;
                }

                final StructuralVariantData dbData = var.getSvData();

                final SvArmCluster armClusterStart = cluster.findArmCluster(var.getBreakend(true));

                final SvArmCluster armClusterEnd = !var.isNullBreakend() ? cluster.findArmCluster(var.getBreakend(false)) : null;

                if(mSvFileWriter != null)
                {
                    mSvFileWriter.write(String.format("%s,%d,%s,%d,%d",
                            mSampleId, var.dbId(), var.typeStr(), cluster.id(), cluster.getSvCount()));

                    mSvFileWriter.write(String.format(",%s,%d,%d,%s,%s,%d,%d,%s",
                            var.chromosome(true), var.position(true), var.orientation(true), var.arm(true),
                            var.chromosome(false), var.position(false), var.orientation(false), var.arm(false)));

                    mSvFileWriter.write(String.format(",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                            dbData.adjustedStartAF(), dbData.adjustedStartCopyNumber(), dbData.adjustedStartCopyNumberChange(),
                            dbData.adjustedEndAF(), dbData.adjustedEndCopyNumber(), dbData.adjustedEndCopyNumberChange(),
                            dbData.ploidy(), var.ploidyMin(), var.ploidyMax()));

                    mSvFileWriter.write(String.format(",%s,%s,%s,%s,%d,%d",
                            var.getClusterReason(), cluster.getDesc(), cluster.isResolved(), cluster.getResolvedType(),
                            cluster.getConsistencyCount(), cluster.getArmCount()));

                    final String insSeqAlignments = dbData.insertSequenceAlignments().replaceAll(",", ";");

                    mSvFileWriter.write(String.format(",%s,%d,%d,%s,%s,%.0f,%s,%s,%s,%s",
                            dbData.insertSequence().isEmpty() && var.type() != INS ? dbData.startHomologySequence() : "",
                            dbData.inexactHomologyOffsetStart(), dbData.inexactHomologyOffsetEnd(),
                            dbData.insertSequence(), dbData.imprecise(), dbData.qualityScore(),
                            dbData.startRefContext(), dbData.endRefContext(), insSeqAlignments,
                            dbData.recovered()));

                    mSvFileWriter.write(String.format(",%s,%s,%s,%s",
                            var.isFragileSite(true), var.isFragileSite(false),
                            var.getLineElement(true), var.getLineElement(false)));

                    // linked pair info
                    for (int be = SE_START; be <= SE_END; ++be)
                    {
                        boolean isStart = isStart(be);
                        final SvLinkedPair link = var.getLinkedPair(isStart);
                        if (link != null)
                        {
                            mSvFileWriter.write(String.format(",%s,%d",
                                    link.first().equals(var, true) ? link.second().origId() : link.first().origId(), link.length()));
                        }
                        else
                        {
                            mSvFileWriter.write(",,-1");
                        }
                    }

                    // assembly info
                    mSvFileWriter.write(String.format(",%s,%s,%s,%s",
                            var.getAssemblyData(true), var.getAssemblyData(false),
                            var.getAssemblyMatchType(true), var.getAssemblyMatchType(false)));

                    // chain info
                    final SvChain chain = cluster.findChain(var);
                    String chainStr = "";

                    if (chain != null)
                    {
                        chainStr = String.format(",%d,%d,%s", chain.id(), chain.getSvCount(), chain.getSvIndices(var));
                    }
                    else
                    {
                        chainStr = String.format(",%d,0,", cluster.getChainId(var));
                    }

                    mSvFileWriter.write(chainStr);

                    int dbLenStart = var.getDBLink(true) != null ? var.getDBLink(true).length() : NO_DB_MARKER;
                    int dbLenEnd = var.getDBLink(false) != null ? var.getDBLink(false).length() : NO_DB_MARKER;

                    mSvFileWriter.write(String.format(",%d,%s,%d,%d",
                            var.getNearestSvDistance(), var.getNearestSvRelation(), dbLenStart, dbLenEnd));

                    mSvFileWriter.write(String.format(",%s,%d,%s,%s,%d,%s",
                            var.getFoldbackLink(true), var.getFoldbackLength(true), var.getFoldbackInfo(true),
                            var.getFoldbackLink(false), var.getFoldbackLength(false), var.getFoldbackInfo(false)));

                    for (int be = SE_START; be <= SE_END; ++be)
                    {
                        SvArmCluster armCluster = be == SE_START ? armClusterStart : armClusterEnd;

                        if (armCluster != null)
                            mSvFileWriter.write(String.format(",%d,%s,%d", armCluster.id(), armCluster.getTypeStr(), armCluster.getTICount()));
                        else
                            mSvFileWriter.write(",-1,,0");
                    }

                    mSvFileWriter.write(String.format(",%s,%s,%.4f,%.4f",
                            var.getGeneInBreakend(true, true), var.getGeneInBreakend(false, true),
                            var.getReplicationOrigin(true), var.getReplicationOrigin(false)));

                    String virusName = "";

                    if(mViralInsertAnnotator != null)
                    {
                        final String[] viralInsertData = mViralInsertAnnotator.matchesViralInsert(var);

                        if(viralInsertData != null)
                            virusName = viralInsertData[VH_NAME];
                    }

                    mSvFileWriter.write(String.format(",%s,%s,%s",
                            dbData.insertSequenceRepeatClass(), dbData.insertSequenceRepeatType(), virusName));

                    mSvFileWriter.write(String.format(",%.2f,%,2f,%,2f,%,2f",
                            var.getBreakend(true).minorAllelePloidy(true),
                            var.getBreakend(true).minorAllelePloidy(false),
                            !var.isNullBreakend() ? var.getBreakend(false).minorAllelePloidy(true) : 0,
                            !var.isNullBreakend() ? var.getBreakend(false).minorAllelePloidy(false) : 0));

                    mSvFileWriter.newLine();
                }

                if(linxSvData != null)
                {
                    linxSvData.add(ImmutableLinxSvData.builder()
                            .svId(var.dbId())
                            .clusterId(cluster.id())
                            .clusterReason(var.getClusterReason())
                            .fragileSiteStart(var.isFragileSite(true))
                            .fragileSiteEnd(var.isFragileSite(false))
                            .isFoldback(var.isFoldback())
                            .lineTypeStart(var.getLineElement(true))
                            .lineTypeEnd(var.getLineElement(false))
                            .ploidyMin(var.ploidyMin())
                            .ploidyMax(var.ploidyMax())
                            .geneStart(var.getGeneInBreakend(true, false))
                            .geneEnd(var.getGeneInBreakend(true, false))
                            .replicationTimingStart(var.getReplicationOrigin(true))
                            .replicationTimingEnd(var.getReplicationOrigin(false))
                            .localTopologyIdStart(armClusterStart.id())
                            .localTopologyIdEnd(armClusterEnd != null ? armClusterEnd.id() : -1)
                            .localTopologyStart(armClusterStart.getTypeStr())
                            .localTopologyEnd(armClusterEnd != null ? armClusterEnd.getTypeStr() : "")
                            .localTICountStart(armClusterStart.getTICount())
                            .localTICountEnd(armClusterEnd != null ? armClusterEnd.getTICount() : 0)
                            .build());
                }
            }
        }
        catch (final IOException e)
        {
            LOGGER.error("error writing to outputFile: {}", e.toString());
        }
    }

    private void createClusterFile()
    {
        try
        {
            String outputFileName = mConfig.OutputDataPath + "SVA_CLUSTERS.csv";

            mClusterFileWriter = createBufferedWriter(outputFileName, false);

            mClusterFileWriter.write("SampleId,ClusterId,ClusterDesc,ClusterCount,SuperType,ResolvedType,Synthetic,Subclonal,FullyChained,ChainCount");
            mClusterFileWriter.write(",DelCount,DupCount,InsCount,InvCount,BndCount,SglCount,InfCount");
            mClusterFileWriter.write(",ClusterReasons,Consistency,ArmCount,IsLINE,Replication,MinPloidy,MaxPloidy,Foldbacks");
            mClusterFileWriter.write(",TotalTIs,AssemblyTIs,ShortTIs,IntTIs,ExtTIs,IntShortTIs,ExtShortTIs,IntTIsCnGain,ExtTIsCnGain,OverlapTIs");
            mClusterFileWriter.write(",DSBs,ShortDSBs,ChainEndsFace,ChainEndsAway,SyntheticLen,SyntheticTILen");
            mClusterFileWriter.write(",OriginArms,FragmentArms,UnchainedSVs,Annotations,AlleleValidPerc");
            mClusterFileWriter.write(",ArmClusterCount,AcTotalTIs,AcIsolatedBE,AcTIOnly,AcDsb,AcSimpleDup");
            mClusterFileWriter.write(",AcSingleFb,AcFbDsb,AcComplexFb,AcComplexLine,AcComplexOther");
            mClusterFileWriter.newLine();
        }
        catch (final IOException e)
        {
            LOGGER.error("error writing cluster-data to outputFile: {}", e.toString());
        }

    }

    private void generateClusterOutput(@Nullable List<LinxCluster> clusterData)
    {
        try
        {
            for(final SvCluster cluster : getClusters())
            {
                int clusterSvCount = cluster.getSvCount();

                // isSpecificCluster(cluster);

                ResolvedType resolvedType = cluster.getResolvedType();

                int inferredCount = cluster.getInferredTypeCount();
                int sglCount = cluster.getTypeCount(SGL);

                final String superType = getSuperType(cluster);

                if(mClusterFileWriter != null)
                {
                    mClusterFileWriter.write(String.format("%s,%d,%s,%d,%s,%s,%s,%s,%s,%d",
                            mSampleId, cluster.id(), cluster.getDesc(), clusterSvCount,
                            superType, resolvedType, cluster.isSyntheticType(),
                            cluster.isSubclonal(), cluster.isFullyChained(false), cluster.getChains().size()));

                    mClusterFileWriter.write(String.format(",%d,%d,%d,%d,%d,%d,%d",
                            cluster.getTypeCount(DEL), cluster.getTypeCount(DUP), cluster.getTypeCount(INS),
                            cluster.getTypeCount(INV), cluster.getTypeCount(BND), sglCount - inferredCount, inferredCount));

                    double foldbackCount = 0;

                    for (final SvVarData var : cluster.getFoldbacks())
                    {
                        // avoid double-count chained foldbacks
                        if (var.getFoldbackBreakend(true) != null)
                            foldbackCount += 0.5;
                        if (var.getFoldbackBreakend(false) != null)
                            foldbackCount += 0.5;
                    }

                    mClusterFileWriter.write(String.format(",%s,%d,%d,%s,%s,%.0f,%.0f,%.0f",
                            cluster.getClusteringReasons(), cluster.getConsistencyCount(), cluster.getArmCount(),
                            cluster.hasLinkingLineElements(), cluster.requiresReplication(), cluster.getMinPloidy(), cluster.getMaxPloidy(),
                            foldbackCount));

                    long shortTIs = cluster.getLinkedPairs().stream().filter(x -> x.length() <= SHORT_TI_LENGTH).count();

                    mClusterFileWriter.write(String.format(",%d,%d,%d",
                            cluster.getLinkedPairs().size(), cluster.getAssemblyLinkedPairs().size(), shortTIs));

                    final ChainMetrics chainMetrics = cluster.getLinkMetrics();

                    mClusterFileWriter.write(String.format(",%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                            chainMetrics.InternalTIs, chainMetrics.ExternalTIs, chainMetrics.InternalShortTIs, chainMetrics.ExternalShortTIs,
                            chainMetrics.InternalTICnGain, chainMetrics.ExternalTICnGain, chainMetrics.OverlappingTIs,
                            chainMetrics.DSBs, chainMetrics.ShortDSBs, chainMetrics.ChainEndsFace, chainMetrics.ChainEndsAway));

                    mClusterFileWriter.write(String.format(",%d,%d,%d,%d,%d,%s,%.2f",
                            cluster.getSyntheticLength(), cluster.getSyntheticTILength(), cluster.getOriginArms(), cluster.getFragmentArms(),
                            cluster.getUnlinkedSVs().size(), cluster.getAnnotations(), cluster.getValidAllelePloidySegmentPerc()));

                    final int[] armClusterData = getArmClusterData(cluster);
                    long armClusterTIs = cluster.getArmClusters().stream().mapToInt(x -> x.getTICount()).sum();

                    mClusterFileWriter.write(String.format(",%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                            cluster.getArmClusters()
                                    .size(), armClusterTIs, armClusterData[ARM_CL_ISOLATED_BE], armClusterData[ARM_CL_TI_ONLY],
                            armClusterData[ARM_CL_DSB], armClusterData[ARM_CL_SIMPLE_DUP], armClusterData[ARM_CL_FOLDBACK],
                            armClusterData[ARM_CL_FOLDBACK_DSB], armClusterData[ARM_CL_COMPLEX_FOLDBACK], armClusterData[ARM_CL_COMPLEX_LINE],
                            armClusterData[ARM_CL_COMPLEX_OTHER]));

                    mClusterFileWriter.newLine();
                }

                if(clusterData != null)
                {
                    clusterData.add(ImmutableLinxCluster.builder()
                            .clusterId(cluster.id())
                            .resolvedType(superType)
                            .synthetic(cluster.isSyntheticType())
                            .subClonal(cluster.isSubclonal())
                            .subType(cluster.getResolvedType().toString())
                            .clusterCount(clusterSvCount)
                            .clusterDesc(cluster.getDesc())
                            .build());
                }
            }
        }
        catch (final IOException e)
        {
            LOGGER.error("error writing cluster-data to outputFile: {}", e.toString());
        }
    }

    private void createLinksFile()
    {
        try
        {
            String outputFileName = mConfig.OutputDataPath + "SVA_LINKS.csv";

            mLinksFileWriter = createBufferedWriter(outputFileName, false);

            mLinksFileWriter.write("SampleId,ClusterId,ClusterDesc,ClusterCount,ResolvedType");
            mLinksFileWriter.write(",ChainId,ChainCount,ChainConsistent,LinkReason,LinkIndex,ChainIndex,Ploidy,PloidyUncertainty");
            mLinksFileWriter.write(",IsAssembled,TILength,NextSvDist,NextClusteredSvDist,TraversedSVCount");
            mLinksFileWriter.write(",LocationType,OverlapCount,CopyNumberGain");
            mLinksFileWriter.write(",Id1,Id2,ChrArm,PosStart,PosEnd,LocTopTypeStart,LocTopTypeEnd,GeneStart,GeneEnd,ExonMatch");
            mLinksFileWriter.newLine();
        }
        catch (final IOException e)
        {
            LOGGER.error("error writing links to outputFile: {}", e.toString());
        }

    }

    private void generateLinksOutput(@Nullable List<LinxLink> linksData)
    {
        try
        {
            for(final SvCluster cluster : getClusters())
            {
                int clusterSvCount = cluster.getSvCount();

                // isSpecificCluster(cluster);

                List<SvChain> chains = cluster.getChains();

                for (final SvChain chain : chains)
                {
                    int chainSvCount = chain.getSvCount();
                    boolean chainConsistent = chain.isConsistent();

                    List<SvLinkedPair> uniquePairs = Lists.newArrayList();
                    final List<SvLinkedPair> chainLinks = chain.getLinkedPairs();

                    for (int chainIndex = 0; chainIndex < chainLinks.size(); ++chainIndex)
                    {
                        final SvLinkedPair pair = chainLinks.get(chainIndex);

                        if(uniquePairs.stream().anyMatch(x -> x.matches(pair)))
                            continue;

                        uniquePairs.add(pair);

                        String chainIndexStr = String.valueOf(chainIndex);

                        for(int j = chainIndex + 1; j < chainLinks.size(); ++j)
                        {
                            if(chainLinks.get(j).matches(pair))
                            {
                                chainIndexStr = appendStr(chainIndexStr, String.valueOf(j), ';');
                            }
                        }

                        final SvBreakend beStart = pair.getBreakend(true);
                        final SvBreakend beEnd = pair.getBreakend(false);

                        if(mLinksFileWriter != null)
                        {
                            mLinksFileWriter.write(String.format("%s,%d,%s,%d,%s",
                                    mSampleId, cluster.id(), cluster.getDesc(), clusterSvCount, cluster.getResolvedType()));

                            mLinksFileWriter.write(String.format(",%d,%d,%s,%s,%d,%s,%s,%.3f",
                                    chain.id(), chainSvCount, chainConsistent, pair.getLinkReason(), pair.getLinkIndex(),
                                    chainIndexStr, formatPloidy(chain.ploidy()), chain.ploidyUncertainty()));

                            mLinksFileWriter.write(String.format(",%s,%d,%d,%d,%d,%s,%d,%s",
                                    pair.isAssembled(), pair.length(),
                                    pair.getNextSvDistance(), pair.getNextClusteredSvDistance(), pair.getTraversedSVCount(),
                                    pair.locationType(), pair.overlapCount(), pair.hasCopyNumberGain()));

                            SvArmCluster acStart = cluster.findArmCluster(beStart);
                            SvArmCluster acEnd = cluster.findArmCluster(beEnd);

                            mLinksFileWriter.write(String.format(",%d,%d,%s,%d,%d,%s,%s,%s,%s,%s",
                                    beStart.getSV().dbId(), beEnd.getSV().dbId(),
                                    beStart.getChrArm(), beStart.position(), beEnd.position(),
                                    acStart != null ? acStart.getTypeStr() : "", acEnd != null ? acEnd.getTypeStr() : "",
                                    beStart.getSV().getGeneInBreakend(beStart.usesStart(), false),
                                    beEnd.getSV().getGeneInBreakend(beEnd.usesStart(), false), pair.getExonMatchData()));

                            mLinksFileWriter.newLine();
                        }

                        if(linksData != null)
                        {
                            final String linkArm = beStart.arm() == beEnd.arm() ? beStart.arm() : beStart.arm() + "_" + beEnd.arm();

                            double ploidy = DatabaseUtil.decimal((beStart.getSV().ploidy() + beEnd.getSV().ploidy()) * 0.5);

                            linksData.add(ImmutableLinxLink.builder()
                                    .clusterId(cluster.id())
                                    .chainId(chain.id())
                                    .chainCount(chainSvCount)
                                    .chainIndex(chainIndex)
                                    .lowerSvId(beStart.getOrigSV().dbId())
                                    .upperSvId(beEnd.getOrigSV().dbId())
                                    .lowerBreakendIsStart(beStart.usesStart())
                                    .upperBreakendIsStart(beEnd.usesStart())
                                    .chromosome(beStart.chromosome())
                                    .arm(linkArm)
                                    .assembled(pair.isAssembled())
                                    .traversedSVCount(pair.getTraversedSVCount())
                                    .length(pair.length())
                                    .ploidy(ploidy)
                                    .pseudogeneInfo(pair.getExonMatchData())
                                    .build());
                        }
                    }
                }
            }
        }
        catch (final IOException e)
        {
            LOGGER.error("error writing links to outputFile: {}", e.toString());
        }
    }

    private List<LinxViralInsertFile> generateViralInserts()
    {
        List<LinxViralInsertFile> viralInserts = Lists.newArrayList();

        for(final SvVarData var : mAllVariants)
        {
            final String[] viralInsertData = mViralInsertAnnotator.matchesViralInsert(var);

            if(viralInsertData != null)
            {
                viralInserts.add(new LinxViralInsertFile(mSampleId, var.dbId(), viralInsertData[VH_ID], viralInsertData[VH_NAME]));
            }
        }

        return viralInserts;
    }

    public void close()
    {
        closeBufferedWriter(mSvFileWriter);
        closeBufferedWriter(mClusterFileWriter);
        closeBufferedWriter(mLinksFileWriter);

        mVisWriter.close();

        if(mConfig.hasMultipleSamples() || LOGGER.isDebugEnabled())
        {
            // log perf stats
            mPcPrep.logStats();
            mPcClusterAnalyse.logStats();
            mPcWrite.logStats();

            mAnalyser.logStats();
        }

        mAnalyser.close();
    }
}
