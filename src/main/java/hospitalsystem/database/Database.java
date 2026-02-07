package hospitalsystem.database;

import hospitalsystem.personnel.Patient;

import java.sql.*;
import java.time.LocalDate;

public class Database {
    private static final String insertPatient = "INSERT INTO patients(firstname, lastname, birth_date, anamnesis) VALUES (?, ?, ?, ?)";

    private final String url;

    public Database(String url){
        this.url = url;

        try (Connection conn = DriverManager.getConnection(url)) {
            //System.out.println("Connection to SQLite has been established.");

            Statement stmt =  conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY, firstname text NOT NULL, lastname text NOT NULL, birth_date TEXT NOT NULL CHECK ( birth_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]' and date(birth_date) IS NOT NULL), anamnesis text NOT NULL);");
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

    public Patient addPatient(String firstname, String lastname, LocalDate birthDate, String anamnesis) throws Exception {
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
    }
}
