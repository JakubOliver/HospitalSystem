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
     * Creates main menu page and runs the IU cycle.
     *
     * @param api HospitalAPI providing the menu options how to interact with hospital system.
     */
    public MainMenu(Hospital api) {
        super(api, new Scanner(System.in));

        while (state != UIState.END){
            printMenu();
            processMenu();
        }
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
