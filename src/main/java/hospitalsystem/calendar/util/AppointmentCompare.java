package hospitalsystem.calendar.util;

import hospitalsystem.calendar.Appointment;

public interface AppointmentCompare {
    boolean compare(Appointment date1, Appointment date2);
}
