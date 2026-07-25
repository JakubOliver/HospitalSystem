package cz.cuni.kubinja.hospitalsystem.core;

import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics.Occurrence;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics.PatientVisits;
import cz.cuni.kubinja.hospitalsystem.core.statistics.TopX;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Calculates presentation-independent hospital statistics.
 */
final class HospitalStatisticsCalculator {
    private static final int PATIENT_RANKING_SIZE = 5;
    private static final int OCCURRENCE_RANKING_SIZE = 10;

    private HospitalStatisticsCalculator() {}

    static HospitalStatistics calculate(
            List<Patient> patients,
            List<Doctor> doctors,
            Calendar calendar
    ) {
        int appointmentCount = calendar.size();

        TopX<PatientVisits> topPatientVisits = new TopX<>(
                PATIENT_RANKING_SIZE,
                Comparator.comparingInt(PatientVisits::patientId)
        );
        patients.forEach(patient -> {
            PatientVisits visits = new PatientVisits(
                    patient.getId(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    calendar.numberOfAppearances(patient)
            );
            topPatientVisits.add(visits, visits.appointmentCount());
        });
        List<PatientVisits> patientVisits = topPatientVisits.entries().stream()
                .map(TopX.TopXEntry::data)
                .toList();

        List<Occurrence> commonAnamneses = occurrences(
                patients.stream().map(Patient::getAnamnesis).toList()
        );
        List<Occurrence> commonSpecializations = occurrences(
                doctors.stream().map(Doctor::getSpecialization).toList()
        );

        return new HospitalStatistics(
                patients.size(),
                doctors.size(),
                appointmentCount,
                average(appointmentCount, patients.size()),
                average(appointmentCount, doctors.size()),
                patientVisits,
                commonAnamneses,
                commonSpecializations
        );
    }

    private static List<Occurrence> occurrences(List<String> values) {
        Map<String, Long> counts = values.stream().collect(
                Collectors.groupingBy(Function.identity(), Collectors.counting())
        );

        TopX<String> commonValues = new TopX<>(
                OCCURRENCE_RANKING_SIZE,
                String.CASE_INSENSITIVE_ORDER.thenComparing(Comparator.naturalOrder())
        );
        counts.forEach((value, count) -> commonValues.add(value, count.intValue()));

        return commonValues.entries().stream()
                .map(entry -> new Occurrence(entry.data(), entry.value()))
                .toList();
    }

    private static double average(int appointments, int personnel) {
        return personnel == 0 ? 0 : (double) appointments / personnel;
    }
}
