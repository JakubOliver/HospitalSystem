package cz.cuni.kubinja.hospitalsystem.app;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;

import java.util.Arrays;

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
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && Arrays.asList(args).contains("-t")) {
            Hospital hospital;
            try {
                hospital = new Hospital(databasePath);
            } catch (DatabaseException e) {
                System.out.println(e.getMessage());
                return;
            }

            new cz.cuni.kubinja.hospitalsystem.TUI.MainMenu(hospital);
        } else {
            cz.cuni.kubinja.hospitalsystem.GUI.MainGUI.main(args);
        }
    }
}
