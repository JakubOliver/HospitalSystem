package cz.cuni.kubinja.hospitalsystem.core;

import cz.cuni.kubinja.hospitalsystem.core.calendar.Appointment;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.core.calendar.CalendarException;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.database.Database;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonKinds;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics;
import cz.cuni.kubinja.hospitalsystem.core.export.Exportable;
import cz.cuni.kubinja.hospitalsystem.core.export.ExportsUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hospital system for managing patients, doctors and appointments.
 */
public class Hospital {
    /** Database for storing data connected to the hospital system */
    private final Database database;

    /**
     * Creates Hospital which content is based on the data in provided database.
     *
     * @param databasePath Path to the database.
     * @throws DatabaseException Errors connected to the unability to open/create database.
     */
    public Hospital(String databasePath) throws DatabaseException {
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
     * Returns list of patients with the provided first name and last name.
     *
     * @param firstName First name of the patient.
     * @param lastName Last name of the patient.
     * @return List of patients with the provided first and last name.
     */
    public DataPacket<List<Patient>> getAllPatientWithName(String firstName, String lastName){
        try {
            List<Person> people = database.getPerson(firstName, lastName, Patient.getClassIdentifier());

            List<Patient> patients = new ArrayList<>();

            for (Person person : people) {
                patients.add(database.getPatient(person.getId()));
            }

            return new DataPacket<>(patients);
        } catch (DatabaseException e) {
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
            database.getPatient(id); //check whether the patient with id exists
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
    public DataPacket<List<Patient>> allPatients(){
        try{
            return new DataPacket<>(database.getAllPatients());
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
     * Returns list of doctors with provided first name and last name.
     *
     * @param firstName First name of the doctor.
     * @param lastName Last name of the doctor.
     * @return List of doctors with provided first name and last name.
     */
    public DataPacket<List<Doctor>> getAllDoctorsWithName(String firstName, String lastName){
        try {
            List<Person> people = database.getPerson(firstName, lastName, Doctor.getClassIdentifier());

            List<Doctor> doctors = new ArrayList<>();

            for (Person person : people) {
                doctors.add(database.getDoctor(person.getId()));
            }

            return new DataPacket<>(doctors);
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
            database.getDoctor(id); //Check whether exists doctor with provided id.
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
    public DataPacket<List<Doctor>> allDoctors(){
        try{
            return new DataPacket<>(database.getAllDoctors());
        } catch (DatabaseException e){
            return new DataPacket<>(e);
        }
    }

    /**
     * Adds new appointment into the system.
     *
     * @param appointmentData Calendar Entry data that describes the new appointment.
     * @return General packet that provide caller with the information whether the addition of the appointment into the system was successful.
     */
    public GeneralPacket addAppointment(AppointmentData appointmentData) {
        try {
            if (!Calendar.timeIsValid(appointmentData.starTime(), appointmentData.endTime()))
                throw new CalendarException(CalendarException.invalidTimes);

            database.checkAndAddAppointment(appointmentData);

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
            if (!Calendar.timeIsValid(appointment.startTime, appointment.endTime))
                throw new CalendarException(CalendarException.invalidTimes);

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
            database.getAppointment(id); //Tests whether the appointment even exists
            database.deleteAppointment(id);
        } catch (DatabaseException e){
            return new GeneralPacket(e);
        }

        return new GeneralPacket();
    }

    /**
     * Returns list of all appointments connected to the person with provided id and kind.
     *
     * @param id Identification number of the person.
     * @param kind Kind of person.
     * @return List of all appointment connected to the person with provided id and kind.
     */
    public DataPacket<List<String>> getAppointmentsForPersonnel(int id, PersonKinds kind){
        try{
            List<Appointment> appointments = switch (kind) {
                case Patient -> {
                    database.getPatient(id);
                    yield database.getAppointmentsForPatient(id);
                }
                case Doctor -> {
                    database.getDoctor(id);
                    yield database.getAppointmentsForDoctor(id);
                }
            };

            return new DataPacket<>(appointments.stream().map(Appointment::toString).toList());
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns typed summaries of all appointments.
     *
     * @return Appointment summaries ordered by time and identifier.
     */
    public DataPacket<List<AppointmentSummary>> getAppointmentSummaries() {
        try {
            return new DataPacket<>(database.getAppointmentSummaries());
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns a typed summary of one appointment.
     *
     * @param id Appointment identifier.
     * @return Appointment summary.
     */
    public DataPacket<AppointmentSummary> getAppointmentSummary(int id) {
        try {
            return new DataPacket<>(database.getAppointmentSummary(id));
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns typed appointment summaries associated with one person.
     *
     * @param id Identification number of the person.
     * @param kind Kind of personnel.
     * @return Appointment summaries associated with the person.
     */
    public DataPacket<List<AppointmentSummary>> getAppointmentSummariesForPersonnel(
            int id,
            PersonKinds kind
    ) {
        try {
            switch (kind) {
                case Patient -> database.getPatient(id);
                case Doctor -> database.getDoctor(id);
            }

            return new DataPacket<>(
                    database.getAppointmentSummariesForPersonnel(id, kind)
            );
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns string representing calendar for the provided department.
     *
     * @param department Name of department.
     * @param fromToday Denotes whether calendar will be exported whole or only appointments in the present and future.
     * @return String representing calendar for the provided department.
     */
    public DataPacket<String> getCalendarForDepartment(String department, boolean fromToday){
        try {
            Calendar calendar = database.getCalendar();

            return new DataPacket<>(calendar.getDepartmentCalendar(department, fromToday));
        } catch (DatabaseException | CalendarException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns list of strings representing the calendar of the whole hospital.
     *
     * @param fromToday Denotes whether calendar will be exported whole or only appointments in the present and future.
     * @return list of strings representing the calendar of the whole hospital.
     */
    public DataPacket<List<String>> getCalendarRepresentation(boolean fromToday){
        try {
            Calendar calendar = database.getCalendar();

            return new DataPacket<>(calendar.getCalendar(fromToday));
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Returns calendar for the hospital.
     *
     * @return Calendar for the hospital.
     */
    public DataPacket<Calendar> getCalendar(){
        try{
            return new DataPacket<>(database.getCalendar());
        } catch (DatabaseException e) {
            return new DataPacket<>(e);
        }
    }

    /**
     * Calculates statistics about hospital personnel and appointments.
     *
     * @return Data packet containing an immutable statistics snapshot.
     */
    public DataPacket<HospitalStatistics> getStatistics() {
        try {
            return new DataPacket<>(HospitalStatisticsCalculator.calculate(
                    database.getAllPatients(),
                    database.getAllDoctors(),
                    database.getCalendar()
            ));
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
            File destination = new File(createExportDirectory(), ExportsUtil.getExportFileName(ExportsUtil.patientExportDestination));

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
            File destination = new File(createExportDirectory(), ExportsUtil.getExportFileName(ExportsUtil.doctorExportDestination));

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
            File destination = new File(createExportDirectory(), ExportsUtil.getExportFileName(ExportsUtil.appointmentExportDestination));
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
                    new File(directory, ExportsUtil.getExportFileName(ExportsUtil.patientExportDestination)),
                    database.getAllPatients()
            );

            writeExport(
                    new File(directory, ExportsUtil.getExportFileName(ExportsUtil.doctorExportDestination)),
                    database.getAllDoctors()
            );

            Calendar calendar = database.getCalendar();
            calendar.export(new File(directory, ExportsUtil.getExportFileName(ExportsUtil.appointmentExportDestination)));
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
