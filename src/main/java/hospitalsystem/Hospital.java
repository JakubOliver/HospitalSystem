package hospitalsystem;

import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.Calendar;
import hospitalsystem.calendar.CalendarException;
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
import hospitalsystem.util.ExportsUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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
     * @return Data packet providing caller with information about successfulness of the query. And also contains object of the patient with provided ID.
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
     * @return Data packet providing caller with information about successfulness of the query. And also contains object of the patient with provided ID.
     */
    public DataPacket<Patient> getPatient(int id){
        try{
            return new DataPacket<>(database.getPatient(id));
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Updates information about patient with provided id and based on provided new data.
     *
     * @param patient State to which will be patient changed.
     * @return General packet which provide caller with information about successfulness of the update.
     */
    public GeneralPacket updatePatient(Patient patient){
        try{
            database.updatePatient(patient);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Deletes patient with provided id.
     *
     * @param id Identifier of the patient that will be removed from the system.
     * @return General packet which provide caller with information about successfulness of delete.
     */
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
     * @return Data packet that provide caller with information about successfulness of the query and provides array of data strings about every patient.
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
     * @return Data packet that provide caller with information about successfulness of the addition of doctor into the system. And also provides doctor object representing the same doctor.
     */
    public DataPacket<Doctor> addDoctor(DoctorData doctorData) {
        //TODO: validate
        try {
            return new DataPacket<>(database.addDoctor(doctorData));
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns object representing doctor with provided id.
     *
     * @param id Identification number of the required doctor.
     * @return Data packet that provide caller with information about successfulness of the query of doctor. And also provides doctor object.
     */
    public DataPacket<Doctor> getDoctor(int id){
        try{
            return new DataPacket<>(database.getDoctor(id));
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Updates information about doctor with provided id and based on provided new data.
     *
     * @param doctor State to which will be doctor changed.
     * @return General packet which provide caller with information about successfulness of the update.
     */
    public GeneralPacket updateDoctor(Doctor doctor){
        try{
            database.updateDoctor(doctor);

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Deletes doctor with provided id.
     *
     * @param id Identifier of the doctor that will be removed from the system.
     * @return General packet which provide caller with information about successfulness of delete.
     */
    public GeneralPacket deleteDoctor(int id){
        try{
            database.deleteDoctor(id);

            return new GeneralPacket();
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }
    }

    /**
     * Returns list of information about every doctor in the system.
     *
     * @return Data packet that provide caller with information about successfulness of the query and provides array of data strings about every doctor.
     */
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
     * Decides whether the time interval [start, end] is in the conflict (have overlap) with some appointment.
     *
     * @param appointments List of appointments that we want to check.
     * @param start Start of the time interval.
     * @param end End of the time interval.
     * @return Whether the time interval is not in the conflict with the appointments.
     */
    private boolean haveTime(List<Appointment> appointments, LocalDateTime start, LocalDateTime end){
        for (Appointment appointment : appointments){
            if (appointment.inConflict(start, end)) return false;
        }

        return true;
    }

    /**
     * Checks whether the data from which will be new appointment created satisfy criteria.
     * <p>
     * Such as: valid doctor and patient id, time interval does not collide with doctor or patient other appointments, time interval is in the correct form (starts and ends at full or half hour and is long at least 1 hour)
     *
     * @param appointmentData Appointment data which we want to validate whether satisfy criteria.
     * @throws CalendarException Time interval is in the conflict or is not in correct form.
     * @throws DatabaseException Error occurs while working with the database (mostly invalid id or general connection fault).
     */
    private void validateAppointmentData(AppointmentData appointmentData) throws CalendarException, DatabaseException {
        //Validate whether the patient and doctor exists and have correct type
        database.getPatient(appointmentData.patientsId());
        database.getDoctor(appointmentData.doctorsId());

        Calendar.timeIsValid(appointmentData.starTime(), appointmentData.endTime());

        if (!haveTime(
                database.getAppointmentsForPatient(appointmentData.patientsId()),
                appointmentData.starTime(),
                appointmentData.endTime()
        )) throw new CalendarException(CalendarException.timeCollisionWithPatient);

        if (!haveTime(
                database.getAppointmentsForDoctor(appointmentData.doctorsId()),
                appointmentData.starTime(),
                appointmentData.endTime()
        )) throw new CalendarException(CalendarException.timeCollisionWIthDoctor);
    }

    /**
     * Adds new appointment into the system.
     *
     * @param appointmentData Calendar Entry data that describes the new appointment.
     * @return General packet that provide caller with the information whether the addition of the appointment into the system was successful.
     */
    public GeneralPacket addAppointment(AppointmentData appointmentData) {
        try {
            validateAppointmentData(appointmentData);

            database.addAppointment(
                    appointmentData.patientsId(),
                    appointmentData.doctorsId(),
                    appointmentData.starTime(),
                    appointmentData.endTime()
            );

            return new GeneralPacket();
        } catch (DatabaseException | CalendarException e){
            return new GeneralPacket(e);
        }
    }

    /**
     * Returns appointment with the provided id.
     *
     * @param id Identifier of the appointment.
     * @return General packet which provides caller with the information about successfulness of the query. And provides the object of the appointment.
     */
    public DataPacket<Appointment> getAppointment(int id){
        try{
            Appointment appointment = database.getAppointment(id);

            return new DataPacket<>(appointment);
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Updates information about appointment with provided id and based on provided new data.
     *
     * @param appointment State to which will be appointment changed.
     * @return General packet which provide caller with information about successfulness of the update.
     */
    public GeneralPacket updateAppointment(Appointment appointment){
        try {
            validateAppointmentData(new AppointmentData(
                    appointment.patientId,
                    appointment.doctorId,
                    appointment.startTime,
                    appointment.endTime
            ));

            database.updateAppointment(appointment);
        } catch (DatabaseException | CalendarException e) {
            return new GeneralPacket(e);
        }

        return new GeneralPacket();
    }

    /**
     * Deletes appointment with provided id.
     *
     * @param id Identifier of the appointment that will be removed from the system.
     * @return General packet which provide caller with information about successfulness of delete.
     */
    public GeneralPacket deleteAppointment(int id){
        try{
            database.deleteAppointment(id);
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }

        return new GeneralPacket();
    }

    //TODO: edit and delete appointment
    //TODO: appointment where patientId or doctorId (maybe like some general search, where the user provide data and get all appointments satisfying this query)

    /**
     * Returns string representing the calendar of the whole hospital.
     *
     * @return string representing the calendar of the whole hospital.
     */
    public DataPacket<String> showCalendar(){
        try {
            //TODO: mozna vracet Calendar a vypsat to aby se nemuselo vytvaret obrovsky string
            Calendar calendar = database.getCalendar();

            return new DataPacket<>(calendar.toString());
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Prepares folder where hospital data could be exported.
     *
     * @return File object representing the prepared directory.
     * @throws IOException Error occurs while creating or checking directory.
     */
    private File createExportDirectory() throws IOException {
        File directory = new File(ExportsUtil.exportDirectoryDestination);

        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException(ExportsUtil.unableToPrepareExportsDirectoryErrMsg);
        }

        return directory;
    }

    /**
     * Exports array of object into destination file.
     * <p>
     * The format of the export is based on how the object implemented required export method.
     *
     * @param destination File which will be used for exporting.
     * @param data List of objects that will be exported.
     * @throws IOException Error that occurs while opening/writing into the file.
     */
    private void writeExport(File destination, List<? extends Exportable> data) throws IOException{
        try (FileWriter writer = new FileWriter(destination)){
            for (Exportable entry : data) {
                writer.write(entry.export() + "\n");
            }
        }
    }

    /**
     * Exports patients into file in CSV format
     *
     * @return General packet providing caller with the information about successfulness of the export.
     */
    public GeneralPacket exportPatients(){
        try {
            File destination = new File(createExportDirectory(), ExportsUtil.patientExportDestination);

            writeExport(destination, database.getAllPatients());

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Exports doctors into file in CSV format.
     *
     * @return General packet providing caller with the information about successfulness of the export.
     */
    public GeneralPacket exportDoctors(){
        try {
            File destination = new File(createExportDirectory(), ExportsUtil.doctorExportDestination);

            writeExport(destination, database.getAllDoctors());

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Exports appointments into file in CSV format.
     *
     * @return General packet providing caller with the information about successfulness of  the export.
     */
    public GeneralPacket exportAppointments(){
        try{
            File destination = new File(createExportDirectory(), ExportsUtil.appointmentExportDestination);
            Calendar calendar = database.getCalendar();

            calendar.export(destination);

            return new GeneralPacket();
        } catch (DatabaseException | IOException e) {
            return new GeneralPacket(e);
        }
    }

    /**
     * Exports all hospital system into CSV files (patients, doctors, appointments - one file for each)
     *
     * @return General packet providing caller with the information about successfulness of the export.
     */
    public GeneralPacket export(){
        try {
            File directory = createExportDirectory();

            writeExport(
                    new File(directory, ExportsUtil.patientExportDestination),
                    database.getAllPatients()
            );

            writeExport(
                    new File(directory, ExportsUtil.doctorExportDestination),
                    database.getAllDoctors()
            );

            Calendar calendar = database.getCalendar();
            calendar.export(new File(directory, ExportsUtil.appointmentExportDestination));
        } catch (DatabaseException | IOException e){
            return new GeneralPacket(e);
        }

        return new  GeneralPacket();
    }

    /**
     * Deletes all data from hospital system.
     * <p>
     * For the purpose of the unit testing to ensure same database for tests.
     *
     * @return General packet providing caller with the information about successfulness of the cleanup.
     */
    public GeneralPacket delete(){
        try {
            database.deleteAllData();

            return new GeneralPacket();
        } catch (DatabaseException e) {
            return new GeneralPacket(e);
        }
    }
}
