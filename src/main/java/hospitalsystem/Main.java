package hospitalsystem;

import hospitalsystem.database.Database;
import hospitalsystem.personnel.Doctor;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Doctor doctor = new Doctor(0, "John", "Brown", 1,3,1990, "Surgeon");

        //System.out.println(doctor);

        Database database = new Database("jdbc:sqlite:database.db");

        try {
            System.out.println(database.addPatient("John", "Brown", LocalDate.now(), "Cancer"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
