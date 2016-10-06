package com.hartwig.hmftools.ecrfanalyser.datamodel;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

public class EcrfField implements Comparable<EcrfField> {

    private static final Logger LOGGER = LogManager.getLogger(EcrfField.class);
    private static final String BIRTH_DATE_IDENTIFIER = "BIRTHDTC";

    @NotNull
    private final String category;
    @NotNull
    private final String fieldName;
    @NotNull
    private final String description;
    @NotNull
    private final Map<Integer, String> values;

    public EcrfField(@NotNull final String category, @NotNull final String fieldName,
            @NotNull final String description, @NotNull final Map<Integer, String> values) {
        this.category = category;
        this.fieldName = fieldName;
        this.description = description;
        this.values = values;
    }

    @NotNull
    public String category() {
        return category;
    }

    @NotNull
    public String fieldName() {
        return fieldName;
    }

    @NotNull
    public String description() {
        return description;
    }

    @NotNull
    public Map<Integer, String> values() {
        return values;
    }

    @NotNull
    public String resolveValue(@NotNull String ecrfValue) {
        String value;
        if (values.size() > 0) {
            if (isInteger(ecrfValue)) {
                value = values.get(Integer.valueOf(ecrfValue));
                if (value == null) {
                    LOGGER.warn("Could not find value in dropdown for " + fieldName + ": " + ecrfValue);
                    value = Strings.EMPTY;
                }
            } else {
                LOGGER.warn("Could not convert value from list to integer for " + fieldName + ": " + ecrfValue);
                value = Strings.EMPTY;
            }
        } else {
            value = ecrfValue;
        }

        if (fieldName.contains(BIRTH_DATE_IDENTIFIER)) {
            if (value.length() < 4) {
                LOGGER.warn("Weird " + BIRTH_DATE_IDENTIFIER + " value: " + value);
            } else {
                value = value.substring(0, 4) + "-01-01";
            }
        }

        return value;
    }

    private static boolean isInteger(@NotNull String integerString) {
        try {
            return Integer.valueOf(integerString) != null;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public int compareTo(@NotNull EcrfField other) {
        if (category.equals(other.category)) {
            return fieldName.compareTo(other.fieldName);
        } else {
            return category.compareTo(other.category);
        }
    }
}
