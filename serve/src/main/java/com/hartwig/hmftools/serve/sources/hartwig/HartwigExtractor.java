package com.hartwig.hmftools.serve.sources.hartwig;

import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.serve.Knowledgebase;
import com.hartwig.hmftools.common.variant.hotspot.ImmutableVariantHotspotImpl;
import com.hartwig.hmftools.common.variant.hotspot.VariantHotspot;
import com.hartwig.hmftools.serve.hotspot.HotspotFunctions;
import com.hartwig.hmftools.serve.hotspot.ImmutableKnownHotspot;
import com.hartwig.hmftools.serve.hotspot.KnownHotspot;
import com.hartwig.hmftools.serve.hotspot.ProteinKeyFormatter;
import com.hartwig.hmftools.serve.hotspot.ProteinResolver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class HartwigExtractor {

    private static final Logger LOGGER = LogManager.getLogger(HartwigExtractor.class);

    @NotNull
    private final Knowledgebase source;
    @NotNull
    private final ProteinResolver proteinResolver;
    private final boolean addExplicitHotspots;

    public HartwigExtractor(@NotNull final Knowledgebase source, @NotNull final ProteinResolver proteinResolver,
            final boolean addExplicitHotspots) {
        this.source = source;
        this.proteinResolver = proteinResolver;
        this.addExplicitHotspots = addExplicitHotspots;
    }

    @NotNull
    public List<KnownHotspot> extractFromHartwigEntries(@NotNull List<HartwigEntry> entries) {
        List<KnownHotspot> knownHotspots = Lists.newArrayList();
        for (HartwigEntry entry : entries) {
            List<VariantHotspot> hotspots = Lists.newArrayList();
            if (!entry.proteinAnnotation().isEmpty()) {
                hotspots =
                        proteinResolver.extractHotspotsFromProteinAnnotation(entry.gene(), entry.transcript(), entry.proteinAnnotation());
            }

            if (addExplicitHotspots) {
                VariantHotspot explicitHotspot = toHotspot(entry);
                if (!hotspots.contains(explicitHotspot)) {
                    if (entry.proteinAnnotation().isEmpty()) {
                        LOGGER.debug("Adding hotspot '{}' since protein annotation is not provided", explicitHotspot);
                    } else {
                        LOGGER.info("Adding hotspot '{}' since it was not generated by protein resolving based on '{}'",
                                explicitHotspot,
                                ProteinKeyFormatter.toProteinKey(entry.gene(), entry.transcript(), entry.proteinAnnotation()));
                    }
                    hotspots.add(explicitHotspot);
                }
            }

            for (VariantHotspot hotspot : hotspots) {
                knownHotspots.add(ImmutableKnownHotspot.builder()
                        .from(hotspot)
                        .addSources(source)
                        .gene(entry.gene())
                        .transcript(entry.transcript())
                        .proteinAnnotation(entry.proteinAnnotation())
                        .build());
            }
        }

        List<KnownHotspot> consolidatedHotspots = HotspotFunctions.consolidateHotspots(knownHotspots);
        if (consolidatedHotspots.size() != knownHotspots.size()) {
            LOGGER.warn("Consolidating of '{}' hotspots changed number of hotspots from {} to {}",
                    source,
                    knownHotspots.size(),
                    consolidatedHotspots.size());
        }
        return consolidatedHotspots;
    }

    @NotNull
    private static VariantHotspot toHotspot(@NotNull HartwigEntry entry) {
        return ImmutableVariantHotspotImpl.builder()
                .chromosome(entry.chromosome())
                .position(entry.position())
                .ref(entry.ref())
                .alt(entry.alt())
                .build();
    }
}
