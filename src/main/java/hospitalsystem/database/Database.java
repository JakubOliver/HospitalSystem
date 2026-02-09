package hospitalsystem.database;

import hospitalsystem.calendar.CalendarEntry;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.PatientsDetails;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO: zvazit zda nenahradit cast metd tim ze dostanou Query a parametry pomoci ... a potom se sestroji

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

    private int addPerson(Connection connection, String firstname, String lastname, LocalDate birthDate, String type) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertPerson)){
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, birthDate.toString());
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

    private void addPatientDetails(Connection connection, int id, String anamnesis) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertPatientDetail)){
            statement.setInt(1, id);
            statement.setString(2, anamnesis);

            statement.executeUpdate();
        }
    }

    public Patient addPatient(String firstname, String lastname, LocalDate birthDate, String anamnesis) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, firstname, lastname, birthDate, Patient.getClassIdentifier());
            addPatientDetails(connection, id, anamnesis);

            connection.commit();

            Patient patient = new Patient(id, firstname, lastname, birthDate, anamnesis);

            //SystemLogger.successfullNewPatient(patient);
            return patient;
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.patientInsertDatabaseError);
        }
    }

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

    public Patient getPatient(int id) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)){
            Person person = getPerson(connection, id);
            PatientsDetails details =  getPatientDetails(connection, id);

            return new Patient(person, details);
        } catch  (SQLException e) {
            System.out.println(e.getMessage());
            throw new DatabaseException(DatabaseException.patientGetDatabaseError);
        }
    }

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

    private void addDoctorDetails(Connection connection, int id, String specialization) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertDoctorDetail)){
            statement.setInt(1, id);
            statement.setString(2, specialization);

            statement.executeUpdate();
        }
    }

    public Doctor addDoctor(String firstname, String lastname, LocalDate birthDate, String specialization) throws DatabaseException {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, firstname, lastname, birthDate, Doctor.getClassIdentifier());
            addDoctorDetails(connection, id, specialization);

            connection.commit();

            return new Doctor(id, firstname, lastname, birthDate, specialization);
        } catch  (SQLException e) {
            throw new DatabaseException(DatabaseException.doctorInsertDatabaseError);
        }
    }

    /////////////////////////////////
    ///       Appointment        ///
    ////////////////////////////////

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

    public void addAppointment(Patient patient, Doctor doctor, LocalDateTime startTime, LocalDateTime endTime) throws DatabaseException {
        addAppointment(patient.getId(), doctor.getId(), startTime, endTime);
    }
}
