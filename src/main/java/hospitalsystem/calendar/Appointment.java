package hospitalsystem.calendar;

import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;

import java.time.LocalDateTime;

/**
 * Appointment of patient and doctor in specific time and length.
 */
public class Appointment {
    private final int id;

    private final int patientId;
    private final int doctorId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Creates new appointment.
     *
     * @param id Identifier of appointment.
     * @param patientId Patients identifier.
     * @param DoctorId Doctors identifier.
     * @param startTime Starting time of the appointment.
     * @param endTime Ending time of the appointment.
     */
    public Appointment(int id, int patientId, int DoctorId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;

        this.patientId = patientId;
        this.doctorId = DoctorId;

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
        this(id, patient.getId(), doctor.getId(), startTime, endTime);
    }

    //TODO: konstruktor s delkou
}
