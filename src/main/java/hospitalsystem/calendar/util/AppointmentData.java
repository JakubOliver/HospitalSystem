package hospitalsystem.calendar.util;

import java.time.LocalDateTime;

/**
 * Data wrapper for appointment.
 *
 * @param patientsId Identifier of patient connected to the appointment.
 * @param doctorsId Identifier of doctor connected to the appointment.
 * @param starTime Starting time of the appointment.
 * @param endTime Ending time of the appointment.
 */
public record AppointmentData(int patientsId, int doctorsId, LocalDateTime starTime, LocalDateTime endTime) { }
