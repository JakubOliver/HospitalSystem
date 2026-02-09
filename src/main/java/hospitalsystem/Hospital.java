package hospitalsystem;

import hospitalsystem.database.Database;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

record PersonInputData(String firstName, String lastName, String dateOfBirth){}

public class Hospital {
    enum UIState{
        RUN,
        END,
    }

    private final Database database;
    private UIState uiState = UIState.RUN;

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
        System.out.println("2. Doctors");
        System.out.println("3. Calendar");
        System.out.println("4. End");
    }

    private void processMainMenu(Scanner scanner){
        printMainMenu();

        switch (getOption(scanner, 4)){
            case 1:
                processPatientMenu(scanner); break;
            case 2:
                processDoctorMenu(scanner); break;
            case 3:
                processAppointmentMenu(scanner); break;
            case 4:
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

                if (option > range || option <= 0){
                    System.out.println("Invalid option: out of range");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option: invalid format");
            }

        } while (option > range || option <= 0);

        return option;
    }

    private PersonInputData getPersonData(Scanner scanner){
        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Date of birth (YYYY-MM-DD): ");
        String dateOfBirth = scanner.nextLine();

        return new PersonInputData(firstName, lastName, dateOfBirth);
    }

    private void processPatientMenu(Scanner scanner){
        System.out.println("1. Add new patient");
        System.out.println("2. Back");

        switch (getOption(scanner, 2)){
            case 1:
                addPatient(scanner); break;
        }
    }

    private void addPatient(Scanner scanner){
        PersonInputData personData = getPersonData(scanner);

        System.out.print("Anamnesis: ");
        String anamnesis = scanner.nextLine();

        //TODO: validate

        try {
            database.addPatient(personData.firstName(), personData.lastName(), LocalDate.parse(personData.dateOfBirth()), anamnesis);

            System.out.println("Success!");
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    private void processDoctorMenu(Scanner scanner){
        System.out.println("1. Add new doctor");
        System.out.println("2. Back"); //TODO: mozna spotit na volani s PetientMenu

        switch (getOption(scanner, 2)){
            case 1:
                addDoctor(scanner);
        }
    }

    private void addDoctor(Scanner scanner){
        PersonInputData personData = getPersonData(scanner);

        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();

        //TODO: validate

        try {
            database.addDoctor(personData.firstName(), personData.lastName(), LocalDate.parse(personData.dateOfBirth()), specialization);

            System.out.println("Success!");
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    private void processAppointmentMenu(Scanner scanner){
        System.out.println("1. Add new appointment");
        System.out.println("2. Back"); //TODO: mozna spotit na volani s PetientMenu

        switch (getOption(scanner, 2)){
            case 1:
                addAppointment(scanner);
        }
    }

    private boolean createNew(Scanner scanner, String what){
        System.out.print("Use existing " + what + ": ");
        String existing = scanner.nextLine();

        return existing.trim().equals("Y");
    }

    private void addAppointment(Scanner scanner){
        if (createNew(scanner, "patient")){
        }

        try {
            database.addAppointment(
                    new Patient(5, "Kamil", "Dorazil", LocalDate.of(1999, 1, 1), "Broken leg"),
                    new Doctor(3, "Pepa", "Novak", LocalDate.of(1990, 2, 2), "Surgeon"),
                    LocalDateTime.of(2026,9,2,13,0),
                    LocalDateTime.of(2025,9,2,14,0));

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
