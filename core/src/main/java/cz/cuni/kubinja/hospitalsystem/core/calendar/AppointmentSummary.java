package cz.cuni.kubinja.hospitalsystem.core.calendar;

import java.time.LocalDateTime;

/**
 * Immutable appointment data enriched with patient and doctor names.
 *
 * @param id Appointment identifier.
 * @param patientId Patient identifier.
 * @param patientFirstName Patient first name.
 * @param patientLastName Patient last name.
 * @param doctorId Doctor identifier.
 * @param doctorFirstName Doctor first name.
 * @param doctorLastName Doctor last name.
 * @param department Department in which the appointment takes place.
 * @param startTime Appointment starting time.
 * @param endTime Appointment ending time.
 */
public record AppointmentSummary(
        int id,
        int patientId,
        String patientFirstName,
        String patientLastName,
        int doctorId,
        String doctorFirstName,
        String doctorLastName,
        String department,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    /**
     * Returns the patient's full name.
     *
     * @return Patient's full name.
     */
    public String patientName() {
        return patientFirstName + " " + patientLastName;
    }

    /**
     * Returns the doctor's full name.
     *
     * @return Doctor's full name.
     */
    public String doctorName() {
        return doctorFirstName + " " + doctorLastName;
    }
}
