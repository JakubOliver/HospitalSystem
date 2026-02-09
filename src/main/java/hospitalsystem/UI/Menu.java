package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

abstract class Menu implements Page{
    HospitalAPI api;

    Menu(HospitalAPI api) {
        this.api = api;
    }

    protected static int getOption(Scanner scanner, int range){
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

    protected static PersonData getPersonData(Scanner scanner){
        System.out.print("First name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Date of birth (YYYY-MM-DD): ");
        String dateOfBirth = scanner.nextLine();

        return new PersonData(firstName, lastName, dateOfBirth);
    }

    protected static void clearConsole()
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

    protected boolean createNew(Scanner scanner, String what){
        System.out.print("Use existing " + what + ": ");
        String existing = scanner.nextLine();

        return existing.trim().equals("Y");
    }
}
