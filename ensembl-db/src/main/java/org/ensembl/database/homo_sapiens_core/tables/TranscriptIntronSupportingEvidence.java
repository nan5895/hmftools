/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.TranscriptIntronSupportingEvidenceRecord;
import org.jooq.Field;
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
public class TranscriptIntronSupportingEvidence extends TableImpl<TranscriptIntronSupportingEvidenceRecord> {

    private static final long serialVersionUID = 1021688349;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence</code>
     */
    public static final TranscriptIntronSupportingEvidence TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE = new TranscriptIntronSupportingEvidence();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TranscriptIntronSupportingEvidenceRecord> getRecordType() {
        return TranscriptIntronSupportingEvidenceRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence.transcript_id</code>.
     */
    public final TableField<TranscriptIntronSupportingEvidenceRecord, UInteger> TRANSCRIPT_ID = createField("transcript_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence.intron_supporting_evidence_id</code>.
     */
    public final TableField<TranscriptIntronSupportingEvidenceRecord, UInteger> INTRON_SUPPORTING_EVIDENCE_ID = createField("intron_supporting_evidence_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence.previous_exon_id</code>.
     */
    public final TableField<TranscriptIntronSupportingEvidenceRecord, UInteger> PREVIOUS_EXON_ID = createField("previous_exon_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence.next_exon_id</code>.
     */
    public final TableField<TranscriptIntronSupportingEvidenceRecord, UInteger> NEXT_EXON_ID = createField("next_exon_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence</code> table reference
     */
    public TranscriptIntronSupportingEvidence() {
        this("transcript_intron_supporting_evidence", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.transcript_intron_supporting_evidence</code> table reference
     */
    public TranscriptIntronSupportingEvidence(String alias) {
        this(alias, TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE);
    }

    private TranscriptIntronSupportingEvidence(String alias, Table<TranscriptIntronSupportingEvidenceRecord> aliased) {
        this(alias, aliased, null);
    }

    private TranscriptIntronSupportingEvidence(String alias, Table<TranscriptIntronSupportingEvidenceRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<TranscriptIntronSupportingEvidenceRecord> getPrimaryKey() {
        return Keys.KEY_TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TranscriptIntronSupportingEvidenceRecord>> getKeys() {
        return Arrays.<UniqueKey<TranscriptIntronSupportingEvidenceRecord>>asList(Keys.KEY_TRANSCRIPT_INTRON_SUPPORTING_EVIDENCE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TranscriptIntronSupportingEvidence as(String alias) {
        return new TranscriptIntronSupportingEvidence(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TranscriptIntronSupportingEvidence rename(String name) {
        return new TranscriptIntronSupportingEvidence(name, null);
    }
}
