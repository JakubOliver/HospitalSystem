package hospitalsystem;

import hospitalsystem.database.Database;

import java.time.LocalDate;
import java.util.Scanner;

public class Hospital {
    enum UIState{
        RUN,
        END,
    }

    private final Database database;
    private UIState uiState = UIState.RUN;

    private final String header =
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

    public Hospital(String databasePath){
        database = new Database(databasePath);

        UI();
    }

    private void clearConsole()
    {
        String os = System.getProperty("os.name");
        try {
            System.out.println(os);
            if (os.startsWith("Windows")) {
                // Command for Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Command for Linux/Mac
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }

        } catch (Exception e) {
            System.out.println("Error while clearing console: " + e.getMessage());
        }
    }

    private void printMainMenu(){
        clearConsole();

        System.out.println(header);

        System.out.println("1. Patients");
        System.out.println("2. End");
    }

    private void processMainMenu(Scanner scanner){
        printMainMenu();

        switch (getOption(scanner, 3)){
            case 1:
                processPatientMenu(scanner); break;
            case 2:
                uiState =  UIState.END; break;
        }
    }

    private int getOption(Scanner scanner, int range){
        int option = 0; //TODO: constant
        String line;

        do {
            System.out.print("Select an option: ");

            line =  scanner.nextLine();

            try {
                option = Integer.parseInt(line.trim());

                if (option >= range || option <= 0){
                    System.out.println("Invalid option: out of range");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option: invalid format");
            }

        } while (option >= range || option <= 0);

        return option;
    }

    private void processPatientMenu(Scanner scanner){
        System.out.println("1. Add");
        System.out.println("2. Back");

        switch (getOption(scanner, 3)){
            case 1:
                addPatient(scanner); break;
        }
    }

    private void addPatient(Scanner scanner){
        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Date of birth (YYYY-MM-DD): ");
        String dateOfBirth = scanner.nextLine();

        System.out.print("Anamnesis: ");
        String anamnesis = scanner.nextLine();

        //TODO: validate

        try {
            database.addPatient(firstName, lastName, LocalDate.parse(dateOfBirth), anamnesis);

            System.out.println("Success!");
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    private void UI(){
        try (Scanner scanner = new Scanner(System.in)) {
            while (uiState != UIState.END){
                processMainMenu(scanner);
            }
        }

    }
}
