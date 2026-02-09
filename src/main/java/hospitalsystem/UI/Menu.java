package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;
import org.apache.commons.digester3.RegexMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    protected int getInteger(Scanner scanner, String question){
        System.out.print(question);
        String line = scanner.nextLine();

        while (true){
            try{
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e){
                System.out.println("Invalid input");

                System.out.print(question);
                line = scanner.nextLine();
            }
        }
    }

    protected String getString(Scanner scanner, String question){
        System.out.print(question);
        String line = scanner.nextLine();

        while (line.isEmpty()){
            System.out.println("Invalid input");

            System.out.print(question);
            line = scanner.nextLine();
        }

        return line;
    }

    protected String satisfyRegex(Scanner scanner, String question, String regex){
        Pattern pattern = Pattern.compile(regex);

        String text = getString(scanner, question);

        while (pattern.matcher(text).matches()){
            text = getString(scanner, question);
        }

        return text;
    }

    protected LocalDate getDate(Scanner scanner, String question){
        String line = getString(scanner, question);

        while (true){
            try{
                return LocalDate.parse(line);
            } catch (DateTimeParseException e){
                line = getString(scanner, question);
            }
        }
    }

    protected LocalDateTime getDateTime(Scanner scanner, String question){
        String line = getString(scanner, question);

        while (true){
            try{
                return LocalDateTime.parse(line);
            } catch (DateTimeParseException e){
                line = getString(scanner, question);
            }
        }
    }

    protected boolean createNew(Scanner scanner, String what){
        System.out.print("Use existing " + what + ": ");
        String existing = scanner.nextLine();

        return existing.trim().equals("Y");
    }

    protected void waitForEnter(Scanner scanner){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
