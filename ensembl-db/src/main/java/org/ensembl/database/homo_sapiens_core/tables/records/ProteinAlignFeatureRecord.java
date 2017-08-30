/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.ProteinAlignFeature;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProteinAlignFeatureRecord extends UpdatableRecordImpl<ProteinAlignFeatureRecord> implements Record15<UInteger, UInteger, UInteger, UInteger, Byte, Integer, Integer, String, UShort, Double, Double, Double, String, UInteger, Double> {

    private static final long serialVersionUID = -1090472497;

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.protein_align_feature_id</code>.
     */
    public void setProteinAlignFeatureId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.protein_align_feature_id</code>.
     */
    public UInteger getProteinAlignFeatureId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_id</code>.
     */
    public void setSeqRegionId(UInteger value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_id</code>.
     */
    public UInteger getSeqRegionId() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_start</code>.
     */
    public void setSeqRegionStart(UInteger value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_start</code>.
     */
    public UInteger getSeqRegionStart() {
        return (UInteger) get(2);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_end</code>.
     */
    public void setSeqRegionEnd(UInteger value) {
        set(3, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_end</code>.
     */
    public UInteger getSeqRegionEnd() {
        return (UInteger) get(3);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_strand</code>.
     */
    public void setSeqRegionStrand(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.seq_region_strand</code>.
     */
    public Byte getSeqRegionStrand() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_start</code>.
     */
    public void setHitStart(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_start</code>.
     */
    public Integer getHitStart() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_end</code>.
     */
    public void setHitEnd(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_end</code>.
     */
    public Integer getHitEnd() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_name</code>.
     */
    public void setHitName(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.hit_name</code>.
     */
    public String getHitName() {
        return (String) get(7);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.analysis_id</code>.
     */
    public void setAnalysisId(UShort value) {
        set(8, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.analysis_id</code>.
     */
    public UShort getAnalysisId() {
        return (UShort) get(8);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.score</code>.
     */
    public void setScore(Double value) {
        set(9, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.score</code>.
     */
    public Double getScore() {
        return (Double) get(9);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.evalue</code>.
     */
    public void setEvalue(Double value) {
        set(10, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.evalue</code>.
     */
    public Double getEvalue() {
        return (Double) get(10);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.perc_ident</code>.
     */
    public void setPercIdent(Double value) {
        set(11, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.perc_ident</code>.
     */
    public Double getPercIdent() {
        return (Double) get(11);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.cigar_line</code>.
     */
    public void setCigarLine(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.cigar_line</code>.
     */
    public String getCigarLine() {
        return (String) get(12);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.external_db_id</code>.
     */
    public void setExternalDbId(UInteger value) {
        set(13, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.external_db_id</code>.
     */
    public UInteger getExternalDbId() {
        return (UInteger) get(13);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.protein_align_feature.hcoverage</code>.
     */
    public void setHcoverage(Double value) {
        set(14, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.protein_align_feature.hcoverage</code>.
     */
    public Double getHcoverage() {
        return (Double) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<UInteger, UInteger, UInteger, UInteger, Byte, Integer, Integer, String, UShort, Double, Double, Double, String, UInteger, Double> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<UInteger, UInteger, UInteger, UInteger, Byte, Integer, Integer, String, UShort, Double, Double, Double, String, UInteger, Double> valuesRow() {
        return (Row15) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.PROTEIN_ALIGN_FEATURE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.SEQ_REGION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field3() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.SEQ_REGION_START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field4() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.SEQ_REGION_END;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.SEQ_REGION_STRAND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.HIT_START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.HIT_END;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.HIT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UShort> field9() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.ANALYSIS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field10() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field11() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.EVALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field12() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.PERC_IDENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.CIGAR_LINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field14() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.EXTERNAL_DB_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field15() {
        return ProteinAlignFeature.PROTEIN_ALIGN_FEATURE.HCOVERAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getProteinAlignFeatureId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value2() {
        return getSeqRegionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value3() {
        return getSeqRegionStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value4() {
        return getSeqRegionEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getSeqRegionStrand();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getHitStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getHitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getHitName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UShort value9() {
        return getAnalysisId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value10() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value11() {
        return getEvalue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value12() {
        return getPercIdent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getCigarLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value14() {
        return getExternalDbId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value15() {
        return getHcoverage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value1(UInteger value) {
        setProteinAlignFeatureId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value2(UInteger value) {
        setSeqRegionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value3(UInteger value) {
        setSeqRegionStart(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value4(UInteger value) {
        setSeqRegionEnd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value5(Byte value) {
        setSeqRegionStrand(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value6(Integer value) {
        setHitStart(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value7(Integer value) {
        setHitEnd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value8(String value) {
        setHitName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value9(UShort value) {
        setAnalysisId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value10(Double value) {
        setScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value11(Double value) {
        setEvalue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value12(Double value) {
        setPercIdent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value13(String value) {
        setCigarLine(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value14(UInteger value) {
        setExternalDbId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord value15(Double value) {
        setHcoverage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProteinAlignFeatureRecord values(UInteger value1, UInteger value2, UInteger value3, UInteger value4, Byte value5, Integer value6, Integer value7, String value8, UShort value9, Double value10, Double value11, Double value12, String value13, UInteger value14, Double value15) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProteinAlignFeatureRecord
     */
    public ProteinAlignFeatureRecord() {
        super(ProteinAlignFeature.PROTEIN_ALIGN_FEATURE);
    }

    /**
     * Create a detached, initialised ProteinAlignFeatureRecord
     */
    public ProteinAlignFeatureRecord(UInteger proteinAlignFeatureId, UInteger seqRegionId, UInteger seqRegionStart, UInteger seqRegionEnd, Byte seqRegionStrand, Integer hitStart, Integer hitEnd, String hitName, UShort analysisId, Double score, Double evalue, Double percIdent, String cigarLine, UInteger externalDbId, Double hcoverage) {
        super(ProteinAlignFeature.PROTEIN_ALIGN_FEATURE);

        set(0, proteinAlignFeatureId);
        set(1, seqRegionId);
        set(2, seqRegionStart);
        set(3, seqRegionEnd);
        set(4, seqRegionStrand);
        set(5, hitStart);
        set(6, hitEnd);
        set(7, hitName);
        set(8, analysisId);
        set(9, score);
        set(10, evalue);
        set(11, percIdent);
        set(12, cigarLine);
        set(13, externalDbId);
        set(14, hcoverage);
    }
}
