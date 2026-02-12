package hospitalsystem;

import hospitalsystem.calendar.Calendar;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.PersonPacket;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;

import java.util.ArrayList;
import java.util.List;

/**
 * Hospital system for managing patients, doctors and appointments.
 */
public class Hospital {
    private final Database database;

    /**
     * Creates Hospital which content is based on the data in provided database.
     *
     * @param databasePath Path to the database.
     */
    public Hospital(String databasePath){
        database = new Database(databasePath);
    }

    /**
     * Adds new patient into hospital system.
     *
     * @param patientData Patients data which describes new patient.
     */
    public GeneralPacket addPatient(PatientData patientData) {
        //TODO: validate

        try {
            return new PersonPacket(database.addPatient(patientData));
        } catch (DatabaseException e){
            return new GeneralPacket(false, e.getMessage());
        }
    }

    /**
     * Returns info about patient with provided id.
     *
     * @param id Id that identifies patient.
     * @return info about patient.
     */
    public String getPatientInfo(int id){
        try{
            return database.getPatient(id).toString();
        } catch (DatabaseException e){
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns list of information about every patient in the system.
     *
     * @return list of information about every patient in the system.
     */
    public List<String> findAllPatient(){
        try{
            List<Patient> patients = database.getAllPatients();

            return patients.stream().map(Patient::toString).toList();
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Adds new doctor into the system.
     *
     * @param doctorData Doctor data that describes the new doctor.
     */
    public void addDoctor(DoctorData doctorData) {
        //TODO: validate
        try {
            database.addDoctor(doctorData);

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds new appointment into the system.
     *
     * @param appointmentData Calendar Entry data that describes the new appointment.
     */
    public void addAppointment(AppointmentData appointmentData) {
        //TODO: validate

        try {
            database.addAppointment(
                    appointmentData.patientsId(),
                    appointmentData.doctorsId(),
                    appointmentData.starTime(),
                    appointmentData.endTime()
            );

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void showCalendar(){
        try {
            Calendar calendar = database.getCalendar();

            System.out.print(calendar);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
