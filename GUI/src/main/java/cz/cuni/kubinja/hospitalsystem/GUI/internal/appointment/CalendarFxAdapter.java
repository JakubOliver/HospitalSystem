package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts presentation-independent appointments into CalendarFX models.
 */
final class CalendarFxAdapter {
    private CalendarFxAdapter() {}

    static CalendarSource createSource(
            List<AppointmentSummary> appointments,
            String selectedDepartment
    ) {
        Map<String, Calendar<AppointmentSummary>> calendars = new TreeMap<>(
                String.CASE_INSENSITIVE_ORDER
        );
        appointments.stream()
                .filter(appointment -> selectedDepartment == null
                        || appointment.department().equals(selectedDepartment))
                .forEach(appointment -> {
                    Calendar<AppointmentSummary> calendar = calendars.computeIfAbsent(
                            appointment.department(),
                            department -> createCalendar(department, calendars.size())
                    );
                    calendar.addEntry(createEntry(appointment));
                });

        CalendarSource source = new CalendarSource(
                selectedDepartment == null
                        ? "Hospital departments"
                        : selectedDepartment
        );
        source.getCalendars().addAll(calendars.values());
        return source;
    }

    private static Calendar<AppointmentSummary> createCalendar(
            String department,
            int index
    ) {
        Calendar<AppointmentSummary> calendar = new Calendar<>(department);
        Calendar.Style[] styles = Calendar.Style.values();
        calendar.setStyle(styles[index % styles.length]);
        calendar.setReadOnly(true);
        return calendar;
    }

    private static Entry<AppointmentSummary> createEntry(
            AppointmentSummary appointment
    ) {
        Entry<AppointmentSummary> entry = new Entry<>(
                appointment.patientName() + " / Dr. " + appointment.doctorLastName()
        );
        entry.setId(Integer.toString(appointment.id()));
        entry.setLocation(appointment.department());
        entry.setUserObject(appointment);
        entry.setInterval(
                appointment.startTime(),
                appointment.endTime(),
                ZoneId.systemDefault()
        );
        return entry;
    }
}
