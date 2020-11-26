package com.hartwig.hmftools.common.serve.classification.matchers;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.jetbrains.annotations.NotNull;

class CombinedMatcher implements EventMatcher {

    private static final Map<String, Set<String>> COMBINED_EVENTS_PER_GENE = Maps.newHashMap();

    static {
        COMBINED_EVENTS_PER_GENE.put("EGFR", Sets.newHashSet("Ex19 del L858R"));
        COMBINED_EVENTS_PER_GENE.put("BRAF", Sets.newHashSet("p61BRAF-V600E", "V600E AMPLIFICATION"));
    }

    public CombinedMatcher() {
    }

    @Override
    public boolean matches(@NotNull String gene, @NotNull String event) {
        Set<String> entriesForGene = COMBINED_EVENTS_PER_GENE.get(gene);
        if (entriesForGene != null) {
            if (entriesForGene.contains(event)) {
                return true;
            }
        }

        if ((event.contains(",") || event.contains(";")) && !event.toLowerCase().contains(" or ")) {
            return true;
        } else if (event.contains("+") && !event.toLowerCase().contains("c.") && !event.contains(">")) {
            return true;
        } else if (event.contains("/")) {
            return false;
        } else if (event.trim().contains(" ")) {
            String[] parts = event.trim().replace("  ", " ").split(" ");
            if (FusionPairMatcher.isFusionPair(parts[0])) {
                // Hotspots or amplifications on fusion genes are considered combined.
                return HotspotMatcher.isProteinAnnotation(parts[1]) || AmplificationMatcher.isAmplification(parts[1]);
            }
        }

        return false;
    }
}
