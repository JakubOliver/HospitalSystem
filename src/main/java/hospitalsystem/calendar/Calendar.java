package hospitalsystem.calendar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String department : departments.keySet()){
            sb.append(departments.get(department).toString());
        }

        return sb.toString();
    }

    /**
     * Exports calendar in CSV format into provided file.
     *
     * @param destination File where the data should be exported.
     * @throws IOException Errors occurs when occurs problem with opening and writing into files.
     */
    public void export(File destination) throws IOException {
        //TODO: mozna export seradit podle id
        try (FileWriter writer = new FileWriter(destination)){
            for (Department department : departments.values()){
                for (Appointment appointment : department.appointments){
                    writer.write(appointment.export() + "\n");
                }
            }
        }
    }
}