package hospitalsystem;

import hospitalsystem.UI.MainMenu;

//TODO: info na uvidni strance

/**
 * Contains main entry point of the program.
 */
public class Main {
    private static final String databasePath = "jdbc:sqlite:database.db";

    private Main(){}

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
