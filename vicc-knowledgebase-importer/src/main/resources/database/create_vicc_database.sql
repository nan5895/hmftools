SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS viccEntry;
DROP TABLE IF EXISTS gene;
DROP TABLE IF EXISTS geneIdentifier;
DROP TABLE IF EXISTS featureName;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS devTag;
DROP TABLE IF EXISTS feature;
DROP TABLE IF EXISTS provenance;
DROP TABLE IF EXISTS synonym;
DROP TABLE IF EXISTS link;
DROP TABLE IF EXISTS sequenceOntology;
DROP TABLE IF EXISTS hierarchy;
DROP TABLE IF EXISTS association;
DROP TABLE IF EXISTS evidence;
DROP TABLE IF EXISTS evidenceInfo;
DROP TABLE IF EXISTS evidenceType;
DROP TABLE IF EXISTS publicationUrl;
DROP TABLE IF EXISTS phenotype;
DROP TABLE IF EXISTS phenotypeType;
DROP TABLE IF EXISTS environmentalContext;
DROP TABLE IF EXISTS approvedCountry;
DROP TABLE IF EXISTS taxonomy;
DROP TABLE IF EXISTS sage;
DROP TABLE IF EXISTS brcaPart1;
DROP TABLE IF EXISTS brcaPart2;
DROP TABLE IF EXISTS cgi;
DROP TABLE IF EXISTS cgicDNA;
DROP TABLE IF EXISTS cgiIndividualMutation;
DROP TABLE IF EXISTS cgigDNA;
DROP TABLE IF EXISTS cgiTranscript;
DROP TABLE IF EXISTS cgiStrand;
DROP TABLE IF EXISTS cgiInfo;
DROP TABLE IF EXISTS cgiRegion;
DROP TABLE IF EXISTS jax;
DROP TABLE IF EXISTS jaxMolecularProfile;
DROP TABLE IF EXISTS jaxTherapy;
DROP TABLE IF EXISTS jaxIndications;
DROP TABLE IF EXISTS jaxReferences;
DROP TABLE IF EXISTS jaxTrials;
DROP TABLE IF EXISTS jaxTrialsIndications;
DROP TABLE IF EXISTS jaxTrialsVariantRequirementDetails;
DROP TABLE IF EXISTS jaxTrialsMolecularProfile;
DROP TABLE IF EXISTS jaxTrialsTherapies;
DROP TABLE IF EXISTS pmkb;
DROP TABLE IF EXISTS pmkbTissue;
DROP TABLE IF EXISTS pmkbTumor;
DROP TABLE IF EXISTS pmkbVariant;
DROP TABLE IF EXISTS pmkbGene;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE viccEntry
(   id int NOT NULL AUTO_INCREMENT,
    source varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE gene
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    geneName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE geneIdentifier
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    symbol varchar(255) NOT NULL,
    entrezId varchar(255) NOT NULL,
    ensemblGeneId varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);


CREATE TABLE featureName
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    nameOfFeature varchar(2500),
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE tag
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    tagName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE devTag
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    devTagName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE feature
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    name varchar(1000),
    biomarkerType varchar(255),
    referenceName varchar(255),
    chromosome varchar(255),
    start varchar(255),
    end varchar(255),
    ref varchar(1000),
    alt varchar(255),
    provenanceRule varchar(255),
    geneSymbol varchar(255),
    entrezId varchar(255),
    description varchar(2000),
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE provenance
(   id int NOT NULL AUTO_INCREMENT,
    featureId int NOT NULL,
    provenanceName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (featureId) REFERENCES feature(id)
);

CREATE TABLE synonym
(   id int NOT NULL AUTO_INCREMENT,
    featureId int NOT NULL,
    synonymName varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (featureId) REFERENCES feature(id)
);

CREATE TABLE link
(   id int NOT NULL AUTO_INCREMENT,
    featureId int NOT NULL,
    linkName varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (featureId) REFERENCES feature(id)
);

CREATE TABLE sequenceOntology
(   id int NOT NULL AUTO_INCREMENT,
    featureId int NOT NULL,
    soid varchar(255) NOT NULL,
    parentSoid varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    parentName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (featureId) REFERENCES feature(id)
);

CREATE TABLE hierarchy
(   id int NOT NULL AUTO_INCREMENT,
    sequenceOntologyId int NOT NULL,
    hierarchyName varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (sequenceOntologyId) REFERENCES sequenceOntology(id)
);

CREATE TABLE association
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    variantName varchar(255),
    evidenceLevel varchar(255),
    evidenceLabel varchar(255),
    responseType varchar(255),
    drugLabel varchar(2000),
    sourceLink varchar(255),
    description varchar(2500) NOT NULL,
    oncogenic varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE evidence
(   id int NOT NULL AUTO_INCREMENT,
    associationId int NOT NULL,
    description varchar(2000),
    PRIMARY KEY (id),
    FOREIGN KEY (associationId) REFERENCES association(id)
);

CREATE TABLE evidenceInfo
(   id int NOT NULL AUTO_INCREMENT,
    evidenceId int NOT NULL,
    publication varchar(225) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (evidenceId) REFERENCES evidence(id)
);

CREATE TABLE evidenceType
(   id int NOT NULL AUTO_INCREMENT,
    evidenceId int NOT NULL,
    sourceName varchar(225) NOT NULL,
    idEvidenceType varchar(225),
    PRIMARY KEY (id),
    FOREIGN KEY (evidenceId) REFERENCES evidence(id)
);

CREATE TABLE publicationUrl
(   id int NOT NULL AUTO_INCREMENT,
    associationId int NOT NULL,
    urlOfPublication varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (associationId) REFERENCES association(id)
);

CREATE TABLE phenotype
(   id int NOT NULL AUTO_INCREMENT,
    associationId int NOT NULL,
    description varchar(255) NOT NULL,
    family varchar(255) NOT NULL,
    idPhenotype varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (associationId) REFERENCES association(id)
);

CREATE TABLE phenotypeType
(   id int NOT NULL AUTO_INCREMENT,
    phenotypeId int NOT NULL,
    source varchar(255),
    term varchar(255) NOT NULL,
    idPhenotypeType varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (phenotypeId) REFERENCES phenotype(id)
);

CREATE TABLE environmentalContext
(   id int NOT NULL AUTO_INCREMENT,
    associationId int NOT NULL,
    term varchar(255),
    description varchar(255),
    source varchar(255),
    usanStem varchar(255),
    idEnvironmentalContext varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (associationId) REFERENCES association(id)
);

CREATE TABLE approvedCountry
(   id int NOT NULL AUTO_INCREMENT,
    environmentContextId int NOT NULL,
    approvedCountryName varchar(225) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (environmentContextId) REFERENCES environmentalContext(id)
);

CREATE TABLE taxonomy
(   id int NOT NULL AUTO_INCREMENT,
    environmentContextId int NOT NULL,
    kingdom varchar(225) NOT NULL,
    directParent varchar(225) NOT NULL,
    class varchar(225) NOT nULL,
    subClass varchar(225),
    superClass varchar(225) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (environmentContextId) REFERENCES environmentalContext(id)
);

CREATE TABLE sage
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    entrezId varchar(255) NOT NULL,
    clinicalManifestation varchar(255) NOT NULL,
    publicationUrl varchar(255) NOT NULL,
    germlineOrSomatic varchar(255) NOT NULL,
    evidenceLabel varchar(255) NOT NULL,
    drugLabel varchar(255) NOT NULL,
    responseType varchar(255) NOT NULL,
    gene varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE pmkb
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE pmkbTissue
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    idTissue varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE pmkbTumor
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    idTumor varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE pmkbVariant
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    aminoAcidChange varchar(255),
    germline varchar(255),
    partnerGene varchar(255),
    codons varchar(255),
    description varchar(255),
    exons varchar(255),
    notes varchar(255),
    cosmic varchar(255),
    effect varchar(255),
    cnvType varchar(255),
    idVariant varchar(255),
    cytoband varchar(255),
    variantType varchar(255),
    dnaChange varchar(255),
    coordinates varchar(255),
    chromosomeBasedCnv varchar(255),
    transcript varchar(255),
    descriptionType varchar(255),
    chromosome varchar(255),
    name varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE pmkbGene
(   id int NOT NULL AUTO_INCREMENT,
    pmkbVariantId int NOT NULL,
    description varchar(255),
    createdAt varchar(255) NOT NULL,
    updatedAt varchar(255) NOT NULL,
    activeInd varchar(255) NOT NULL,
    externalId varchar(255) NOT NULL,
    idGene varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (pmkbVariantId) REFERENCES pmkbVariant(id)
);

CREATE TABLE jaxTrials
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    title varchar(500) NOT NULL,
    gender varchar(255),
    nctId varchar(255) NOT NULL,
    sponsors varchar(255) NOT NULL,
    recruitment varchar(255) NOT NULL,
    variantRequirements varchar(255) NOT NULL,
    updateDate varchar(255) NOT NULL,
    phase varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE jaxTrialsIndications
(   id int NOT NULL AUTO_INCREMENT,
    jaxTrialsId int NOT NULL,
    source varchar(255) NOT NULL,
    idIndications varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxTrialsId) REFERENCES jaxTrials(id)
);

CREATE TABLE jaxTrialsVariantRequirementDetails
(   id int NOT NULL AUTO_INCREMENT,
    jaxTrialsId int NOT NULL,
    requirementType varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxTrialsId) REFERENCES jaxTrials(id)
);

CREATE TABLE jaxTrialsMolecularProfile
(   id int NOT NULL AUTO_INCREMENT,
    jaxTrialsVariantRequirementDetailsId int NOT NULL,
    profileName varchar(255) NOT NULL,
    idMolecularProfile varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxTrialsVariantRequirementDetailsId) REFERENCES jaxTrialsVariantRequirementDetails(id)
);

CREATE TABLE jaxTrialsTherapies
(   id int NOT NULL AUTO_INCREMENT,
    jaxTrialsId int NOT NULL,
    idTherapies varchar(255) NOT NULL,
    therapyName varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxTrialsId) REFERENCES jaxTrials(id)
);

CREATE TABLE jax
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    responseType varchar(255) NOT NULL,
    approvalStatus varchar(255) NOT NULL,
    evidenceType varchar(255) NOT NULL,
    efficacyEvidence varchar(1000) NOT NULL,
    idJaxSource varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE jaxMolecularProfile
(   id int NOT NULL AUTO_INCREMENT,
    jaxId int NOT NULL,
    profileName varchar(255) NOT NULL,
    idMolecularProfile varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxId) REFERENCES jax(id)
);

CREATE TABLE jaxTherapy
(   id int NOT NULL AUTO_INCREMENT,
    jaxId int NOT NULL,
    therapyName varchar(255) NOT NULL,
    idTherapy varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxId) REFERENCES jax(id)
);

CREATE TABLE jaxIndications
(   id int NOT NULL AUTO_INCREMENT,
    jaxId int NOT NULL,
    source varchar(255) NOT NULL,
    idIndications varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (jaxId) REFERENCES jax(id)
);

CREATE TABLE jaxReferences
(   id int NOT NULL AUTO_INCREMENT,
    jaxId int NOT NULL,
    url varchar(255),
    idReferences varchar(255),
    pubMedId varchar(255),
    title varchar(500),
    PRIMARY KEY (id),
    FOREIGN KEY (jaxId) REFERENCES jax(id)
);

CREATE TABLE cgi
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    targeting varchar(255) NOT NULL,
    source varchar(255) NOT NULL,
    primaryTumorType varchar(255) NOT NULL,
    drugsFullName varchar(255) NOT NULL,
    curator varchar(255) NOT NULL,
    drugFamily varchar(255) NOT NULL,
    alteration varchar(1000) NOT NULL,
    drug varchar(255) NOT NULL,
    biomarker varchar(1000) NOT NULL,
    drugStatus varchar(255) NOT NULL,
    gene varchar(255) NOT NULL,
    assayType varchar(255) NOT NULL,
    alterationType varchar(255) NOT NULL,
    evidenceLevel varchar(255) NOT NULL,
    association varchar(255) NOT NULL,
    metastaticTumorType varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE cgicDNA
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    cDNA varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgiIndividualMutation
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    individualMutation varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgigDNA
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    gDNA varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgiTranscript
( id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    transcript varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgiStrand
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    strand varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgiInfo
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    info varchar(500) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE cgiRegion
(   id int NOT NULL AUTO_INCREMENT,
    cgiId int NOT NULL,
    region varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cgiId) REFERENCES cgi(id)
);

CREATE TABLE brcaPart1
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    Variant_frequency_LOVD TINYTEXT NOT NULL,
    Allele_frequency_FIN_ExAC TINYTEXT NOT NULL,
    ClinVarAccession_ENIGMA TINYTEXT NOT NULL,
    Homozygous_count_AFR_ExAC TINYTEXT NOT NULL,
    BX_ID_ExAC TINYTEXT NOT NULL,
    Variant_in_LOVD TINYTEXT NOT NULL,
    Allele_frequency_AFR_ExAC TINYTEXT NOT NULL,
    DBID_LOVD TINYTEXT NOT NULL,
    Chr TINYTEXT NOT NULL,
    BX_ID_ENIGMA TINYTEXT NOT NULL,
    Co_occurrence_LR_exLOVD TINYTEXT NOT NULL,
    Homozygous_count_EAS_ExAC TINYTEXT NOT NULL,
    Submitter_ClinVar TEXT NOT NULL,
    Allele_frequency_EAS_ExAC TINYTEXT NOT NULL,
    Hg37_End TINYTEXT NOT NULL,
    Submitters_LOVD TEXT NOT NULL,
    Clinical_classification_BIC TINYTEXT NOT NULL,
    Homozygous_count_NFE_ExAC TINYTEXT NOT NULL,
    Allele_count_SAS_ExAC TINYTEXT NOT NULL,
    Method_ClinVar TINYTEXT NOT NULL,
    Allele_count_NFE_ExAC TINYTEXT NOT NULL,
    Pathogenicity_all TINYTEXT NOT NULL,
    Germline_or_Somatic_BIC TINYTEXT NOT NULL,
    Homozygous_count_SAS_ExAC TINYTEXT NOT NULL,
    BIC_Nomenclature TINYTEXT NOT NULL,
    Assertion_method_ENIGMA TINYTEXT NOT NULL,
    Literature_source_exLOVD TINYTEXT NOT NULL,
    Change_Type_id TINYTEXT NOT NULL,
    Collection_method_ENIGMA TINYTEXT NOT NULL,
    Sum_family_LR_exLOVD TINYTEXT NOT NULL,
    HGVS_cDNA_LOVD TINYTEXT NOT NULL,
    Homozygous_count_FIN_ExAC TINYTEXT NOT NULL,
    EAS_Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    Ethnicity_BIC TEXT NOT NULL,
    Individuals_LOVD TINYTEXT NOT NULL,
    Variant_in_ExAC TINYTEXT NOT NULL,
    URL_ENIGMA TINYTEXT NOT NULL,
    Allele_Origin_ClinVar TINYTEXT NOT NULL,
    Allele_frequency_AMR_ExAC TINYTEXT NOT NULL,
    Variant_in_1000_Genomes TINYTEXT NOT NULL,
    AFR_Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    BX_ID_exLOVD TINYTEXT NOT NULL,
    Source TINYTEXT NOT NULL,
    Condition_ID_value_ENIGMA TINYTEXT NOT NULL,
    HGVS_Protein TINYTEXT NOT NULL,
    Ref TINYTEXT NOT NULL,
    Allele_number_AFR_ExAC TINYTEXT NOT NULL,
    Allele_count_AFR_ExAC TINYTEXT NOT NULL,
    BX_ID_LOVD TINYTEXT NOT NULL,
    Synonyms TEXT NOT NULL,
    Gene_Symbol TINYTEXT NOT NULL,
    Comment_on_clinical_significance_ENIGMA TEXT NOT NULL,
    Missense_analysis_prior_probability_exLOVD TINYTEXT NOT NULL,
    Allele_number_FIN_ExAC TINYTEXT NOT NULL,
    Posterior_probability_exLOVD TINYTEXT NOT NULL,
    Polyphen_Score TINYTEXT NOT NULL,
    Reference_Sequence TINYTEXT NOT NULL,
    Allele_count_EAS_ExAC TINYTEXT NOT NULL,
    Hg38_End TINYTEXT NOT NULL,
    HGVS_cDNA TINYTEXT NOT NULL,
    Functional_analysis_technique_LOVD TINYTEXT NOT NULL,
    SAS_Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    RNA_LOVD TINYTEXT NOT NULL,
    Combined_prior_probablility_exLOVD TINYTEXT NOT NULL,
    BX_ID_ClinVar TINYTEXT NOT NULL,
    IARC_class_exLOVD TINYTEXT NOT NULL,
    BX_ID_BIC varchar(12500) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
);

CREATE TABLE brcaPart2
(   id int NOT NULL AUTO_INCREMENT,
    viccEntryId int NOT NULL,
    Sift_Prediction TINYTEXT NOT NULL,
    Allele_number_NFE_ExAC TINYTEXT NOT NULL,
    Allele_origin_ENIGMA TINYTEXT NOT NULL,
    Allele_number_OTH_ExAC TINYTEXT NOT NULL,
    Hg36_End TINYTEXT NOT NULL,
    Allele_frequency_SAS_ExAC TINYTEXT NOT NULL,
    Date_Last_Updated_ClinVar TEXT NOT NULL,
    Allele_number_EAS_ExAC TINYTEXT NOT NULL,
    Allele_frequency_OTH_ExAC TINYTEXT NOT NULL,
    Source_URL TEXT NOT NULL,
    SCV_ClinVar TEXT NOT NULL,
    Pathogenicity_expert TINYTEXT NOT NULL,
    Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    Functional_analysis_result_LOVD TINYTEXT NOT NULL,
    AMR_Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    Variant_in_ESP TINYTEXT NOT NULL,
    Variant_in_BIC TINYTEXT NOT NULL,
    Clinical_significance_ENIGMA TINYTEXT NOT NULL,
    Max_Allele_Frequency TINYTEXT NOT NULL,
    Allele_count_AMR_ExAC TINYTEXT NOT NULL,
    Variant_in_ENIGMA TINYTEXT NOT NULL,
    BX_ID_ESP TINYTEXT NOT NULL,
    Patient_nationality_BIC TEXT NOT NULL,
    BX_ID_1000_Genomes TINYTEXT NOT NULL,
    Genomic_Coordinate_hg37 TINYTEXT NOT NULL,
    Genomic_Coordinate_hg36 TINYTEXT NOT NULL,
    EUR_Allele_frequency_1000_Genomes TINYTEXT NOT NULL,
    Number_of_family_member_carrying_mutation_BIC TINYTEXT NOT NULL,
    Segregation_LR_exLOVD TINYTEXT NOT NULL,
    Allele_Frequency TINYTEXT NOT NULL,
    Minor_allele_frequency_percent_ESP TINYTEXT NOT NULL,
    Allele_frequency_ExAC TINYTEXT NOT NULL,
    Mutation_type_BIC TINYTEXT NOT NULL,
    Assertion_method_citation_ENIGMA TINYTEXT NOT NULL,
    Condition_ID_type_ENIGMA TINYTEXT NOT NULL,
    Allele_count_OTH_ExAC TINYTEXT NOT NULL,
    HGVS_protein_LOVD TINYTEXT NOT NULL,
    Variant_in_ClinVar TINYTEXT NOT NULL,
    Clinical_importance_BIC TINYTEXT NOT NULL,
    Discordant TINYTEXT NOT NULL,
    Allele_count_FIN_ExAC TINYTEXT NOT NULL,
    Condition_category_ENIGMA TINYTEXT NOT NULL,
    Allele_Frequency_ESP TINYTEXT NOT NULL,
    Homozygous_count_OTH_ExAC TINYTEXT NOT NULL,
    Genetic_origin_LOVD TINYTEXT NOT NULL,
    Homozygous_count_AMR_ExAC TINYTEXT NOT NULL,
    Clinical_Significance_ClinVar TINYTEXT NOT NULL,
    AA_Allele_Frequency_ESP TINYTEXT NOT NULL,
    Protein_Change TINYTEXT NOT NULL,
    Variant_in_exLOVD TINYTEXT NOT NULL,
    EA_Allele_Frequency_ESP TINYTEXT NOT NULL,
    HGVS_RNA TINYTEXT NOT NULL,
    Clinical_significance_citations_ENIGMA TINYTEXT NOT NULL,
    Variant_effect_LOVD TINYTEXT NOT NULL,
    Polyphen_Prediction TINYTEXT NOT NULL,
    Data_Release_id TINYTEXT NOT NULL,
    Hg37_Start TINYTEXT NOT NULL,
    Hg36_Start TINYTEXT NOT NULL,
    Sift_Score TINYTEXT NOT NULL,
    Genomic_Coordinate_hg38 TINYTEXT NOT NULL,
    Alt TINYTEXT NOT NULL,
    Literature_citation_BIC TEXT NOT NULL,
    Variant_haplotype_LOVD TINYTEXT NOT NULL,
    Allele_frequency_NFE_ExAC TINYTEXT NOT NULL,
    Hg38_Start TINYTEXT NOT NULL,
    Pos TINYTEXT NOT NULL,
    Date_last_evaluated_ENIGMA TINYTEXT NOT NULL,
    Allele_number_SAS_ExAC TINYTEXT NOT NULL,
    Allele_number_AMR_ExAC TINYTEXT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (viccEntryId) REFERENCES viccEntry(id)
    );
