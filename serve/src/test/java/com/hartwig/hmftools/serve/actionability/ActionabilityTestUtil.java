package com.hartwig.hmftools.serve.actionability;

import java.util.Objects;
import java.util.Set;

import com.google.common.io.Resources;
import com.hartwig.hmftools.common.serve.Knowledgebase;
import com.hartwig.hmftools.common.serve.actionability.EvidenceDirection;
import com.hartwig.hmftools.common.serve.actionability.EvidenceLevel;
import com.hartwig.hmftools.serve.tumorlocation.TumorLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActionabilityTestUtil {

    public static final String TEST_SERVE_OUTPUT_DIR = Resources.getResource("serve_output").getPath();

    private ActionabilityTestUtil() {
    }

    @NotNull
    public static ActionableEvent create(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
            @NotNull String treatment, @NotNull TumorLocation whiteListCancerType, @NotNull Set<TumorLocation> blackListCancerTypes,
            @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
        return new ActionableEventImpl(source,
                sourceEvent,
                sourceUrls,
                treatment,
                whiteListCancerType,
                blackListCancerTypes,
                level,
                direction,
                evidenceUrls);
    }

    private static class ActionableEventImpl implements ActionableEvent {

        @NotNull
        private final Knowledgebase source;
        @NotNull
        private final String sourceEvent;
        @NotNull
        private final Set<String> sourceUrls;
        @NotNull
        private final String treatment;
        @NotNull
        private final TumorLocation whiteListCancerType;
        @NotNull
        private final Set<TumorLocation> blackListCancerTypes;
        @NotNull
        private final EvidenceLevel level;
        @NotNull
        private final EvidenceDirection direction;
        @NotNull
        private final Set<String> evidenceUrls;

        public ActionableEventImpl(@NotNull Knowledgebase source, @NotNull String sourceEvent, @NotNull Set<String> sourceUrls,
                @NotNull String treatment, @NotNull TumorLocation whiteListCancerType, @NotNull Set<TumorLocation> blackListCancerTypes,
                @NotNull EvidenceLevel level, @NotNull EvidenceDirection direction, @NotNull Set<String> evidenceUrls) {
            this.source = source;
            this.sourceEvent = sourceEvent;
            this.sourceUrls = sourceUrls;
            this.treatment = treatment;
            this.whiteListCancerType = whiteListCancerType;
            this.blackListCancerTypes = blackListCancerTypes;
            this.level = level;
            this.direction = direction;
            this.evidenceUrls = evidenceUrls;
        }

        @NotNull
        @Override
        public Knowledgebase source() {
            return source;
        }

        @NotNull
        @Override
        public String sourceEvent() {
            return sourceEvent;
        }

        @NotNull
        @Override
        public Set<String> sourceUrls() {
            return sourceUrls;
        }

        @NotNull
        @Override
        public String treatment() {
            return treatment;
        }

        @NotNull
        @Override
        public TumorLocation whiteListCancerType() {
            return whiteListCancerType;
        }

        @NotNull
        @Override
        public Set<TumorLocation> blackListCancerTypes() {
            return blackListCancerTypes;
        }

        @NotNull
        @Override
        public EvidenceLevel level() {
            return level;
        }

        @NotNull
        @Override
        public EvidenceDirection direction() {
            return direction;
        }

        @NotNull
        @Override
        public Set<String> evidenceUrls() {
            return evidenceUrls;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ActionableEventImpl that = (ActionableEventImpl) o;
            return source == that.source && sourceEvent.equals(source == that.source) && sourceUrls.equals(that.sourceUrls)
                    && treatment.equals(that.treatment) && whiteListCancerType.equals(that.whiteListCancerType)
                    && blackListCancerTypes.equals(that.blackListCancerTypes) && level == that.level && direction == that.direction
                    && evidenceUrls.equals(that.evidenceUrls);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, sourceEvent, sourceUrls, treatment, whiteListCancerType, blackListCancerTypes, level, direction,

                    evidenceUrls);
        }

        @Override
        public String toString() {
            return "ActionableEventImpl{" + "source=" + source + ", sourceEvent='" + sourceEvent + ", sourceUrls=" + sourceUrls
                    + ", treatment='" + treatment + ", whitelistCancerType='" + whiteListCancerType + ", blackListCancerTypes='"
                    + blackListCancerTypes + ", level=" + level + ", direction=" + direction + ", evidenceUrls=" + evidenceUrls + '}';
        }
    }
}