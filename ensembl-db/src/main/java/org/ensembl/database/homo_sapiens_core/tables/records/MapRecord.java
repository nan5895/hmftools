/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.Map;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
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
public class MapRecord extends UpdatableRecordImpl<MapRecord> implements Record2<UInteger, String> {

    private static final long serialVersionUID = 138344589;

    /**
     * Setter for <code>homo_sapiens_core_89_37.map.map_id</code>.
     */
    public void setMapId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.map.map_id</code>.
     */
    public UInteger getMapId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.map.map_name</code>.
     */
    public void setMapName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.map.map_name</code>.
     */
    public String getMapName() {
        return (String) get(1);
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
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<UInteger, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<UInteger, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return Map.MAP.MAP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Map.MAP.MAP_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getMapId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getMapName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapRecord value1(UInteger value) {
        setMapId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapRecord value2(String value) {
        setMapName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapRecord values(UInteger value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MapRecord
     */
    public MapRecord() {
        super(Map.MAP);
    }

    /**
     * Create a detached, initialised MapRecord
     */
    public MapRecord(UInteger mapId, String mapName) {
        super(Map.MAP);

        set(0, mapId);
        set(1, mapName);
    }
}
