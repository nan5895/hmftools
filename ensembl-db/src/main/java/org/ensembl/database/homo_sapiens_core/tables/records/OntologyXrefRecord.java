/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.OntologyXref;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
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
public class OntologyXrefRecord extends TableRecordImpl<OntologyXrefRecord> implements Record3<UInteger, UInteger, String> {

    private static final long serialVersionUID = -845094455;

    /**
     * Setter for <code>homo_sapiens_core_89_37.ontology_xref.object_xref_id</code>.
     */
    public void setObjectXrefId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.ontology_xref.object_xref_id</code>.
     */
    public UInteger getObjectXrefId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.ontology_xref.source_xref_id</code>.
     */
    public void setSourceXrefId(UInteger value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.ontology_xref.source_xref_id</code>.
     */
    public UInteger getSourceXrefId() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.ontology_xref.linkage_type</code>.
     */
    public void setLinkageType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.ontology_xref.linkage_type</code>.
     */
    public String getLinkageType() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UInteger, UInteger, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UInteger, UInteger, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return OntologyXref.ONTOLOGY_XREF.OBJECT_XREF_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return OntologyXref.ONTOLOGY_XREF.SOURCE_XREF_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return OntologyXref.ONTOLOGY_XREF.LINKAGE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getObjectXrefId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value2() {
        return getSourceXrefId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getLinkageType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OntologyXrefRecord value1(UInteger value) {
        setObjectXrefId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OntologyXrefRecord value2(UInteger value) {
        setSourceXrefId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OntologyXrefRecord value3(String value) {
        setLinkageType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OntologyXrefRecord values(UInteger value1, UInteger value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OntologyXrefRecord
     */
    public OntologyXrefRecord() {
        super(OntologyXref.ONTOLOGY_XREF);
    }

    /**
     * Create a detached, initialised OntologyXrefRecord
     */
    public OntologyXrefRecord(UInteger objectXrefId, UInteger sourceXrefId, String linkageType) {
        super(OntologyXref.ONTOLOGY_XREF);

        set(0, objectXrefId);
        set(1, sourceXrefId);
        set(2, linkageType);
    }
}
