package hospitalsystem;

import hospitalsystem.calendar.util.CalendarEntryData;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
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
    public void addPatient(PatientData patientData) {
        //TODO: validate

        try {
            database.addPatient(
                    patientData.person().firstName(),
                    patientData.person().lastName(),
                    patientData.person().dateOfBirth(),
                    patientData.details().anamnesis()
            ); //TODO: rozbalovani az na urovni databaze

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
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
            database.addDoctor(
                    doctorData.person().firstName(),
                    doctorData.person().lastName(),
                    doctorData.person().dateOfBirth(),
                    doctorData.details().specialization()
            );

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds new appointment into the system.
     *
     * @param calendarEntryData Calendar Entry data that describes the new appointment.
     */
    public void addAppointment(CalendarEntryData calendarEntryData) {
        //TODO: validate

        try {
            database.addAppointment(
                    calendarEntryData.patientsId(),
                    calendarEntryData.doctorsId(),
                    calendarEntryData.starTime(),
                    calendarEntryData.endTime()
            );

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
