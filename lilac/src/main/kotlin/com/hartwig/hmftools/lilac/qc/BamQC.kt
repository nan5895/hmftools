package com.hartwig.hmftools.lilac.qc

import com.hartwig.hmftools.lilac.read.SAMRecordReader
import org.apache.logging.log4j.LogManager

data class BamQC(val discardedAlignmentFragments: Int, val discardedIndelFragments: Int, val discardedIndelMaxCount: Int) {

    fun header(): List<String> {
        return listOf("discardedIndelFragments", "discardedIndelMaxCount", "discardedAlignmentFragments")
    }

    fun body(): List<String> {
        return listOf(discardedIndelFragments.toString(), discardedIndelMaxCount.toString(), discardedAlignmentFragments.toString())
    }

    companion object {
        val logger = LogManager.getLogger(this::class.java)

        fun create(reader: SAMRecordReader): BamQC {
            val fragmentsWithUnmatchedIndel = reader.unmatchedIndels(2)
            for ((indel, count) in fragmentsWithUnmatchedIndel) {
                logger.warn("UNMATCHED_INDEL - $count fragments excluded with unmatched indel $indel")
            }
            return BamQC(reader.alignmentFiltered(),  fragmentsWithUnmatchedIndel.size, fragmentsWithUnmatchedIndel.values.max() ?: 0)
        }
    }

}

