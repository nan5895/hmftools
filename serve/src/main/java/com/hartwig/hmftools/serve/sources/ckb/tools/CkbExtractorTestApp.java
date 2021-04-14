package com.hartwig.hmftools.serve.sources.ckb.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.hartwig.hmftools.ckb.JsonDatabaseToCkbEntryConverter;
import com.hartwig.hmftools.ckb.classification.CkbClassificationConfig;
import com.hartwig.hmftools.ckb.datamodel.CkbEntry;
import com.hartwig.hmftools.ckb.json.CkbJsonDatabase;
import com.hartwig.hmftools.ckb.json.CkbJsonReader;
import com.hartwig.hmftools.common.drivercatalog.panel.DriverGene;
import com.hartwig.hmftools.common.drivercatalog.panel.DriverGeneFile;
import com.hartwig.hmftools.common.fusion.KnownFusionCache;
import com.hartwig.hmftools.common.genome.genepanel.HmfGenePanelSupplier;
import com.hartwig.hmftools.common.genome.refgenome.RefGenomeVersion;
import com.hartwig.hmftools.common.refseq.RefSeq;
import com.hartwig.hmftools.common.refseq.RefSeqFile;
import com.hartwig.hmftools.serve.ServeConfig;
import com.hartwig.hmftools.serve.ServeLocalConfigProvider;
import com.hartwig.hmftools.serve.curation.DoidLookup;
import com.hartwig.hmftools.serve.curation.DoidLookupFactory;
import com.hartwig.hmftools.serve.extraction.ExtractionResult;
import com.hartwig.hmftools.serve.extraction.ExtractionResultWriter;
import com.hartwig.hmftools.serve.extraction.hotspot.ProteinResolverFactory;
import com.hartwig.hmftools.serve.refgenome.ImmutableRefGenomeResource;
import com.hartwig.hmftools.serve.refgenome.RefGenomeResource;
import com.hartwig.hmftools.serve.sources.ckb.CkbExtractor;
import com.hartwig.hmftools.serve.sources.ckb.CkbExtractorFactory;
import com.hartwig.hmftools.serve.sources.ckb.CkbReader;
import com.hartwig.hmftools.serve.sources.ckb.CkbUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

public class CkbExtractorTestApp {

    private static final Logger LOGGER = LogManager.getLogger(CkbExtractorTestApp.class);

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.DEBUG);

        ServeConfig config = ServeLocalConfigProvider.create();

        Path outputPath = new File(config.outputDir()).toPath();
        if (!Files.exists(outputPath)) {
            LOGGER.debug("Creating {} directory for writing SERVE output", outputPath.toString());
            Files.createDirectory(outputPath);
        }

        DoidLookup doidLookup = DoidLookupFactory.buildFromMappingTsv(config.missingDoidsMappingTsv());

        LOGGER.info("Reading ref seq matching to transcript from {}", config.refSeqTsv());
        List<RefSeq> refSeqMatchFile = RefSeqFile.readingRefSeq(config.refSeqTsv());

        CkbExtractor extractor =
                CkbExtractorFactory.buildCkbExtractor(CkbClassificationConfig.build(), buildRefGenomeResource(config), doidLookup);

        CkbJsonDatabase ckbJsonDatabase = CkbJsonReader.read(config.ckbDir());
        List<CkbEntry> allCkbEntries = JsonDatabaseToCkbEntryConverter.convert(ckbJsonDatabase);

        List<CkbEntry> curatedEntries = CkbReader.filterAndCurate(allCkbEntries);
        ExtractionResult result = extractor.extract(curatedEntries, refSeqMatchFile);

        String eventsTsv = config.outputDir() + File.separator + "CkbEvents.tsv";
        CkbUtils.writeEventsToTsv(eventsTsv, curatedEntries);
        CkbUtils.printExtractionResults(result);

        new ExtractionResultWriter(config.outputDir(), RefGenomeVersion.V38).write(result);
    }

    @NotNull
    private static RefGenomeResource buildRefGenomeResource(@NotNull ServeConfig config) throws IOException {
        LOGGER.debug("Reading driver genes from {}", config.driverGene38Tsv());
        List<DriverGene> driverGenes = DriverGeneFile.read(config.driverGene38Tsv());
        LOGGER.debug(" Read {} driver genes", driverGenes.size());

        LOGGER.debug("Reading known fusions from {}", config.knownFusion38File());
        KnownFusionCache fusionCache = new KnownFusionCache();
        if (!fusionCache.loadFile(config.knownFusion38File())) {
            throw new IllegalStateException("Could not load known fusion cache from " + config.knownFusion38File());
        }
        LOGGER.debug(" Read {} known fusions", fusionCache.getData().size());

        return ImmutableRefGenomeResource.builder()
                .fastaFile(config.refGenome38FastaFile())
                .driverGenes(driverGenes)
                .knownFusionCache(fusionCache)
                .canonicalTranscriptPerGeneMap(HmfGenePanelSupplier.allGenesMap38())
                .proteinResolver(ProteinResolverFactory.dummy())
                .build();
    }
}