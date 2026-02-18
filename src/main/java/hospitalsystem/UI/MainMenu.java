package hospitalsystem.UI;

import hospitalsystem.Hospital;

import java.util.Scanner;

/**
 * Menu page containing crossroad to other submenus.
 */
public class MainMenu extends Menu{
    private static final String header =
            """
            
            /$$   /$$                               /$$   /$$               /$$        /$$$$$$                        /$$
           | $$  | $$                              |__/  | $$              | $$       /$$__  $$                      | $$
           | $$  | $$  /$$$$$$   /$$$$$$$  /$$$$$$  /$$ /$$$$$$    /$$$$$$ | $$      | $$  \\__/ /$$   /$$  /$$$$$$$ /$$$$$$    /$$$$$$  /$$$$$$/$$$$
           | $$$$$$$$ /$$__  $$ /$$_____/ /$$__  $$| $$|_  $$_/   |____  $$| $$      |  $$$$$$ | $$  | $$ /$$_____/|_  $$_/   /$$__  $$| $$_  $$_  $$
           | $$__  $$| $$  \\ $$|  $$$$$$ | $$  \\ $$| $$  | $$      /$$$$$$$| $$       \\____  $$| $$  | $$|  $$$$$$   | $$    | $$$$$$$$| $$ \\ $$ \\ $$
           | $$  | $$| $$  | $$ \\____  $$| $$  | $$| $$  | $$ /$$ /$$__  $$| $$       /$$  \\ $$| $$  | $$ \\____  $$  | $$ /$$| $$_____/| $$ | $$ | $$
           | $$  | $$|  $$$$$$/ /$$$$$$$/| $$$$$$$/| $$  |  $$$$/|  $$$$$$$| $$      |  $$$$$$/|  $$$$$$$ /$$$$$$$/  |  $$$$/|  $$$$$$$| $$ | $$ | $$
           |__/  |__/ \\______/ |_______/ | $$____/ |__/   \\___/   \\_______/|__/       \\______/  \\____  $$|_______/    \\___/   \\_______/|__/ |__/ |__/
                                         | $$                                                   /$$  | $$
                                         | $$                                                  |  $$$$$$/
           """;

    /**
     * Creates main menu page and runs the UI cycle.
     *
     * @param api Hospital providing the menu options how to interact with hospital system.
     */
    public MainMenu(Hospital api) {
        super(api, new Scanner(System.in));
    }

    /**
     * Creats main menu page and runs the UI cycle.
     *
     * @param api Hospital providing the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public MainMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Patients", () -> new PatientMenu(api, scanner));
        addOption("Doctors",  () -> new DoctorMenu(api, scanner));
        addOption("Calendar",  () -> new AppointmentMenu(api, scanner));
        addOption("Export", () -> new ExportMenu(api, scanner));
        addOption("End", this::end);
    }

    @Override
    public void printMenu(){
        clearConsole();
        System.out.println(header);

        super.printMenu();
    }
}
