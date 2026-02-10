package hospitalsystem.UI;

import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

/**
 * Menu page containing crossroad to other submenus.
 */
public class MainMenu extends Menu{
    UIState state = UIState.RUN;
    Scanner scanner = new Scanner(System.in);

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
    public MainMenu(HospitalAPI api) {
        super(api);

        while (state != UIState.END){
            printMenu();
            processMenu();
        }
    }

    @Override
    public void printMenu() {
        clearConsole();

        System.out.println(header);

        System.out.println("1. Patients");
        System.out.println("2. Doctors");
        System.out.println("3. Calendar");
        System.out.println("4. End");


    }

    @Override
    public void processMenu(){
        switch (getOption(scanner, 4)){
            case 1:
                new PatientMenu(api, scanner); break;
            case 2:
                new DoctorMenu(api, scanner); break;
            case 3:
                new AppointmentMenu(api, scanner); break;
            case 4:
                state =  UIState.END; break;
        }
    }
}
