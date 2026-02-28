package cz.cuni.kubinja.hospitalsystem.calendar.util;

import cz.cuni.kubinja.hospitalsystem.calendar.Appointment;

/**
 * Functional interface for appointment comparating functions.
 */
public interface AppointmentCompare {
    /**
     * Compares two appointments.
     *
     * @param a1 First appointment.
     * @param a2 Second appointment.
     * @return Decided comparing based on implementation.
     */
    boolean compare(Appointment a1, Appointment a2);
}
