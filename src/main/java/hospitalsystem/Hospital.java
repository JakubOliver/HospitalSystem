package hospitalsystem;

import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.Calendar;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.util.Exportable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public DataPacket<Patient> addPatient(PatientData patientData) {
        try {
            return new DataPacket<>(database.addPatient(patientData));
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns info about patient with provided id.
     *
     * @param id Id that identifies patient.
     * @return info about patient.
     */
    public DataPacket<Patient> getPatient(int id){
        try{
            return new DataPacket<>(database.getPatient(id));
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    public GeneralPacket updatePatient(Patient patient){
        try{
            database.updatePatient(patient);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket deletePatient(int id){
        try{
            database.deletePatient(id);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Returns list of information about every patient in the system.
     *
     * @return list of information about every patient in the system.
     */
    public DataPacket<List<String>> allPatients(){
        try{
            List<Patient> patients = database.getAllPatients();

            return new DataPacket<>(
                    patients.stream().map(Patient::toString).toList()
            );
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Adds new doctor into the system.
     *
     * @param doctorData Doctor data that describes the new doctor.
     */
    public DataPacket<Doctor> addDoctor(DoctorData doctorData) {
        //TODO: validate
        try {
            return new DataPacket<>(database.addDoctor(doctorData));
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    public DataPacket<Doctor> getDoctor(int id){
        try{
            return new DataPacket<>(database.getDoctor(id));
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
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

    public DataPacket<List<String>> allDoctors(){
        try{
            List<Doctor> doctors = database.getAllDoctors();

            return new DataPacket<>(
                    doctors.stream().map(Doctor::toString).toList()
            );
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Adds new appointment into the system.
     *
     * @param appointmentData Calendar Entry data that describes the new appointment.
     */
    public GeneralPacket addAppointment(AppointmentData appointmentData) {
        try {
            //Validate whether the patient and doctor exists and have correct type
            Patient patient = database.getPatient(appointmentData.patientsId());
            Doctor doctor = database.getDoctor(appointmentData.patientsId());

            //TODO: validate whether the doctor or patient have time for the appointment (or at least doctor)

            database.addAppointment(
                    patient,
                    doctor,
                    appointmentData.starTime(),
                    appointmentData.endTime()
            );

            return new GeneralPacket();
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    public DataPacket<Appointment> getAppointment(int id){
        try{
            Appointment appointment = database.getAppointment(id);

            return new DataPacket<>(appointment);
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    public GeneralPacket updateAppointment(Appointment appointment){
        try {
            database.updateAppointment(appointment);
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }

        return new GeneralPacket();
    }

    public  GeneralPacket deleteAppointment(int id){
        try{
            database.deleteAppointment(id);
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }

        return new GeneralPacket();
    }

    //TODO: edit and delete appointment
    //TODO: appointment where patientId or doctorId (maybe like some general search, where the user provide data and get all appointments satisfying this query)

    public DataPacket<String> showCalendar(){
        try {
            //TODO: mozna vracet Calendar a vypsat to aby se nemuselo vytvaret obrovsky string
            Calendar calendar = database.getCalendar();

            return new DataPacket<>(calendar.toString());
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    private File createExportDirectory() throws IOException {
        File directory = new File("exports");

        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException("Unable to create directory for exports");
        }

        return directory;
    }

    private void writeExport(File destination, List<? extends Exportable> data) throws IOException{
        try (FileWriter writer = new FileWriter(destination)){
            for (Exportable entry : data) {
                writer.write(entry.export() + "\n");
            }
        }
    }

    public GeneralPacket exportPatients(){
        try {
            File destination = new File(createExportDirectory(), "patients.csv");

            writeExport(destination, database.getAllPatients());

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket exportDoctors(){
        try {
            File destination = new File(createExportDirectory(), "doctors.csv");

            writeExport(destination, database.getAllDoctors());

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket exportAppointments(){
        try{
            File destination = new File(createExportDirectory(), "appointments.csv");
            Calendar calendar = database.getCalendar();

            calendar.export(destination);

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    public GeneralPacket export(){
        try {
            File directory = createExportDirectory();

            writeExport(
                    new File(directory, "patients.csv"),
                    database.getAllPatients()
            );

            writeExport(
                    new File(directory, "doctors.csv"),
                    database.getAllDoctors()
            );

            Calendar calendar = database.getCalendar();
            calendar.export(new File(directory, "appointments.csv"));
        } catch (DatabaseException | IOException e){
            return new GeneralPacket(e);
        }

        return new  GeneralPacket();
    }
}
