package hospitalsystem.database;

import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;

import java.sql.*;
import java.time.LocalDate;

public class Database {
    private static final String insertPerson = "INSERT INTO people(firstname, lastname, birth_date, type) VALUES (?, ?, ?, ?)";
    private static final String insertPatientDetail = "INSERT INTO patients_details(id, anamnesis) VALUES (?, ?)";
    private static final String insertDoctorDetail = "INSERT INTO doctors_details(id, specialization) VALUES (?, ?)";
    private static final String insertPatient = "INSERT INTO patients(firstname, lastname, birth_date, anamnesis) VALUES (?, ?, ?, ?)";
    private static final String insertDoctor = "INSERT INTO patients(firstname, lastname, birth_date, specialization) VALUES (?, ?, ?, ?)";

    private final String url;

    public Database(String url){
        this.url = url;

        try (Connection conn = DriverManager.getConnection(url)) {
            //System.out.println("Connection to SQLite has been established.");

            Statement stmt =  conn.createStatement();
            //stmt.execute("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY, firstname text NOT NULL, lastname text NOT NULL, birth_date TEXT NOT NULL CHECK ( birth_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]' and date(birth_date) IS NOT NULL), anamnesis text NOT NULL);");

            stmt.execute("CREATE TABLE IF NOT EXISTS people (id INTEGER PRIMARY KEY, firstname TEXT NOT NULL, lastname TEXT NOT NULL, birth_date TEXT NOT NULL CHECK ( birth_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]' and date(birth_date) IS NOT NULL), type TEXT NOT NULL CHECK (type IN ('patient', 'doctor')));");

            stmt.execute("CREATE TABLE IF NOT EXISTS patients_details (id INTEGER PRIMARY KEY, anamnesis TEXT NOT NULL, FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE);");

            stmt.execute("CREATE TABLE IF NOT EXISTS doctors_details (id INTEGER PRIMARY KEY, specialization TEXT NOT NULL, FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private int getLastUsedId(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()){
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException("Unable to get generated key.");
        }
    }

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

    public void addPatientDetails(Connection connection, int id, String anamnesis) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertPatientDetail)){
            statement.setInt(1, id);
            statement.setString(2, anamnesis);

            statement.executeUpdate();
        }
    }

    public Patient addPatient(String firstname, String lastname, LocalDate birthDate, String anamnesis) throws Exception {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, firstname, lastname, birthDate, "patient");
            System.out.println("ahoj");
            addPatientDetails(connection, id, anamnesis);

            connection.commit();

            return new Patient(id, firstname, lastname, birthDate, anamnesis);
        } catch  (SQLException e) {
            System.out.println(e.getMessage());
            throw new Exception("Unable to correctly add patient into database"); //TODO: use custom exception and use constant for the message
        }

        /*
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(insertPatient);

            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, birthDate.toString());
            stmt.setString(4, anamnesis);

            stmt.executeUpdate();

            try {
                int id = getLastUsedId(stmt);

                conn.commit();

                return new Patient(id, firstname, lastname, birthDate, anamnesis);
            } catch (SQLException e) {
                conn.rollback();

                throw e;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            throw new Exception("Unable to correctly add patient into database"); //TODO: use custom exception and use constant for the message
        }
         */
    }

    public void addDoctorDetails(Connection connection, int id, String specialization) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(insertDoctorDetail)){
            statement.setInt(1, id);
            statement.setString(2, specialization);

            statement.executeUpdate();
        }
    }

    public Doctor addDoctor(String firstname, String lastname, LocalDate birthDate, String specialization) throws Exception {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            int id = addPerson(connection, firstname, lastname, birthDate, "doctor");
            addDoctorDetails(connection, id, specialization);

            connection.commit();

            return new Doctor(id, firstname, lastname, birthDate, specialization);
        } catch  (SQLException e) {
            throw new Exception("Unable to correctly add doctor into database"); //TODO: use custom exception and use constant for the message
        }
    }
}
