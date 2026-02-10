package hospitalsystem.UI;

import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Abstract ancestor for menu pages. Implementing various methods with processing input data.
 */
abstract class Menu implements Page{
    HospitalAPI api;

    /**
     * Abstract constructor used in the chain of construction.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     */
    public Menu(HospitalAPI api) {
        this.api = api;
    }

    /**
     * Processes input and returns extracted option.
     *
     * @param scanner Scanner pointing to the input data.
     * @param range Upper bound for not zero positive range of options, thus (0, range]
     * @return First valid option in the input stream.
     */
    public static int getOption(Scanner scanner, int range){
        int option = getInteger(scanner, "Select an option: ");

        while (option > range || option <= 0){
            System.out.println("Invalid option: out of range");
            option = getInteger(scanner, "Select an option: ");
        }

        return option;
    }

    /**
     * Processes input and extracts valid information into Person data wrapper.
     *
     * @param scanner Scanner pointing to the input data.
     * @return Wrapper containing data for Person creating (without id)
     */
    public static PersonData getPersonData(Scanner scanner){
        String firstName = getString(scanner, "First name: ");
        String lastName = getString(scanner, "Last name: ");
        LocalDate dateOfBirth = getDate(scanner, "Date of birth: ");

        return new PersonData(firstName, lastName, dateOfBirth);
    }

    /**
     * Clears console with appropriate command based on OS
     */
    public static void clearConsole()
    {
        //TODO: rozhodnout OS na zacatku ať se to nemusi porad pocitat
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

    /**
     * Processes input and extracts first valid integer.
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @return Valid integer.
     */
    public static int getInteger(Scanner scanner, String question){
        String line;
        System.out.print(question);

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            try{
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e){
                System.out.println("Invalid input: not a valid number");
            }

            System.out.print(question);
        }

        throw new InputMismatchException("Scanner run out of lines and no correct integer found.");
    }

    /**
     * Processes input and extracts first not empty line.
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @return Not empty string.
     */
    public static String getString(Scanner scanner, String question){
        String line;
        System.out.print(question);

        while (scanner.hasNextLine()){
            line = scanner.nextLine();

            if (!line.isEmpty()){
                return line.trim();
            }

            System.out.println("Invalid input: enter not empty string.");
            System.out.print(question);
        }

        throw new InputMismatchException("Scanner run out of lines and no correct string found.");
    }

    /**
     * Processes input and extracts first line satisfying regular expression.
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @param regex Regular expression which has to be satisfied.
     * @return Not empty string satisfying provided regular expression.
     */
    public static String satisfyRegex(Scanner scanner, String question, String regex){
        Pattern pattern = Pattern.compile(regex);

        String text = getString(scanner, question);

        while (pattern.matcher(text).matches()){
            text = getString(scanner, question);
        }

        return text;
    }

    /**
     * Processes input and extracts first line containing valid date (in format YYYY-MM-DD)
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @return LocalDate object containing valid date.
     */
    public static LocalDate getDate(Scanner scanner, String question){
        while (scanner.hasNextLine()){
            String line = getString(scanner, question);

            try{
                return LocalDate.parse(line.trim());
            } catch (DateTimeParseException _){}
        }

        throw new InputMismatchException("Scanner run out of lines and did not found valid date."); //TODO: const
    }

    /**
     * Processes input and extracts first line containing valid date and time.
     * <p>
     * (in format YYYY-MM-DDTHH:MM or YYYY-MM-DD HH:MM)
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @return LocalDateTime object containing valid date and time.
     */
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

    /**
     * Show waiting text and prompts user into pressing any key.
     *
     * @param scanner Scanner pointing to the input data.
     */
    public static void waitForEnter(Scanner scanner){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
