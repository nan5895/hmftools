package com.hartwig.hmftools.orange.algo.pave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.gene.ExonData;
import com.hartwig.hmftools.common.gene.TranscriptData;
import com.hartwig.hmftools.common.genome.region.Strand;
import com.hartwig.hmftools.orange.algo.purple.PurpleVariant;
import com.hartwig.hmftools.orange.algo.purple.TestPurpleVariantFactory;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class PaveAlgoTest {

    private static final String MATCHING_GENE = "gene";
    private static final String MATCHING_TRANSCRIPT = "trans";

    @Test
    public void canCreatePaveEntries() {
        // "gene", coding range: 30-80, reverse strand. exon1: 60-90, exon2: 45-55, exon3: 25:35
        PaveAlgo pave = new PaveAlgo(TestEnsemblDataCacheFactory.loadTestCache());

        PurpleVariant missingGene = TestPurpleVariantFactory.builder().gene("other gene").build();
        assertNull(pave.run(missingGene, MATCHING_TRANSCRIPT));

        PurpleVariant missingTrans = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).build();
        assertNull(pave.run(missingTrans, "other trans"));

        PurpleVariant beyond = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).position(100).build();
        PaveEntry entryBeyond = pave.run(beyond, MATCHING_TRANSCRIPT);
        assertNull(entryBeyond.affectedCodon());
        assertNull(entryBeyond.affectedExon());

        PurpleVariant utr5 = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).position(85).build();
        PaveEntry entryUtr5 = pave.run(utr5, MATCHING_TRANSCRIPT);
        assertNull(entryUtr5.affectedCodon());
        assertEquals(1, (int) entryUtr5.affectedExon());

        PurpleVariant coding = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).position(50).build();
        PaveEntry entryCoding = pave.run(coding, MATCHING_TRANSCRIPT);
        assertEquals(9, (int) entryCoding.affectedCodon());
        assertEquals(2, (int) entryCoding.affectedExon());

        PurpleVariant intronic = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).position(40).build();
        PaveEntry entryIntronic = pave.run(intronic, MATCHING_TRANSCRIPT);
        assertNull(entryIntronic.affectedCodon());
        assertNull(entryIntronic.affectedExon());

        PurpleVariant utr3 = TestPurpleVariantFactory.builder().gene(MATCHING_GENE).position(25).build();
        PaveEntry entryUtr3 = pave.run(utr3, MATCHING_TRANSCRIPT);
        assertNull(entryUtr3.affectedCodon());
        assertEquals(3, (int) entryUtr3.affectedExon());
    }

    @Test
    public void canFindAffectedExon() {
        ExonData exon1 = createExon(1, 3, 5);
        ExonData exon2 = createExon(2, 11, 15);
        ExonData exon3 = createExon(3, 18, 20);
        List<ExonData> exons = Lists.newArrayList(exon1, exon2, exon3);

        assertNull(PaveAlgo.findAffectedExon(exons, 1));
        assertEquals(exon1, PaveAlgo.findAffectedExon(exons, 3));
        assertNull(PaveAlgo.findAffectedExon(exons, 8));
        assertEquals(exon2, PaveAlgo.findAffectedExon(exons, 15));
        assertNull(PaveAlgo.findAffectedExon(exons, 17));
        assertEquals(exon3, PaveAlgo.findAffectedExon(exons, 19));
        assertNull(PaveAlgo.findAffectedExon(exons, 25));
    }

    @Test
    public void canFindAffectedCodonPositiveStrand() {
        ExonData exon1 = createExon(1, 3, 5);
        ExonData exon2 = createExon(2, 11, 15);
        ExonData exon3 = createExon(3, 18, 20);
        TranscriptData transcript = createTranscript(Lists.newArrayList(exon1, exon2, exon3), 4, 19, Strand.FORWARD);

        assertNull(PaveAlgo.findAffectedCodon(transcript, 2));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 3));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 4));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 5));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 6));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 10));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 11));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 12));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 13));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 14));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 15));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 16));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 17));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 18));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 19));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 20));
    }

    @Test
    public void canFindAffectedCodonNegativeStrand() {
        ExonData exon1 = createExon(3, 3, 5);
        ExonData exon2 = createExon(2, 11, 15);
        ExonData exon3 = createExon(1, 18, 20);
        TranscriptData transcript = createTranscript(Lists.newArrayList(exon1, exon2, exon3), 4, 19, Strand.REVERSE);

        assertNull(PaveAlgo.findAffectedCodon(transcript, 2));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 3));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 4));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 5));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 6));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 10));
        assertEquals(3, (int) PaveAlgo.findAffectedCodon(transcript, 11));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 12));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 13));
        assertEquals(2, (int) PaveAlgo.findAffectedCodon(transcript, 14));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 15));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 16));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 17));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 18));
        assertEquals(1, (int) PaveAlgo.findAffectedCodon(transcript, 19));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 20));
    }

    @Test
    public void canHandleNonCodingTranscripts() {
        ExonData exon1 = createExon(1, 3, 5);
        ExonData exon2 = createExon(2, 11, 15);
        ExonData exon3 = createExon(3, 18, 20);
        TranscriptData transcript = createTranscript(Lists.newArrayList(exon1, exon2, exon3), null, null, Strand.FORWARD);

        assertNull(PaveAlgo.findAffectedCodon(transcript, 4));
        assertNull(PaveAlgo.findAffectedCodon(transcript, 5));
    }

    @NotNull
    private static ExonData createExon(int rank, int start, int end) {
        return new ExonData(-1, start, end, rank, -1, -1);
    }

    @NotNull
    private static TranscriptData createTranscript(@NotNull List<ExonData> exons, @Nullable Integer codingStart,
            @Nullable Integer codingEnd, @NotNull Strand strand) {
        byte strandEnum = strand == Strand.FORWARD ? Strand.POS_STRAND : Strand.NEG_STRAND;
        TranscriptData transcript =
                new TranscriptData(-1, Strings.EMPTY, Strings.EMPTY, false, strandEnum, -1, -1, codingStart, codingEnd, Strings.EMPTY);
        transcript.setExons(exons);
        return transcript;
    }
}