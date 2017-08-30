/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.SeqRegionRecord;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
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
public class SeqRegion extends TableImpl<SeqRegionRecord> {

    private static final long serialVersionUID = 2007550918;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.seq_region</code>
     */
    public static final SeqRegion SEQ_REGION = new SeqRegion();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SeqRegionRecord> getRecordType() {
        return SeqRegionRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.seq_region.seq_region_id</code>.
     */
    public final TableField<SeqRegionRecord, UInteger> SEQ_REGION_ID = createField("seq_region_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.seq_region.name</code>.
     */
    public final TableField<SeqRegionRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.seq_region.coord_system_id</code>.
     */
    public final TableField<SeqRegionRecord, UInteger> COORD_SYSTEM_ID = createField("coord_system_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.seq_region.length</code>.
     */
    public final TableField<SeqRegionRecord, UInteger> LENGTH = createField("length", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.seq_region</code> table reference
     */
    public SeqRegion() {
        this("seq_region", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.seq_region</code> table reference
     */
    public SeqRegion(String alias) {
        this(alias, SEQ_REGION);
    }

    private SeqRegion(String alias, Table<SeqRegionRecord> aliased) {
        this(alias, aliased, null);
    }

    private SeqRegion(String alias, Table<SeqRegionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return HomoSapiensCore_89_37.HOMO_SAPIENS_CORE_89_37;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<SeqRegionRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_SEQ_REGION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SeqRegionRecord> getPrimaryKey() {
        return Keys.KEY_SEQ_REGION_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SeqRegionRecord>> getKeys() {
        return Arrays.<UniqueKey<SeqRegionRecord>>asList(Keys.KEY_SEQ_REGION_PRIMARY, Keys.KEY_SEQ_REGION_NAME_CS_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SeqRegion as(String alias) {
        return new SeqRegion(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SeqRegion rename(String name) {
        return new SeqRegion(name, null);
    }
}
