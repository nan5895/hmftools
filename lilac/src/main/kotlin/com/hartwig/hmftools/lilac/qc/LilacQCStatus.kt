package com.hartwig.hmftools.lilac.qc

enum class LilacQCStatus {
    PASS, WARN_UNMATCHED_TYPE, WARN_UNMATCHED_SOMATIC_VARIANT, WARN_WILDCARD_MATCH, WARN_UNMATCHED_HAPLOTYPE, WARN_UNMATCHED_INDEL;
}