package hospitalsystem;

import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.Calendar;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.PersonPacket;
import hospitalsystem.packet.TextPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public GeneralPacket getPatientInfo(int id){
        try{
            return new PersonPacket(database.getPatient(id));
        } catch (DatabaseException e){
            return new GeneralPacket(false, e.getMessage());
            //return "Error: " + e.getMessage();
        }
    }

    public GeneralPacket updatePatientInfo(Patient patient){
        try{
            database.updatePatient(patient);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(false, e.getMessage());
        }
    }

    public GeneralPacket deletePatient(int id){
        try{
            database.deletePatient(id);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(false, e.getMessage()); //TODO: udelat Packet konstruktor ne z stringu ale z DatabaseException (nebo exception)
        }
    }

    /**
     * Returns list of information about every patient in the system.
     *
     * @return list of information about every patient in the system.
     */
    public GeneralPacket findAllPatient(){
        try{
            List<Patient> patients = database.getAllPatients();

            return new TextPacket(
                    String.join("\n", patients.stream().map(Patient::toString).toList())
            );
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    /**
     * Adds new doctor into the system.
     *
     * @param doctorData Doctor data that describes the new doctor.
     */
    public GeneralPacket addDoctor(DoctorData doctorData) {
        //TODO: validate
        try {
            return new PersonPacket(database.addDoctor(doctorData));
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket getDoctor(int id){
        try{
            return new PersonPacket(database.getDoctor(id));
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket updateDoctor(Doctor doctor){
        try{
            database.updateDoctor(doctor);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket deleteDoctor(int id){
        try{
            database.deleteDoctor(id);

            return new GeneralPacket();
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket findAllDoctors(){
        try{
            List<Doctor> doctors = database.allDoctors();

            return new TextPacket(String.join("\n", doctors.stream().map(Doctor::toString).toList()));
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    /**
     * Adds new appointment into the system.
     *
     * @param appointmentData Calendar Entry data that describes the new appointment.
     */
    public GeneralPacket addAppointment(AppointmentData appointmentData) {
        //TODO: validate if patient is patient and doctor is doctor

        try {
            database.addAppointment(
                    appointmentData.patientsId(),
                    appointmentData.doctorsId(),
                    appointmentData.starTime(),
                    appointmentData.endTime()
            );

            return new GeneralPacket();
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    //TODO: edit and delete appointment

    public GeneralPacket showCalendar(){
        try {
            Calendar calendar = database.getCalendar();

            return new TextPacket(calendar.toString());
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    private File createExportDirectory() throws IOException {
        File directory = new File("exports");

        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException("Unable to create directory for exports");
        }

        return directory;
    }

    public GeneralPacket export(){
        try {
            List<Patient> patients = database.getAllPatients();
            List<Doctor> doctors = database.allDoctors();
            Calendar calendar = database.getCalendar();

            File directory;

            try {
                directory = createExportDirectory();
            } catch (IOException e){
                return new GeneralPacket(e);
            }

            try (FileWriter writer = new FileWriter(new File(directory, "patients.csv"))) {
                for (Patient patient : patients) {
                    writer.write(patient.export() + "\n");
                }
            } catch (IOException e) {
                return new GeneralPacket(e);
            }

            try (FileWriter writer = new FileWriter(new File(directory, "doctors.csv"))){
                for (Doctor doctor : doctors) {
                    writer.write(doctor.export() + "\n");
                }
            } catch (IOException e){
                return new GeneralPacket(e);
            }

            try {
                calendar.export(new File(directory, "appointments.csv"));
            } catch (IOException e) {
                return new GeneralPacket(e);
            }
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }

        return new  GeneralPacket();
    }
}
