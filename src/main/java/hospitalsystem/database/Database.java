package hospitalsystem.database;

import hospitalsystem.calendar.CalendarEntry;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO: zvazit zda nenahradit cast metd tim ze dostanou Query a parametry pomoci ... a potom se sestroji

/**
 * Interface for communication with SQLite database from Hospital system request.
 */
public class Database {
    private static final String insertPerson = "INSERT INTO people(firstname, lastname, birth_date, type) VALUES (?, ?, ?, ?)";
    private static final String insertPatientDetail = "INSERT INTO patients_details(id, anamnesis) VALUES (?, ?)";
    private static final String insertDoctorDetail = "INSERT INTO doctors_details(id, specialization) VALUES (?, ?)";
    private static final String insertAppointment = "INSERT INTO appointments(patient_id, doctor_id, start_time, end_time) VALUES (?, ?, ?, ?)";
    private static final String insertPatient = "INSERT INTO patients(firstname, lastname, birth_date, anamnesis) VALUES (?, ?, ?, ?)";
    private static final String insertDoctor = "INSERT INTO patients(firstname, lastname, birth_date, specialization) VALUES (?, ?, ?, ?)";

    private static final String getPersonById = "SELECT * FROM people WHERE id = ?";
    private static final String getAllPeopleByType = "SELECT * FROM people, patients_details, doctors_details WHERE people.type = ?";
    private static final String getPatientDetailsById = "SELECT * FROM patients_details WHERE id = ?";
    private static final String getAllPatients = "SELECT * FROM people, patients_details WHERE people.id = patients_details.id";

    private static final String getLastUsedIdError = "Unable to get generated key!";
    private static final String notExistingIdentifierError = "Id does not exist!";

    private final String url;

    /**
     * Connects to the databased located on provided url and check whether the databases has correct structure.
     * If not then creates missing tables.
     *
     * @param url Location of the database.
     */
    public Database(String url){
        this.url = url;

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt =  conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS people (id INTEGER PRIMARY KEY, firstname TEXT NOT NULL, lastname TEXT NOT NULL, birth_date TEXT NOT NULL CHECK ( birth_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]' and date(birth_date) IS NOT NULL), type TEXT NOT NULL CHECK (type IN ('patient', 'doctor')));");

            stmt.execute("CREATE TABLE IF NOT EXISTS patients_details (id INTEGER PRIMARY KEY, anamnesis TEXT NOT NULL, FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE);");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS doctors_details (
                        id INTEGER PRIMARY KEY, 
                        specialization TEXT NOT NULL, 
                        
                        FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE
                    );
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS appointments (
                    	id INTEGER PRIMARY KEY,
                    	patient_id INTEGER NOT NULL,
                    	doctor_id INTEGER NOT NULL,
                    
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
     * @return Id that was given to the entry in the provided statement.
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

    /////////////////////////////////
    ///          PERSON          ///
    ////////////////////////////////

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
     * Returns Person with the provided id.
     *
     * @param connection Connection to the database.
     * @param id Id of the person.
     * @return Person with the provided id.
     * @throws SQLException Errors connected with the unability of retrieve person with provided id.
     */
    private Person getPerson(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(getPersonById)){
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new Person(
                        result.getInt("id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        LocalDate.parse(result.getString("birth_date")));
            }

            throw new SQLException(notExistingIdentifierError);
        }
    }

    /////////////////////////////////
    ///         Patient          ///
    ////////////////////////////////

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

            Patient patient = new Patient(
                    id,
                    patientData.person().firstName(),
                    patientData.person().lastName(),
                    patientData.person().dateOfBirth(),
                    patientData.details().anamnesis()
            );

            //SystemLogger.successfullNewPatient(patient);
            return patient;
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.patientInsertDatabaseError);
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

            throw new SQLException(notExistingIdentifierError);
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
            Person person = getPerson(connection, id); //TODO: validace, že je to opravdu patient
            PatientsDetails details =  getPatientDetails(connection, id);

            return new Patient(person, details);
        } catch  (SQLException e) {
            System.out.println(e.getMessage());
            throw new DatabaseException(DatabaseException.patientGetDatabaseError);
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

    /////////////////////////////////
    ///          DOCTOR          ///
    ////////////////////////////////

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
                    id,
                    doctorData.person().firstName(),
                    doctorData.person().lastName(),
                    doctorData.person().dateOfBirth(),
                    doctorData.details().specialization()
            ); //TODO: vytvorit konstruktory, ktere budou lepe zpracovat tyto vstupy
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.doctorInsertDatabaseError);
        }
    }

    /////////////////////////////////
    ///       Appointment        ///
    ////////////////////////////////

    /**
     * Stores new appointment in the database.
     *
     * @param connection Connection to the database.
     * @param patientId Patients identifier.
     * @param doctorId Doctors identifier.
     * @param startTime Starting time of appointment.
     * @param endTime Ending time of appointment.
     * @return Identifier of appointment.
     * @throws SQLException Error connected to the failure of storing new appointment into database or retrieving identifier from database.
     */
    private int pushAppointmentIntoDatabase(Connection connection, int patientId, int doctorId, LocalDateTime startTime, LocalDateTime endTime) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertAppointment)){
            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setString(3, startTime.toString());
            statement.setString(4, endTime.toString());

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
     * Adds new appointment to the database.
     *
     * @param patientId Patients identifier.
     * @param doctorId Doctors identifier.
     * @param startTime Starting time of appointment.
     * @param endTime Ending time of appointment.
     * @throws DatabaseException Error connected to the failure of storing new appointment.
     */
    public void addAppointment(int patientId, int doctorId, LocalDateTime startTime, LocalDateTime endTime) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int appointmentId = pushAppointmentIntoDatabase(connection, patientId, doctorId, startTime, endTime);

            connection.commit();

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
        addAppointment(patient.getId(), doctor.getId(), startTime, endTime);
    }
}
