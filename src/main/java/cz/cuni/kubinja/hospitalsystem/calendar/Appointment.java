package cz.cuni.kubinja.hospitalsystem.calendar;

import cz.cuni.kubinja.hospitalsystem.calendar.util.Parts;
import cz.cuni.kubinja.hospitalsystem.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.util.Exportable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

import static cz.cuni.kubinja.hospitalsystem.util.Math.numberOfDigits;

/**
 * Appointment of patient and doctor in specific time and length.
 */
public class Appointment implements Comparable<Appointment>, Exportable {
    /** Identification number of appointment */
    public final int id;

    /** Identification number of the patient connected with this appointment. */
    public final int patientId;
    /** Identification number of the doctor connected with this appointment. */
    public final int doctorId;

    /** Name of the department in which the appointment is taking place */
    public final String department;

    /** Starting time of the appointment */
    public LocalDateTime startTime;
    /** Ending time of the appointment */
    public LocalDateTime endTime;

    /**
     * Creates new appointment.
     *
     * @param id Identifier of appointment.
     * @param patientId Patients identifier.
     * @param DoctorId Doctors identifier.
     * @param department Department in which the appointment is taking place.
     * @param startTime Starting time of the appointment.
     * @param endTime Ending time of the appointment.
     */
    public Appointment(int id, int patientId, int DoctorId, String department, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;

        this.patientId = patientId;
        this.doctorId = DoctorId;

        this.department = department;

        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates new appointment.
     *
     * @param id Identifier of appointment.
     * @param patient Patient connected to the appointment.
     * @param doctor Doctor connected to the appointment.
     * @param startTime Starting time of the appointment.
     * @param endTime Ending time of the appointment.
     */
    public Appointment(int id, Patient patient, Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        this(id, patient.getId(), doctor.getId(), doctor.getDepartment(), startTime, endTime);
    }

    /**
     * Creates appointment from SQL result set.
     *
     * @param result SQL result set containing data for appointment creation.
     * @throws SQLException Error connected with the invalid columns in result set or failure of retrieving data from result set.
     */
    public Appointment(ResultSet result) throws SQLException {
        this.id = result.getInt("id");
        this.patientId = result.getInt("patient_id");
        this.doctorId = result.getInt("doctor_id");

        this.department = result.getString("department");

        this.startTime = LocalDateTime.parse(result.getString("start_time"));
        this.endTime = LocalDateTime.parse(result.getString("end_time"));
    }

    /**
     * Compares two appointments based on starting and ending time and id.
     *
     * @param o Other appointment.
     * @return Integer which denotes whether this appointment is smaller, bigger or equal then the other one.
     */
    @Override
    public int compareTo(Appointment o) {
        if (!startTime.equals(o.startTime)) {
            return startTime.compareTo(o.startTime);
        }

        if (!endTime.equals(o.endTime)) {
            return endTime.compareTo(o.endTime);
        }

        return id - o.id;
    }

    /**
     * Returns info about appointment for the calendar diagram based on provided required part.
     *
     * @param part Part that is required for drawing calendar diagram.
     * @return Info about appointment for the calendar diagram based on provided required part.
     */
    public String getStringForPart(Parts part){
        String prefix = "";
        int length = Math.toIntExact((Duration.between(startTime, endTime).toMinutes() / 30) * 6);

        return switch (part){
            case TOP -> {
                int idDigits = numberOfDigits(doctorId);

                int nameLength = Math.min(department.length(), length - 3 - idDigits);

                yield  prefix + department.substring(0, nameLength) + " ".repeat(length - nameLength - idDigits) + doctorId;
            }
            case MIDDLE -> {
                int idDigits = numberOfDigits(patientId);
                int emptySize = (length - idDigits) / 2;

                yield prefix + " ".repeat(emptySize) + "\u001b[31m" + patientId + "\u001b[0m" + " ".repeat(length - idDigits - emptySize);
            }
            case BOTTOM -> {
                yield prefix + startTime.toLocalTime().toString() + " ".repeat(length - 10) + endTime.toLocalTime().toString();
            }
        };
    }

    /**
     * Decided whether the time interval is in conflict, if the overlap.
     *
     * @param start Starting time of the interval.
     * @param end Ending time of the interval.
     * @return Whether the time interval is overlapping with the appointment.
     */
    public boolean inConflict(LocalDateTime start, LocalDateTime end){
        return (start.isAfter(startTime) && start.isBefore(endTime)) || (end.isAfter(startTime) && end.isBefore(endTime));
    }

    @Override
    public String export(){
        return String.join(",",
                String.valueOf(id),
                String.valueOf(patientId),
                String.valueOf(doctorId),
                department,
                startTime.toString(),
                endTime.toString());
    }

    @Override
    public String toString(){
        return "id: " + id + ", patientID: " + patientId + ", doctorID: " + doctorId + ", department: " + department + ", startTime: " + startTime.toString() + ", endTime: " + endTime.toString();
    }
}
