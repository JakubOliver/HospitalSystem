package cz.cuni.kubinja.hospitalsystem.app;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.TUI.MainMenu;
//import cz.cuni.kubinja.hospitalsystem.GUI.MainMenu;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import javafx.stage.Stage;

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
        Hospital hospital;
        try {
            hospital = new Hospital(databasePath);
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (args.length > 0 && args[0].equals("-t")) {
            cz.cuni.kubinja.hospitalsystem.TUI.MainMenu mainMenu = new cz.cuni.kubinja.hospitalsystem.TUI.MainMenu(hospital);
        } else {
            cz.cuni.kubinja.hospitalsystem.GUI.MainMenu.main(args);
        }

    }
}
