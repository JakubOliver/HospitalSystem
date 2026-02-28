package hospitalsystem;

import hospitalsystem.UI.MainMenu;

/**
 * Contains main entry point of the program.
 */
public class Main {
    /** Default path of the database */
    private static final String databasePath = "jdbc:sqlite:database.db";

    /**
     * Main entry point of the program.
     *
     * @param args Arguments of the program.
     */
    public static void main(String[] args) {
        Hospital hospital = new Hospital(databasePath);

        MainMenu mainMenu = new MainMenu(hospital);
    }
}
