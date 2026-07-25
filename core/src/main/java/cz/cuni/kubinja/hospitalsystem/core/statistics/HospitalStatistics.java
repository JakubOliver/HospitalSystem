package cz.cuni.kubinja.hospitalsystem.core.statistics;

import java.util.List;

/**
 * Immutable statistics calculated from hospital personnel and appointments.
 *
 * @param patientCount Number of patients.
 * @param doctorCount Number of doctors.
 * @param appointmentCount Number of appointments.
 * @param averageAppointmentsPerPatient Average appointments per patient.
 * @param averageAppointmentsPerDoctor Average appointments per doctor.
 * @param patientsWithMostVisits Up to five patients ordered by appointment count.
 * @param commonAnamneses Up to ten anamneses ordered by occurrence count.
 * @param commonSpecializations Up to ten specializations ordered by occurrence count.
 */
public record HospitalStatistics(
        int patientCount,
        int doctorCount,
        int appointmentCount,
        double averageAppointmentsPerPatient,
        double averageAppointmentsPerDoctor,
        List<PatientVisits> patientsWithMostVisits,
        List<Occurrence> commonAnamneses,
        List<Occurrence> commonSpecializations
) {
    /**
     * Creates an immutable snapshot of hospital statistics.
     */
    public HospitalStatistics {
        patientsWithMostVisits = List.copyOf(patientsWithMostVisits);
        commonAnamneses = List.copyOf(commonAnamneses);
        commonSpecializations = List.copyOf(commonSpecializations);
    }

    /**
     * Appointment count associated with a patient.
     *
     * @param patientId Patient identifier.
     * @param firstName Patient first name.
     * @param lastName Patient last name.
     * @param appointmentCount Number of the patient's appointments.
     */
    public record PatientVisits(
            int patientId,
            String firstName,
            String lastName,
            int appointmentCount
    ) {}

    /**
     * Number of occurrences of a textual value.
     *
     * @param value Counted value.
     * @param count Number of occurrences.
     */
    public record Occurrence(String value, int count) {}
}
