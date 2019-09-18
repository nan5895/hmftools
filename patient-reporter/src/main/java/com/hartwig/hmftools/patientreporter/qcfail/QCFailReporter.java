package com.hartwig.hmftools.patientreporter.qcfail;

import java.util.Optional;

import com.hartwig.hmftools.common.ecrf.projections.PatientTumorLocation;
import com.hartwig.hmftools.common.ecrf.projections.PatientTumorLocationFunctions;
import com.hartwig.hmftools.patientreporter.SampleMetadata;
import com.hartwig.hmftools.patientreporter.SampleReport;
import com.hartwig.hmftools.patientreporter.SampleReportFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QCFailReporter {

    @NotNull
    private final QCFailReportData reportData;

    public QCFailReporter(@NotNull final QCFailReportData reportData) {
        this.reportData = reportData;
    }

    @NotNull
    public QCFailReport run(@NotNull SampleMetadata sampleMetadata, @NotNull QCFailReason reason, @Nullable String comments,
            @Nullable String correctTitle) {
        QCFailStudy study = QCFailStudy.fromSample(sampleMetadata.tumorSampleId());

        assert study != null;

        PatientTumorLocation patientTumorLocation =
                PatientTumorLocationFunctions.findPatientTumorLocationForSample(reportData.patientTumorLocations(),
                        sampleMetadata.tumorSampleId());

        SampleReport sampleReport = SampleReportFactory.fromLimsAndHospitalModel(sampleMetadata,
                reportData.limsModel(),
                reportData.hospitalModel(),
                patientTumorLocation);

        return ImmutableQCFailReport.of(sampleReport,
                reason,
                study,
                Optional.ofNullable(comments),
                Optional.ofNullable(correctTitle),
                reportData.signaturePath(),
                reportData.logoRVAPath(),
                reportData.logoCompanyPath());
    }
}
