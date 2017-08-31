/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.AssociatedXrefRecord;
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
public class AssociatedXref extends TableImpl<AssociatedXrefRecord> {

    private static final long serialVersionUID = 1862178608;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.associated_xref</code>
     */
    public static final AssociatedXref ASSOCIATED_XREF = new AssociatedXref();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AssociatedXrefRecord> getRecordType() {
        return AssociatedXrefRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.associated_xref_id</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> ASSOCIATED_XREF_ID = createField("associated_xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.object_xref_id</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> OBJECT_XREF_ID = createField("object_xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.xref_id</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> XREF_ID = createField("xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.source_xref_id</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> SOURCE_XREF_ID = createField("source_xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.condition_type</code>.
     */
    public final TableField<AssociatedXrefRecord, String> CONDITION_TYPE = createField("condition_type", org.jooq.impl.SQLDataType.VARCHAR.length(128), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.associated_group_id</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> ASSOCIATED_GROUP_ID = createField("associated_group_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.associated_xref.rank</code>.
     */
    public final TableField<AssociatedXrefRecord, UInteger> RANK = createField("rank", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.associated_xref</code> table reference
     */
    public AssociatedXref() {
        this("associated_xref", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.associated_xref</code> table reference
     */
    public AssociatedXref(String alias) {
        this(alias, ASSOCIATED_XREF);
    }

    private AssociatedXref(String alias, Table<AssociatedXrefRecord> aliased) {
        this(alias, aliased, null);
    }

    private AssociatedXref(String alias, Table<AssociatedXrefRecord> aliased, Field<?>[] parameters) {
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
    public Identity<AssociatedXrefRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_ASSOCIATED_XREF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AssociatedXrefRecord> getPrimaryKey() {
        return Keys.KEY_ASSOCIATED_XREF_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AssociatedXrefRecord>> getKeys() {
        return Arrays.<UniqueKey<AssociatedXrefRecord>>asList(Keys.KEY_ASSOCIATED_XREF_PRIMARY, Keys.KEY_ASSOCIATED_XREF_OBJECT_ASSOCIATED_SOURCE_TYPE_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssociatedXref as(String alias) {
        return new AssociatedXref(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AssociatedXref rename(String name) {
        return new AssociatedXref(name, null);
    }
}
