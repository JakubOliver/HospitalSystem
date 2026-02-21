package hospitalsystem.calendar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager of appointments for hospital system.
 */
public class Calendar {
    private final Map<String, Department> departments;

    /** Minimum length of appointment */
    public static int minimumLengthOfAppointment = 60;
    /** Requirement that all appointments have to start and end at *.30 or *.00 (so at half or whole hours) */
    public static int alignmentOfAppointment = 30;
    /** First appointment can start after hour */
    public static int minStartingTime = 8;
    /** Every appointment have to end before this hour */
    public static int maxEndingTime = 16;

    /**
     * Creates empty calendar.
     */
    public Calendar() {
        departments = new HashMap<>();
    }

    /**
     * Imports appointments from SQL resultSet.
     *
     * @param resultSet SQL result containing appointment entries.
     * @throws SQLException Errors occurs while reading SQL results: invalid columns, reading failure.
     */
    public void importData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Appointment appointment = new Appointment(resultSet);

            if (!departments.containsKey(appointment.department)) {
                departments.put(appointment.department, new Department(appointment.department));
            }

            departments.get(appointment.department).addAppointment(appointment);
        }

        computeLayers();
    }

    private void computeLayers(){
        for (Department department : departments.values()) {
            department.computeLayers();
        }
    }

    /**
     * Decided whether the starting and ending time of appointment satisfy requirements such as: minimal length, opening hours, correct aligning etc.
     *
     * @param start Starting time.
     * @param end Ending time.
     * @return Whether the interval satisfy requirements for being appointment.
     * @throws CalendarException Errors occurs when time interval do not satisfy any of the requirements.
     */
    public static boolean timeIsValid(LocalDateTime start, LocalDateTime end) throws CalendarException {
        if (end.isBefore(start))
            throw new CalendarException(CalendarException.invalidOrdering);

        if (Duration.between(start, end).toMinutes() < minimumLengthOfAppointment)
            throw new CalendarException(CalendarException.toShort);

        if (!start.toLocalDate().equals(end.toLocalDate()))
            throw new CalendarException(CalendarException.notSameDay);

        return startAtHalves(start) && startAtHalves(end);
    }

    /**
     * Checks whether the time is in the opening hours and if satisfy alignment (*:30, *:00).
     *
     * @param time Time we want to process.
     * @return Whether the time satisfy requirements.
     * @throws CalendarException Error occurs when time does not satisfy requirements.
     */
    public static boolean startAtHalves(LocalDateTime time) throws CalendarException {
        if (time.getMinute() % alignmentOfAppointment != 0)
            throw new CalendarException(CalendarException.invalidAlignment);

        if (!(minStartingTime <= time.getHour() && time.getHour() <= maxEndingTime))
            throw new CalendarException(CalendarException.invalidTimes);

        return true;
    }

    /**
     * Returns string representing calendar for the provided department.
     *
     * @param department Name of department.
     * @return String representing calendar for the provided department.
     * @throws CalendarException Department with the provided name does not exist.
     */
    public String getDepartmentCalendar(String department) throws CalendarException {
        if (departments.containsKey(department))
            throw  new CalendarException(CalendarException.notValidDepartment);

        return departments.get(department).toString();
    }

    /**
     * Returns list of strings representing graphical version of calendar.
     *
     * @return List of strings representing graphical version of calendar.
     */
    public List<String> getCalendar(){
        return departments.keySet().stream().map(x -> departments.get(x).toString()).toList();
    }

    @Override
    public String toString() {
        return String.join("", getCalendar());
    }

    /**
     * Exports calendar in CSV format into provided file.
     *
     * @param destination File where the data should be exported.
     * @throws IOException Errors occurs when occurs problem with opening and writing into files.
     */
    public void export(File destination) throws IOException {
        List<Appointment> appointments = new ArrayList<>();

        for (Department department : departments.values()){
            appointments.addAll(department.appointments);
        }

        appointments.sort((a1, a2) -> a1.id - a2.id);

        try (FileWriter writer = new FileWriter(destination)){
            for (Appointment appointment : appointments){
                writer.write(appointment.export() + "\n");
            }
        }
    }
}