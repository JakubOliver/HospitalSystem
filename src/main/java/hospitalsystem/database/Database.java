package hospitalsystem.database;

import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.*;
import hospitalsystem.calendar.*;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for communication with SQLite database from Hospital system request.
 */
public class Database {
    private static final String insertPerson = "INSERT INTO people(firstname, lastname, birth_date, type) VALUES (?, ?, ?, ?)";
    private static final String insertPatientDetail = "INSERT INTO patients_details(id, anamnesis) VALUES (?, ?)";
    private static final String insertDoctorDetail = "INSERT INTO doctors_details(id, specialization, department) VALUES (?, ?, ?)";
    private static final String insertAppointment = "INSERT INTO appointments(patient_id, doctor_id, department, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

    private static final String updatePerson = "UPDATE people SET firstname = ?, lastname = ?, birth_date = ? WHERE id = ?";
    private static final String updatePatientDetails = "UPDATE patients_details SET anamnesis = ? WHERE id = ?";
    private static final String updateDoctorDetails = "UPDATE doctors_details SET specialization = ?, department = ? WHERE id = ?";
    private static final String updateAppointment = "UPDATE appointments SET patient_id = ?, doctor_id = ?, department = ?, start_time = ?, end_time = ? WHERE id = ?";

    private static final String deletePersonById = "DELETE FROM people WHERE id = ?";
    private static final String deletePatientDetailsById = "DELETE FROM patients_details WHERE id = ?";
    private static final String deleteDoctorsDetailsById = "DELETE FROM doctors_details WHERE id = ?";
    private static final String deleteAppointmentsById = "DELETE FROM appointments WHERE id = ?";

    private static final String getPersonById = "SELECT * FROM people WHERE id = ?";
    private static final String getAllPeopleByType = "SELECT * FROM people, patients_details, doctors_details WHERE people.type = ?";
    private static final String getPatientDetailsById = "SELECT * FROM patients_details WHERE id = ?";
    private static final String getAllPatients = "SELECT * FROM people, patients_details WHERE people.id = patients_details.id";
    private static final String getDoctorDetailsById = "SELECT * FROM doctors_details WHERE id = ?";
    private static final String getAllDoctors = "SELECT * FROM people, doctors_details WHERE people.id = doctors_details.id";
    private static final String getAppointmentById = "SELECT * FROM appointments WHERE id = ?";
    private static final String getAppointmentBySomeId = "SELECT * FROM appointments where {0} = ?";
    private static final String getAllAppointments = "SELECT * FROM appointments";

    private static final String deleteDoctorsDetails = "DELETE FROM doctors_details";
    private static final String deletePatientsDetails ="DELETE FROM patients_details";
    private static final String deleteAppointments = "DELETE FROM appointments";
    private static final String deletePeople = "DELETE FROM people";

    private static final String getLastUsedIdError = "Unable to get generated key!";
    private static final String notExistingIdentifierError = "Id does not exist!";
    private static final String notExistingPatientIdentifierError = "Patient with this id does not exist!";
    private static final String notExistingDoctorIdentifierError = "Doctor with this id does not exist!";
    private static final String notExistingAppointmentIdentifierError = "Appointment with this id does not exist!";

    /** Path to the database */
    private final String url;

    /**
     * Connects to the databased located on provided url and check whether the database has correct structure.
     * If not then creates missing tables.
     *
     * @param url Location of the database.
     */
    public Database(String url){
        this.url = url;

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt =  conn.createStatement();

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS people (
                        id INTEGER PRIMARY KEY,
                        firstname TEXT NOT NULL,
                        lastname TEXT NOT NULL,
                        birth_date TEXT NOT NULL CHECK ( birth_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]' and date(birth_date) IS NOT NULL),
                        type TEXT NOT NULL CHECK (type IN ('patient', 'doctor'))
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS patients_details (
                        id INTEGER PRIMARY KEY,
                        anamnesis TEXT NOT NULL,
                    
                        FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS doctors_details (
                        id INTEGER PRIMARY KEY,
                        specialization TEXT NOT NULL,
                        department TEXT NOT NULL,
                    
                        FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS appointments (
                    	id INTEGER PRIMARY KEY,
                    	patient_id INTEGER NOT NULL,
                    	doctor_id INTEGER NOT NULL,
                    	department TEXT NOT NULL,
                    
                    	start_time TEXT NOT NULL
                    		CHECK (
                    			start_time GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]'
                    			and datetime(start_time) IS NOT NULL
                    			),
                    	end_time TEXT NOT NULL
                    		CHECK (
                    			end_time GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]'
                    			and datetime(end_time) IS NOT NULL
                    			),
                    
                    	FOREIGN KEY (patient_id) REFERENCES people(id),
                    	FOREIGN KEY (doctor_id) REFERENCES people(id)
                    );
                    """);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns id that was given to the entry in the provided statement.
     *
     * @param stmt Statement for which we want to know the id.
     * @return id that was given to the entry in the provided statement.
     * @throws SQLException Errors connected with the problem of retrieving id of the entry created by the statement.
     */
    private int getLastUsedId(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()){
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException(getLastUsedIdError);
        }
    }

    /**
     * Adds new person into database.
     *
     * @param connection Connection to the database.
     * @param person Person data that will be used for entry creation.
     * @param type Type of the person (patient/doctor)
     * @return Identifier of the person.
     * @throws SQLException Errors connected with the unability of storing new person.
     */
    private int addPerson(Connection connection, PersonData person, String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertPerson)){
            statement.setString(1, person.firstName());
            statement.setString(2, person.lastName());
            statement.setString(3, person.dateOfBirth().toString());
            statement.setString(4, type);

            statement.executeUpdate();

            try {
                return getLastUsedId(statement);
            } catch (SQLException e) {
                connection.rollback();

                throw e;
            }
        }
    }

    /**
     * Updates information about provided person.
     *
     * @param connection Connection to the database.
     * @param person Person data that will be updated.
     * @throws SQLException Error connected to the failure of updating data or providing invalid person information.
     */
    private void updatePerson(Connection connection, Person person) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(updatePerson)){
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.setString(3, person.getDateOfBirth().toString());
            statement.setInt(4, person.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Deletes person from the database.
     *
     * @param connection Connection to the database.
     * @param id Identification number of the person.
     * @throws SQLException Errors connected to the failure of deleting person from database.
     */
    private void deletePerson(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deletePersonById)){
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }

    /**
     * Returns Person with the provided id.
     *
     * @param connection Connection to the database.
     * @param id Id of the person.
     * @param expectedType Kind of the person which will help filter out invalid combinations.
     * @return Person with the provided id.
     * @throws SQLException Errors connected with the unability of retrieve person with provided id.
     */
    private Person getPerson(Connection connection, int id, String expectedType) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(getPersonById)){
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                if (!expectedType.equals(result.getString("type"))) {
                    throw new SQLException(MessageFormat.format(DatabaseException.invalidTypeOfPersonDatabaseError, id, expectedType));
                }

                return new Person(
                        result.getInt("id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        LocalDate.parse(result.getString("birth_date"))
                );
            }

            throw new SQLException(notExistingIdentifierError);
        }
    }

    /**
     * Returns list of people with provided first name and last name.
     *
     * @param fistName First name of the person.
     * @param lastName Last name of the person.
     * @param expectedType Kind of the person which will help filter out invalid combinations.
     * @return List of people with provided first name and last name.
     * @throws DatabaseException Error connected with the failure of retrieving people from database.
     */
    public List<Person> getPerson(String fistName, String lastName, String expectedType) throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement("SELECT * FROM people WHERE firstname = ? AND lastname = ? and type = ?;")){
            statement.setString(1, fistName);
            statement.setString(2, lastName);
            statement.setString(3, expectedType);

            ResultSet result = statement.executeQuery();

            List<Person> persons = new ArrayList<>();
            while (result.next()) {
                persons.add(new Person(
                        result.getInt("id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        LocalDate.parse(result.getString("birth_date"))
                ));
            }

            return persons;
        } catch (SQLException e){
            throw new DatabaseException(DatabaseException.personGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Deletes appointments which are connected to the person with provided id.
     *
     * @param connection Connection to the database.
     * @param id Identification number of person.
     * @param kind Denotes what kind of person it is.
     * @throws DatabaseException Errors connected to the failure of deleting appointments or that the id or combination id and kind if invalid.
     */
    private void deleteAppointmentsWherePerson(Connection connection, int id, PersonKinds kind) throws DatabaseException {
        try(Statement statement = connection.createStatement()){
            switch (kind){
                case Patient -> statement.executeUpdate("DELETE FROM appointments WHERE patient_id = " + id);
                case Doctor -> statement.executeUpdate("DELETE FROM appointments WHERE doctor_id = " + id);
            }
        }catch (SQLException e){
            throw new DatabaseException(e.getMessage()); //todo:
        }
    }

    /**
     * Adds patients details into database.
     *
     * @param connection Connection to the database.
     * @param id Id of the patient in the person table.
     * @param details Details of the patient.
     * @throws SQLException Errors connected with the unability of storing patients details.
     */
    private void addPatientDetails(Connection connection, int id, PatientsDetails details) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertPatientDetail)){
            statement.setInt(1, id);
            statement.setString(2, details.anamnesis());

            statement.executeUpdate();
        }
    }

    /**
     * Adds patients into the database.
     *
     * @param patientData Patients data which will be used for entry creation.
     * @return Patient object matching with the patient that was stored in database.
     * @throws DatabaseException Errors connected with the unability of storing new patient.
     */
    public Patient addPatient(PatientData patientData) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, patientData.person(), Patient.getClassIdentifier());
            addPatientDetails(connection, id, patientData.details());

            connection.commit();

            return new Patient(
                    id,
                    patientData.person().firstName(),
                    patientData.person().lastName(),
                    patientData.person().dateOfBirth(),
                    patientData.details().anamnesis()
            );
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.patientInsertDatabaseError);
        }
    }

    /**
     * Updates patient details inside the database.
     *
     * @param connection Connection to the database.
     * @param patient Patient data/details that will be updated in the database.
     * @throws SQLException Error connected to the failure of updating patients details or providing invalid patient data.
     */
    private void updatePatientDetails(Connection connection, Patient patient) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(updatePatientDetails)){
            statement.setString(1, patient.getAnamnesis());
            statement.setInt(2, patient.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Updates patient data inside the database.
     *
     * @param patient Patient that will be updated.
     * @throws DatabaseException Errors connected with the inability of update patient's data.
     */
    public void updatePatient(Patient patient) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            connection.setAutoCommit(false);

            getPerson(connection, patient.getId(), Patient.getClassIdentifier()); //Check whether the person exists and if is patient
            updatePerson(connection, patient);
            updatePatientDetails(connection, patient);

            connection.commit();
        }catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.patientUpdateDatabaseError, e.getMessage());
        }
    }

    /**
     * Deletes patient details from the database.
     *
     * @param connection Connection to the database.
     * @param id Identification number of the person connected to the details.
     * @throws SQLException Errors connected to the failure of deleting details or providing invalid id.
     */
    private void deletePatientsDetails(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(deletePatientDetailsById)){
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }

    /**
     * Deletes patient from the database.
     *
     * @param id Identification number of patient.
     * @throws DatabaseException Error connected with the inability of deleting patient from database. Such as ID does not exist or connection errors.
     */
    public void deletePatient(int id) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            connection.setAutoCommit(false);

            deletePatientsDetails(connection, id);
            deletePerson(connection, id);
            deleteAppointmentsWherePerson(connection, id, PersonKinds.Patient);

            connection.commit();
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.patientDeleteDatabaseError, e.getMessage());
        }
    }

    /**
     * Returns patients details for patient with provided id.
     *
     * @param connection Connection to the database.
     * @param id Id of the patient.
     * @return patients details for patient with provided id.
     * @throws SQLException Errors connected with the unability to retrieve patients details.
     */
    private PatientsDetails getPatientDetails(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(getPatientDetailsById)){
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new PatientsDetails(result.getString("anamnesis"));
            }

            throw new SQLException(notExistingPatientIdentifierError);
        }
    }


    /**
     * Returns patient based on provided id.
     *
     * @param id Id of the patient we want to retrieve from database.
     * @return patient based on provided id.
     * @throws DatabaseException Error connected with the unability to successfully retrieve patients data from database.
     */
    public Patient getPatient(int id) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            Person person = getPerson(connection, id, Patient.getClassIdentifier());
            PatientsDetails details =  getPatientDetails(connection, id);

            return new Patient(person, details);
        } catch  (SQLException e) {
            //System.out.println(e.getMessage());
            throw new DatabaseException(DatabaseException.patientGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Find and returns all patients in the database.
     *
     * @param connection Connection to the database.
     * @return list of all patients in the database.
     * @throws SQLException Error connected to the unability to successfully retrieve patients data from database.
     */
    private List<Patient> findAllPatients(Connection connection) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement(getAllPatients)){
            ResultSet result = statement.executeQuery();

            ArrayList<Patient> patients = new ArrayList<>();

            while (result.next()) {
                patients.add(new Patient(
                        result.getInt("id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        LocalDate.parse(result.getString("birth_date")),
                        result.getString("anamnesis")
                ));
            }

            return patients;
        }
    }

    /**
     * Returns all patients in the database.
     *
     * @return list of all patients in the database.
     * @throws DatabaseException Error connected to the failure of retrieving all patients from the database.
     */
    public List<Patient> getAllPatients() throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            return findAllPatients(connection);
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.patientGetDatabaseError);
        }
    }

    /**
     * Adds new doctor details to the database.
     *
     * @param connection Connection to the database.
     * @param id Id of the doctor.
     * @param details Wrapper for all doctor details.
     * @throws SQLException Error connected to the unability to successfully store doctor details in database.
     */
    private void addDoctorDetails(Connection connection, int id, DoctorDetails details) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertDoctorDetail)){
            statement.setInt(1, id);
            statement.setString(2, details.specialization());
            statement.setString(3, details.department());

            statement.executeUpdate();
        }
    }

    /**
     * Adds doctor into the database.
     *
     * @param doctorData Doctor data that will be stored in the database.
     * @return Doctor matching with the doctor entry stored in database.
     * @throws DatabaseException Error connected to the failure of storing new doctor in database.
     */
    public Doctor addDoctor(DoctorData doctorData) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, doctorData.person(), Doctor.getClassIdentifier());
            addDoctorDetails(connection, id, doctorData.details());

            connection.commit();

            return new Doctor(
                    new Person(id, doctorData.person()),
                    doctorData.details()
            );
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.doctorInsertDatabaseError, e.getMessage());
        }
    }

    /**
     * Updates doctor's details inside the database.
     *
     * @param connection Connection to the database.
     * @param doctor Doctor data/details that will be updated.
     * @throws SQLException Errors connected to the failure of updating details or providing invalid doctor's data.
     */
    private void updateDoctorDetails(Connection connection, Doctor doctor) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(updateDoctorDetails)){
            statement.setString(1, doctor.getSpecialization());
            statement.setString(2, doctor.getDepartment());
            statement.setInt(3, doctor.getId());

            statement.executeUpdate();
        }
    }

    /**
     * Updates doctor information inside the database.
     *
     * @param doctor Doctor that will be updated.
     * @throws DatabaseException Errors connected with the failure of updating doctor's data. Such as doctor was not present inside the database or connection errors.
     */
    public void updateDoctor(Doctor doctor) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            connection.setAutoCommit(false);

            getPerson(connection, doctor.getId(), Doctor.getClassIdentifier());
            updatePerson(connection, doctor);
            updateDoctorDetails(connection, doctor);

            connection.commit();
        } catch (SQLException e){
            throw new DatabaseException(DatabaseException.doctorUpdateDatabaseError, e.getMessage());
        }
    }

    /**
     * Deletes doctor details from the database.
     *
     * @param connection Connection to the database.
     * @param id Identification number of the doctor connected to the details that will be deleted.
     * @throws SQLException Errors connected to the failure of deleting details or providing invalid id.
     */
    private void deleteDoctorsDetails(Connection connection, int id) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement(deleteDoctorsDetailsById)){
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }

    /**
     * Deletes doctor from the database.
     *
     * @param id Identification number of the doctor.
     * @throws DatabaseException Error connected with the failure of deleting doctor from database. Such as doctor was not present inside the database or connection errors.
     */
    public void deleteDoctor(int id) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            connection.setAutoCommit(false);

            deletePerson(connection, id);
            deleteDoctorsDetails(connection, id);
            deleteAppointmentsWherePerson(connection, id, PersonKinds.Doctor);

            connection.commit();
        } catch (SQLException e){
            throw new DatabaseException(DatabaseException.doctorDeleteDatabaseError, e.getMessage());
        }
    }

    /**
     * Returns list of all doctors in the database.
     *
     * @return List of all doctors in the database.
     * @throws DatabaseException Error connected with the failure of retrieving all doctors from the database - connection errors.
     */
    public List<Doctor> getAllDoctors() throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(getAllDoctors)){
            ResultSet result = statement.executeQuery();

            List<Doctor> doctors = new ArrayList<>();

            while (result.next()){
                doctors.add(new Doctor(
                        result.getInt("id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        LocalDate.parse(result.getString("birth_date")),
                        result.getString("specialization"),
                        result.getString("department")
                ));
            }

            return doctors;
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.doctorGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Returns doctors details for doctor with provided id.
     *
     * @param connection Connection to the database.
     * @param id Id of the doctor.
     * @return Doctors details for doctor with provided id.
     * @throws SQLException Errors connected with the unability to retrieve doctors details.
     */
    private DoctorDetails getDoctorDetails(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(getDoctorDetailsById)){
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new DoctorDetails(
                        result.getString("specialization"),
                        result.getString("department")
                );
            }

            throw new SQLException(notExistingDoctorIdentifierError);
        }
    }


    /**
     * Returns Doctor with corresponding id.
     *
     * @param id Id of the doctor.
     * @return Doctor with corresponding id.
     * @throws DatabaseException Error connected with failure of retrieving doctor from database.
     */
    public Doctor getDoctor(int id) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            Person person = getPerson(connection, id, Doctor.getClassIdentifier());
            DoctorDetails details = getDoctorDetails(connection, id);

            return new Doctor(person, details);
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.doctorGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Adds new appointment to the database.
     *
     * @param patientId Patients identifier.
     * @param doctorId Doctors identifier.
     * @param department Department where the doctor works and where will be the appointment.
     * @param startTime Starting time of appointment.
     * @param endTime Ending time of appointment.
     * @throws DatabaseException Error connected to the failure of storing new appointment.
     */
    public void addAppointment(int patientId, int doctorId, String department, LocalDateTime startTime, LocalDateTime endTime) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(insertAppointment)) {
            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setString(3, department);
            statement.setString(4, startTime.toString());
            statement.setString(5, endTime.toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DatabaseException(DatabaseException.appointmentInsertDatabaseError);
        }
    }

    /**
     * Adds new appointment to the database.
     *
     * @param patient Patient connected to the appointment.
     * @param doctor Doctor connected to the appointment.
     * @param startTime Starting time of appointment.
     * @param endTime Ending time of appointment.
     * @throws DatabaseException Error connected to the failure of storing new appointment.
     */
    public void addAppointment(Patient patient, Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) throws DatabaseException {
        addAppointment(patient.getId(), doctor.getId(), doctor.getDepartment(), startTime, endTime);
    }

    /**
     * Adds new appointment to the database.
     *
     * @param patientId Patient's identification number.
     * @param doctorId Doctor's identification number.
     * @param startTime Starting time of the appointment.
     * @param endTime Ending time of the appointment.
     * @throws DatabaseException Error connected to the failure of storing new appointment.
     */
    public void addAppointment(int patientId, int doctorId, LocalDateTime startTime, LocalDateTime endTime) throws DatabaseException {
        Doctor doctor = getDoctor(doctorId);

        addAppointment(patientId, doctorId, doctor.getDepartment(), startTime, endTime);
    }

    /**
     * Returns the appointment based on provided id.
     *
     * @param id Identification number of the appointment.
     * @return Appointment based on provided id.
     * @throws DatabaseException Error connected to the failure of retrieving appointment from the database. No such appointment with id is present in the database or connection errors.
     */
    public Appointment getAppointment(int id) throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(getAppointmentById)){
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (!result.next())
                throw new DatabaseException(notExistingAppointmentIdentifierError);

            return new Appointment(result);
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.appointmentGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Deletes appointment from the database based on provided id.
     *
     * @param id Identification number of the appointment.
     * @throws DatabaseException Error connected to the failure of deleting appointment from the database. No such appointment with id is present in the database or connection errors.
     */
    public void deleteAppointment(int id) throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(deleteAppointmentsById)){
            statement.setInt(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.appointmentDeleteDatabaseError, e.getMessage());
        }
    }

    /**
     * Updates information about existing appointment.
     *
     * @param appointment Appointment data that will be used for updating.
     * @throws DatabaseException Failure of updating data inside database. Invalid identifiers or connection errors.
     */
    public void updateAppointment(Appointment appointment) throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(updateAppointment)){
            statement.setInt(1, appointment.patientId);
            statement.setInt(2, appointment.doctorId);
            statement.setString(3, appointment.department);
            statement.setString(4, appointment.startTime.toString());
            statement.setString(5, appointment.endTime.toString());
            statement.setInt(6, appointment.id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.appointmentUpdateDatabaseError, e.getMessage());
        }
    }

    /**
     * Returns list of appointments connected to the person with the provided id.
     *
     * @param id Identification number of the person.
     * @param column Identification text of the column which will be checked of the id match.
     * @return List of appointments connected to the person with the provided id.
     * @throws DatabaseException Error connected with failure of retrieving appointments from the database.
     */
    private List<Appointment> getAppointmentsForPersonnel(int id, String column) throws DatabaseException {
        try(Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(MessageFormat.format(getAppointmentBySomeId, column))) {
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();
            List<Appointment> appointments = new ArrayList<>();

            while (result.next()) {
                appointments.add(new Appointment(result));
            }

            return appointments;
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.appointmentGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Returns list of appointments for patient with provided id.
     *
     * @param id Identification number of the patient.
     * @return List of appointments for patient.
     * @throws DatabaseException Error connected with the connection to the database.
     */
    public List<Appointment> getAppointmentsForPatient(int id) throws DatabaseException {
        return getAppointmentsForPersonnel(id, "patient_id");
    }

    /**
     * Returns list of appointments for doctor with provided id.
     *
     * @param id Identification number of the doctor.
     * @return List of appointments for doctor with provided id.
     * @throws DatabaseException Error connected with the connection to the database.
     */
    public List<Appointment> getAppointmentsForDoctor(int id) throws DatabaseException {
        return getAppointmentsForPersonnel(id, "doctor_id");
    }

    /**
     * Returns calendar object representing all appointments in the database.
     *
     * @return Calendar object representing all appointments.
     * @throws DatabaseException Error connected to the failure of retrieving data from database or connection errors.
     */
    public Calendar getCalendar() throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url); PreparedStatement statement = connection.prepareStatement(getAllAppointments)){
            ResultSet result = statement.executeQuery();

            Calendar calendar = new Calendar();
            calendar.importData(result);

            return calendar;
        } catch (SQLException e){
            throw new DatabaseException(DatabaseException.appointmentGetDatabaseError, e.getMessage());
        }
    }

    /**
     * Deletes all data from database.
     * <p>
     * Created for the purpose of unit tests to ensure same starting point for all tests.
     *
     * @throws DatabaseException Error connected to the failure of deletion of data from database.
     */
    public void deleteAllData() throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url); Statement statement = connection.createStatement()){
            statement.executeUpdate(deleteDoctorsDetails);
            statement.executeUpdate(deletePatientsDetails);
            statement.executeUpdate(deleteAppointments);
            statement.executeUpdate(deletePeople);
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseException.generalDeleteDatabaseError, e.getMessage());
        }
    }
}
