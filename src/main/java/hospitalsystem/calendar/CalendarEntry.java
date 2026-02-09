package hospitalsystem.calendar;

import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;

import java.time.LocalDateTime;

public class CalendarEntry {
    private final int id;

    private final int patientId;
    private final int doctorId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public CalendarEntry(int id, int patientId, int DoctorId,  LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;

        this.patientId = patientId;
        this.doctorId = DoctorId;

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CalendarEntry(int id, Patient patient, Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) {
        this(id, patient.getId(), doctor.getId(), startTime, endTime);
    }
}
