package com.hartwig.hmftools.svanalysis.analyser;

import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.BND;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DEL;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DUP;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.INV;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.SGL;
import static com.hartwig.hmftools.svanalysis.analyser.SvTestHelper.createBnd;
import static com.hartwig.hmftools.svanalysis.analyser.SvTestHelper.createDel;
import static com.hartwig.hmftools.svanalysis.analyser.SvTestHelper.createDup;
import static com.hartwig.hmftools.svanalysis.analyser.SvTestHelper.createInv;
import static com.hartwig.hmftools.svanalysis.analyser.SvTestHelper.createTestSv;
import static com.hartwig.hmftools.svanalysis.types.SvChain.CHAIN_ASSEMBLY_LINK_COUNT;
import static com.hartwig.hmftools.svanalysis.types.SvChain.CHAIN_LINK_COUNT;
import static com.hartwig.hmftools.svanalysis.types.SvLinkedPair.ASSEMBLY_MATCH_MATCHED;
import static com.hartwig.hmftools.svanalysis.types.SvLinkedPair.LINK_TYPE_DB;
import static com.hartwig.hmftools.svanalysis.types.SvLinkedPair.LINK_TYPE_TI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.svanalysis.types.SvChain;
import com.hartwig.hmftools.svanalysis.types.SvCluster;
import com.hartwig.hmftools.svanalysis.types.SvLOH;
import com.hartwig.hmftools.svanalysis.types.SvLinkedPair;
import com.hartwig.hmftools.svanalysis.types.SvVarData;

import org.junit.Ignore;
import org.junit.Test;

public class ChainingTests
{

    @Test
    public void testChainRoutines()
    {
        // create a chain out of simple DELs and test the various chaining features
        final SvVarData var1 = createDel("1", "1", 1100, 1200);
        final SvVarData var2 = createDel("2", "1", 1300, 1400);
        SvLinkedPair lp1 = new SvLinkedPair(var1, var2, LINK_TYPE_TI, false, true);
        lp1.setIsAssembled();

        // test adding linked pairs of various orientations to the start and end of a chain
        SvChain chain = new SvChain(0);

        chain.addLink(lp1, true);

        assertTrue(chain.firstLinkOpenOnStart());
        assertTrue(!chain.lastLinkOpenOnStart());

        final SvVarData var3 = createDel("3", "1", 1500, 1600);
        SvLinkedPair lp2 = new SvLinkedPair(var3, var2, LINK_TYPE_TI, true, false);
        lp2.setIsAssembled();

        assertFalse(chain.canAddLinkedPairToStart(lp2));
        assertTrue(chain.canAddLinkedPairToEnd(lp2));

        chain.addLink(lp2, false);

        assertEquals(chain.getFirstSV(), var1);
        assertEquals(chain.getLastSV(), var3);
        assertEquals(chain.getSvList().get(1), var2);

        final SvVarData var4 = createDel("4", "1", 900, 1000);
        SvLinkedPair lp3 = new SvLinkedPair(var1, var4, LINK_TYPE_TI, true, false);

        assertTrue(chain.canAddLinkedPairToStart(lp3));
        assertFalse(chain.canAddLinkedPairToEnd(lp3));
        chain.addLink(lp3, true);

        assertEquals(chain.getFirstSV(), var4);
        assertEquals(chain.getLastSV(), var3);
        assertEquals(chain.getSvList().get(1), var1);
        assertEquals(chain.getSvList().get(2), var2);

        // test a potentially closing link
        final SvVarData var5 = createDup("5", "1", 800, 1700);
        SvLinkedPair lp4 = new SvLinkedPair(var5, var4, LINK_TYPE_TI, true, true);

        assertTrue(chain.canAddLinkedPairToStart(lp4));
        chain.addLink(lp4, true);

        assertEquals(chain.getAssemblyLinkCount(), 2);

        SvLinkedPair lp5 = new SvLinkedPair(var5, var3, LINK_TYPE_TI, false, false);

        assertTrue(chain.canAddLinkedPairToEnd(lp5));
        assertTrue(chain.linkWouldCloseChain(lp5));

        // tests paths through the chain from various points
        int[] chainData = chain.breakendsAreChained(var4, false, var3, true);
        assertEquals(chainData[CHAIN_LINK_COUNT], 3);
        assertEquals(chainData[CHAIN_ASSEMBLY_LINK_COUNT], 2);

        // check works in the other direction
        chainData = chain.breakendsAreChained(var3, true, var4, false);
        assertEquals(chainData[CHAIN_LINK_COUNT], 3);
        assertEquals(chainData[CHAIN_ASSEMBLY_LINK_COUNT], 2);

        // check a single link
        chainData = chain.breakendsAreChained(var1, false, var2, true);
        assertEquals(chainData[CHAIN_LINK_COUNT], 1);
        assertEquals(chainData[CHAIN_ASSEMBLY_LINK_COUNT], 1);

        // check breakends facing the wrong way
        chainData = chain.breakendsAreChained(var1, false, var2, false);
        assertEquals(chainData[CHAIN_LINK_COUNT], 0);

        chainData = chain.breakendsAreChained(var1, true, var2, false);
        assertEquals(chainData[CHAIN_LINK_COUNT], 0);

        chainData = chain.breakendsAreChained(var1, true, var2, true);
        assertEquals(chainData[CHAIN_LINK_COUNT], 0);

        // check no link
        chainData = chain.breakendsAreChained(var5, false, var1, true);
        assertEquals(chainData[CHAIN_LINK_COUNT], 0);
    }

    @Test
    public void testFullyAssembledChain()
    {
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData var1 = createDel("0", "1", 100,200);
        final SvVarData var2 = createDel("1", "1", 300,400);
        final SvVarData var3 = createDel("2", "1", 500,600);
        final SvVarData var4 = createDel("3", "1", 700,800);

        var1.setAssemblyData(false, "asmb12");
        var2.setAssemblyData(true, "asmb12");
        var2.setAssemblyData(false, "asmb23");
        var3.setAssemblyData(true, "asmb23");
        var3.setAssemblyData(false, "asmb34");
        var4.setAssemblyData(true, "asmb34");

        // add them out of order which will require partial chain reconciliation
        tester.AllVariants.add(var1);
        tester.AllVariants.add(var2);
        tester.AllVariants.add(var3);
        tester.AllVariants.add(var4);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(3, chain.getLinkCount());
        assertEquals(3, cluster.getAssemblyLinkedPairs().size());
    }

    @Test
    public void testChainNotClosed()
    {
        // 2 SVs which could link on both ends
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData var1 = createInv("0", "1", 100,200, -1);
        final SvVarData var2 = createDel("1", "1", 300,400);
        final SvVarData var3 = createDel("2", "1", 500,600);
        final SvVarData var4 = createInv("3", "1", 700,800, 1);

        // add them out of order which will require partial chain reconciliation
        tester.AllVariants.add(var1);
        tester.AllVariants.add(var2);
        tester.AllVariants.add(var3);
        tester.AllVariants.add(var4);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(3, chain.getLinkCount());
    }

    @Test
    public void testPartiallyAssembledChain()
    {
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData var0 = createDel("0", "1", 100,200);
        final SvVarData var1 = createDel("1", "1", 300,400);
        final SvVarData var2 = createDel("2", "1", 500,600);
        final SvVarData var3 = createDel("3", "1", 700,800);

        var1.setAssemblyData(false, "asmb23");
        var2.setAssemblyData(true, "asmb23");

        // add them out of order which will require partial chain reconciliation
        tester.AllVariants.add(var0);
        tester.AllVariants.add(var1);
        tester.AllVariants.add(var2);
        tester.AllVariants.add(var3);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(3, chain.getLinkCount());
    }

    @Test
    public void testBFBChain1()
    {
        // vanilla BFB of the form centromere - A - B - A - C - A - R - telomere, where R is the resolving SV
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData varA = createTestSv("0", "1", "1", 2000,3000, -1, -1, INV,  3);
        final SvVarData varB = createTestSv("1", "1", "1", 9000,10000, 1, 1, INV,  1);
        final SvVarData varC = createTestSv("2", "1", "1", 5000,6000, 1, 1, INV, 1);
        final SvVarData varR = createTestSv("3", "1", "1", 1000,8000, 1, 1, INV, 1);

        tester.AllVariants.add(varA);
        tester.AllVariants.add(varB);
        tester.AllVariants.add(varC);
        tester.AllVariants.add(varR);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(varA.getFoldbackLink(true), varA.id());
        assertEquals(varB.getFoldbackLink(true), varB.id());
        assertEquals(varC.getFoldbackLink(true), varC.id());

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(5, chain.getLinkCount());
    }

    @Test
    public void testBFBChain2()
    {
        // BFB of the form centromere - A - B - A - C - A - D - B - A - C - A - B - R - telomere,
        // where D is a complex DUP around the section B - A - C - A and R is the resolving SV
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData varA = createTestSv("0", "1", "1", 2000,3000, -1, -1, INV, 5);
        final SvVarData varB = createTestSv("1", "1", "1", 9000,10000, 1, 1, INV, 3);
        final SvVarData varC = createTestSv("2", "1", "1", 5000,6000, 1, 1, INV, 2);
        final SvVarData varD = createTestSv("3", "1", "1", 7000,8000, -1, 1, DUP, 1);
        final SvVarData varR = createTestSv("4", "1", "1", 1000,4000, 1, -1, DEL, 1);

        // CN profile:
        // T - 2 - 1 - 6 - 11 - 12 - 10 - 8 - 9 - 8 - 5 - 2 - C

        tester.AllVariants.add(varA);
        tester.AllVariants.add(varB);
        tester.AllVariants.add(varC);
        tester.AllVariants.add(varD);
        tester.AllVariants.add(varR);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(varA.getFoldbackLink(true), varA.id());
        assertEquals(varB.getFoldbackLink(true), varB.id());
        assertEquals(varC.getFoldbackLink(true), varC.id());

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(11, chain.getLinkCount());
    }

    @Test
    public void testComplexDupChain()
    {
        // simple chain with replicated section: centromere - A - B - C - D - DUP - B - C - D - E - telomere,
        // where D is a complex DUP around the section B - C - D
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData varA = createTestSv("1", "1", "1", 1000,5000, 1, 1, INV, 1);
        final SvVarData varB = createTestSv("2", "1", "1", 4000,9000, -1, 1, DUP, 2);
        final SvVarData varC = createTestSv("3", "1", "1", 7000,8000, 1, -1, DEL, 2);
        final SvVarData varD = createTestSv("4", "1", "1", 3000,6000, 1, -1, DEL, 2);
        final SvVarData varE = createTestSv("5", "1", "1", 2000,10000, -1, -1, INV, 1);
        final SvVarData varDup = createTestSv("6", "1", "1", 2500,4500, -1, 1, DUP, 1);

        // CN profile:
        // T - 2 - 1 - 2 - 3 - 1 - 3 - 2 - 1 - 3 - 1 - 3 - 1 - 2  - C

        tester.AllVariants.add(varA);
        tester.AllVariants.add(varB);
        tester.AllVariants.add(varC);
        tester.AllVariants.add(varD);
        tester.AllVariants.add(varE);
        tester.AllVariants.add(varDup);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertTrue(cluster.getFoldbacks().isEmpty());
        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        assertEquals(8, chain.getLinkCount());
    }

    @Test
    public void testBFBChainWithChainedFoldbacks()
    {
        // BFB of the form centromere - A - B - A - C - A - R - telomere, where R is the resolving SV
        // but assembled fragments are inserted into each of the foldbacks, requiring linking to make use of matching ploidy
        SvTestHelper tester = new SvTestHelper();
        tester.logVerbose(true);

        final SvVarData varA1 = createTestSv("0", "1", "1", 2000,5500, -1, -1, INV, 4);
        final SvVarData varA2 = createTestSv("1", "1", "1", 3000,5600, -1, 1, DUP, 4);

        varA1.setAssemblyData(false, "asmb_A1_A2");
        varA2.setAssemblyData(false, "asmb_A1_A2");

        final SvVarData varB = createTestSv("2", "1", "1", 9000,10000, 1, 1, INV, 2);

        // functions as a foldback but interrupted by the assembled TI A1-A2
        final SvVarData varC = createTestSv("3", "1", "1", 5000,6000, 1, 1, INV, 1);
        final SvVarData varR = createTestSv("4", "1", "1", 1000,8000, 1, 1, INV, 1);

        // CN profile
        // T - 2 - 1 - 5 - 9 - 8 - 12 - 8 - 7 - 6 - 4 - 2 - C

        tester.AllVariants.add(varA1);
        tester.AllVariants.add(varA2);
        tester.AllVariants.add(varB);
        tester.AllVariants.add(varC);
        tester.AllVariants.add(varR);

        tester.preClusteringInit();
        tester.Analyser.clusterAndAnalyse();

        assertTrue(varA1.isChainedFoldback());
        assertTrue(varA2.isChainedFoldback());
        assertEquals(varA2.id(), varA1.getFoldbackLink(true));
        assertEquals(varA1.id(), varA2.getFoldbackLink(true));
        assertEquals(varB.id(), varB.getFoldbackLink(true));

        assertEquals(1, tester.Analyser.getClusters().size());
        final SvCluster cluster = tester.Analyser.getClusters().get(0);

        assertEquals(1, cluster.getChains().size());

        final SvChain chain = cluster.getChains().get(0);

        int linkCount = 2 * 4 + 1 * 2 + 2 - 1;
        assertEquals(linkCount, chain.getLinkCount());
    }


}
