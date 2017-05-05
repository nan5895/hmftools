package com.hartwig.hmftools.patientdb.data;

import java.time.LocalDate;

import org.jetbrains.annotations.Nullable;

public class PatientInfo {
    @Nullable
    private final String cpctId;
    @Nullable
    private final LocalDate registrationDate;
    @Nullable
    private final String gender;
    @Nullable
    private final String ethnicity;
    @Nullable
    private final String hospital;
    @Nullable
    private final Integer birthYear;
    @Nullable
    private final String tumorLocation;
    @Nullable
    private final LocalDate deathDate;

    public PatientInfo(@Nullable final String cpctId, @Nullable final LocalDate registrationDate,
            @Nullable final String gender, @Nullable final String ethnicity, @Nullable final String hospital,
            @Nullable final Integer birthYear, @Nullable final String tumorLocation,
            @Nullable final LocalDate deathDate) {
        this.cpctId = cpctId;
        this.registrationDate = registrationDate;
        this.gender = gender;
        this.ethnicity = ethnicity;
        this.hospital = hospital;
        this.birthYear = birthYear;
        this.tumorLocation = tumorLocation;
        this.deathDate = deathDate;
    }

    @Nullable
    public String cpctId() {
        return cpctId;
    }

    @Nullable
    public LocalDate registrationDate() {
        return registrationDate;
    }

    @Nullable
    public String gender() {
        return gender;
    }

    @Nullable
    public String ethnicity() {
        return ethnicity;
    }

    @Nullable
    public String hospital() {
        return hospital;
    }

    @Nullable
    public Integer birthYear() {
        return birthYear;
    }

    @Nullable
    public LocalDate deathDate() {
        return deathDate;
    }

    @Nullable
    public String tumorLocation() {
        return tumorLocation;
    }
}
