package hospitalsystem;

import hospitalsystem.UI.MainMenu;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.util.HospitalAPI;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    private static final String databasePath = "jdbc:sqlite:database.db";

    public static void main(String[] args) {
        Hospital hospital = new Hospital(databasePath);

        MainMenu mainMenu = new MainMenu(new HospitalAPI(hospital));
    }
}
