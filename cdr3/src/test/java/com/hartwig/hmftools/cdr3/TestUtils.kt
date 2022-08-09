package com.hartwig.hmftools.cdr3

import com.hartwig.hmftools.cdr3.layout.ReadLayout
import htsjdk.samtools.SAMUtils
import java.util.concurrent.atomic.AtomicInteger

object TestUtils
{
    const val MIN_BASE_QUALITY = 30.toByte()

    // used to assign unique read id
    val nextReadId = AtomicInteger(1)

    fun createRead(readId: String, seq: String, baseQualityString: String, alignedPosition: Int, firstOfPair: Boolean = true)
    : ReadLayout.Read
    {
        val baseQual = SAMUtils.fastqToPhred(baseQualityString)

        // we are aligned at the T
        return ReadLayout.Read(readId, ReadKey(readId, firstOfPair), seq, baseQual, alignedPosition)
    }

    // create a very simple layout with just a sequence
    fun createLayout(seq: String, minBaseQuality: Byte = MIN_BASE_QUALITY) : ReadLayout
    {
        val baseQual = SAMUtils.phredToFastq(minBaseQuality * 2).toString().repeat(seq.length)
        val read = createRead("createLayout::autoReadId::${nextReadId.getAndIncrement()}", seq, baseQual, 0)
        val layout = ReadLayout()
        layout.addRead(read, minBaseQuality)
        return layout
    }

    // some genes we can use for testing
    val ighJ1 = VJGene(
        "IGHJ1*01",
        "IGHJ1",
        "01",
        null,
        "GCTGAATACTTCCAGCACTGGGGCCAGGGCACCCTGGTCACCGTCTCCTCAG",
        "TGGGGCCAGGGCACCCTGGTCACCGTCTCC",
        null)

    val ighJ6 = VJGene(
        "IGHJ6*01", "IGHJ6","01", null,
        "ATTACTACTACTACTACGGTATGGACGTCTGGGGGCAAGGGACCACGGTCACCGTCTCCTCAG",
        "TGGGGGCAAGGGACCACGGTCACCGTCTCC",
        null)

    val ighV1_18 = VJGene(
        "IGHV1-18*01",
        "IGHV1-18",
        "01",
        null,
        "CAGGTTCAGCTGGTGCAGTCTGGAGCTGAGGTGAAGAAGCCTGGGGCCTCAGTGAAGGTCTCCTGCAAGGCTTCTGGTTACACCTTTACCAGCTATGGTATCAGCTGGGTGCGACAGGCCCCTGGACAAGGGCTTGAGTGGATGGGATGGATCAGCGCTTACAATGGTAACACAAACTATGCACAGAAGCTCCAGGGCAGAGTCACCATGACCACAGACACATCCACGAGCACAGCCTACATGGAGCTGAGGAGCCTGAGATCTGACGACACGGCCGTGTATTACTGTGCGAGAGA",
        "AGATCTGACGACACGGCCGTGTATTACTGT",
        null)

    val ighV3_7 = VJGene(
        "IGHV3-7*01", "IGHV3-7", "01", null,
        "GAGGTGCAGCTGGTGGAGTCTGGGGGAGGCTTGGTCCAGCCTGGGGGGTCCCTGAGACTCTCCTGTGCAGCCTCTGGATTCACCTTTAGTAGCTATTGGATGAGCTGGGTCCGCCAGGCTCCAGGGAAGGGGCTGGAGTGGGTGGCCAACATAAAGCAAGATGGAAGTGAGAAATACTATGTGGACTCTGTGAAGGGCCGATTCACCATCTCCAGAGACAACGCCAAGAACTCACTGTATCTGCAAATGAACAGCCTGAGAGCCGAGGACACGGCTGTGTATTACTGTGCGAGAGA",
        "AGAGCCGAGGACACGGCTGTGTATTACTGT",
        null)
}