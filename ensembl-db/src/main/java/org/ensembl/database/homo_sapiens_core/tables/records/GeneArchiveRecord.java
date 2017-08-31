/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.GeneArchive;
import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.TableRecordImpl;
import org.jooq.types.UInteger;


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
public class GeneArchiveRecord extends TableRecordImpl<GeneArchiveRecord> implements Record8<String, Short, String, Short, String, Short, UInteger, UInteger> {

    private static final long serialVersionUID = -891933335;

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.gene_stable_id</code>.
     */
    public void setGeneStableId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.gene_stable_id</code>.
     */
    public String getGeneStableId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.gene_version</code>.
     */
    public void setGeneVersion(Short value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.gene_version</code>.
     */
    public Short getGeneVersion() {
        return (Short) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.transcript_stable_id</code>.
     */
    public void setTranscriptStableId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.transcript_stable_id</code>.
     */
    public String getTranscriptStableId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.transcript_version</code>.
     */
    public void setTranscriptVersion(Short value) {
        set(3, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.transcript_version</code>.
     */
    public Short getTranscriptVersion() {
        return (Short) get(3);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.translation_stable_id</code>.
     */
    public void setTranslationStableId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.translation_stable_id</code>.
     */
    public String getTranslationStableId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.translation_version</code>.
     */
    public void setTranslationVersion(Short value) {
        set(5, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.translation_version</code>.
     */
    public Short getTranslationVersion() {
        return (Short) get(5);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.peptide_archive_id</code>.
     */
    public void setPeptideArchiveId(UInteger value) {
        set(6, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.peptide_archive_id</code>.
     */
    public UInteger getPeptideArchiveId() {
        return (UInteger) get(6);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.gene_archive.mapping_session_id</code>.
     */
    public void setMappingSessionId(UInteger value) {
        set(7, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.gene_archive.mapping_session_id</code>.
     */
    public UInteger getMappingSessionId() {
        return (UInteger) get(7);
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, Short, String, Short, String, Short, UInteger, UInteger> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, Short, String, Short, String, Short, UInteger, UInteger> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return GeneArchive.GENE_ARCHIVE.GENE_STABLE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field2() {
        return GeneArchive.GENE_ARCHIVE.GENE_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return GeneArchive.GENE_ARCHIVE.TRANSCRIPT_STABLE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field4() {
        return GeneArchive.GENE_ARCHIVE.TRANSCRIPT_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return GeneArchive.GENE_ARCHIVE.TRANSLATION_STABLE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field6() {
        return GeneArchive.GENE_ARCHIVE.TRANSLATION_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field7() {
        return GeneArchive.GENE_ARCHIVE.PEPTIDE_ARCHIVE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field8() {
        return GeneArchive.GENE_ARCHIVE.MAPPING_SESSION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getGeneStableId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value2() {
        return getGeneVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getTranscriptStableId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value4() {
        return getTranscriptVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getTranslationStableId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value6() {
        return getTranslationVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value7() {
        return getPeptideArchiveId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value8() {
        return getMappingSessionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value1(String value) {
        setGeneStableId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value2(Short value) {
        setGeneVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value3(String value) {
        setTranscriptStableId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value4(Short value) {
        setTranscriptVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value5(String value) {
        setTranslationStableId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value6(Short value) {
        setTranslationVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value7(UInteger value) {
        setPeptideArchiveId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord value8(UInteger value) {
        setMappingSessionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneArchiveRecord values(String value1, Short value2, String value3, Short value4, String value5, Short value6, UInteger value7, UInteger value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GeneArchiveRecord
     */
    public GeneArchiveRecord() {
        super(GeneArchive.GENE_ARCHIVE);
    }

    /**
     * Create a detached, initialised GeneArchiveRecord
     */
    public GeneArchiveRecord(String geneStableId, Short geneVersion, String transcriptStableId, Short transcriptVersion, String translationStableId, Short translationVersion, UInteger peptideArchiveId, UInteger mappingSessionId) {
        super(GeneArchive.GENE_ARCHIVE);

        set(0, geneStableId);
        set(1, geneVersion);
        set(2, transcriptStableId);
        set(3, transcriptVersion);
        set(4, translationStableId);
        set(5, translationVersion);
        set(6, peptideArchiveId);
        set(7, mappingSessionId);
    }
}
