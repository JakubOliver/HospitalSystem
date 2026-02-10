package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;
import org.apache.commons.digester3.RegexMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class Menu implements Page{
    HospitalAPI api;

    public Menu(HospitalAPI api) {
        this.api = api;
    }

    public static int getOption(Scanner scanner, int range){
        int option = getInteger(scanner, "Select an option: ");

        while (option > range || option <= 0){
            System.out.println("Invalid option: out of range");
            option = getInteger(scanner, "Select an option: ");
        }

        return option;
    }

    public static PersonData getPersonData(Scanner scanner){
        String firstName = getString(scanner, "First name: ");
        String lastName = getString(scanner, "Last name: ");
        LocalDate dateOfBirth = getDate(scanner, "Date of birth: ");

        return new PersonData(firstName, lastName, dateOfBirth);
    }

    public static void clearConsole()
    {
        String os = System.getProperty("os.name");
        try {
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

    public static int getInteger(Scanner scanner, String question){
        while (scanner.hasNextLine()) {
            System.out.print(question);
            String line = scanner.nextLine();

            try{
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e){
                System.out.println("Invalid input: not a valid number");
            }
        }

        throw new InputMismatchException("Scanner run out of lines and no correct integer found.");
    }

    public static String getString(Scanner scanner, String question){
        System.out.print(question);
        String line = "";

        while (scanner.hasNextLine()){
            System.out.print(question);
            line = scanner.nextLine();

            if (!line.isEmpty()){
                return line.trim();
            }

            System.out.println("Invalid input: enter not empty string.");
        }

        throw new InputMismatchException("Scanner run out of lines and no correct string found.");
    }

    public static String satisfyRegex(Scanner scanner, String question, String regex){
        Pattern pattern = Pattern.compile(regex);

        String text = getString(scanner, question);

        while (pattern.matcher(text).matches()){
            text = getString(scanner, question);
        }

        return text;
    }

    public static LocalDate getDate(Scanner scanner, String question){
        while (scanner.hasNextLine()){
            String line = getString(scanner, question);

            try{
                return LocalDate.parse(line.trim());
            } catch (DateTimeParseException _){}
        }

        throw new InputMismatchException("Scanner run out of lines and did not found valid date."); //TODO: const
    }

    public static LocalDateTime getDateTime(Scanner scanner, String question){
        while (scanner.hasNextLine()){
            String line = getString(scanner, question);

            if (line.contains(" ")){
                String[] parts = line.split(" ");
                line = String.join("T", parts);
            }

            try{
                return LocalDateTime.parse(line.trim());
            } catch (DateTimeParseException _){}
        }

        throw new InputMismatchException("Scanner run out of lines and did not found valid date and time."); //TODO: const
    }

    public static boolean createNew(Scanner scanner, String what){
        System.out.print("Use existing " + what + ": ");
        String existing = scanner.nextLine();

        return existing.trim().equals("Y");
    }

    public static void waitForEnter(Scanner scanner){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
