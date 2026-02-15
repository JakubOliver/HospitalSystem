package hospitalsystem.calendar;

import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.util.Exportable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

import static hospitalsystem.util.Math.numberOfDigits;

/**
 * Appointment of patient and doctor in specific time and length.
 */
public class Appointment implements Comparable<Appointment>, Exportable {
    public final int id;

    public final int patientId;
    public final int doctorId;

    public final String department;

    public LocalDateTime startTime;
    public LocalDateTime endTime;

    /**
     * Creates new appointment.
     *
     * @param id Identifier of appointment.
     * @param patientId Patients identifier.
     * @param DoctorId Doctors identifier.
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

    public Appointment(ResultSet result) throws SQLException {
        this.id = result.getInt("id");
        this.patientId = result.getInt("patient_id");
        this.doctorId = result.getInt("doctor_id");

        this.department = result.getString("department");

        this.startTime = LocalDateTime.parse(result.getString("start_time"));
        this.endTime = LocalDateTime.parse(result.getString("end_time"));
    }

    @Override
    public int compareTo(Appointment o) {
        if (this.startTime.isEqual(o.startTime)) {
            return this.endTime.compareTo(o.endTime);
        }

        return this.startTime.compareTo(o.startTime);
    }

    //TODO: konstruktor s delkou

    public String getStringForPart(int part){
        String prefix = "";
        int length = Math.toIntExact((Duration.between(startTime, endTime).toMinutes() / 30) * 6);

        if (part == 0){
            int idDigits = numberOfDigits(doctorId);

            int nameLength = Math.min(department.length(), length - 3 - idDigits);

            return prefix + department.substring(0, nameLength) + " ".repeat(length - nameLength - idDigits) + doctorId;
        } else if (part == 1){
            int idDigits = numberOfDigits(patientId);
            int emptySize = (length - idDigits) / 2;

            return prefix + " ".repeat(emptySize) + "\u001b[31m" + patientId + "\u001b[0m" + " ".repeat(length - idDigits - emptySize);
        } else {
            return prefix + startTime.toLocalTime().toString() + " ".repeat(length - 10) + endTime.toLocalTime().toString();
        }
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
}
