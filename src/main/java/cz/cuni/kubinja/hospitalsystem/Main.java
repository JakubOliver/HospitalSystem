package cz.cuni.kubinja.hospitalsystem;

import cz.cuni.kubinja.hospitalsystem.UI.MainMenu;
import cz.cuni.kubinja.hospitalsystem.database.DatabaseException;

/**
 * Contains main entry point of the program.
 */
public class Main {
    /** Default path of the database */
    private static final String databasePath = "jdbc:sqlite:database.db";

    /**
     * Private constructor of Main class.
     */
    private Main(){}

    /**
     * Main entry point of the program.
     *
     * @param args Arguments of the program.
     */
    public static void main(String[] args) {
        Hospital hospital;
        try {
            hospital = new Hospital(databasePath);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return;
        }

        MainMenu mainMenu = new MainMenu(hospital);
    }
}
