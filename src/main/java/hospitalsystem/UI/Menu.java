package hospitalsystem.UI;

import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Abstract ancestor for menu pages. Implementing various methods with processing input data.
 */
abstract class Menu implements Page{
    HospitalAPI api;
    Scanner scanner;

    List<MenuEntry> options = new ArrayList<>();
    UIState state = UIState.RUN;

    /**
     * Abstract constructor used in the chain of construction.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     */
    public Menu(HospitalAPI api, Scanner scanner) {
        this.api = api;
        this.scanner = scanner;

        defineMenu();

        while (state == UIState.RUN){
            printMenu();
            processMenu();
        }
    }

    @Override
    public void printMenu(){
        IntStream.range(0, options.size())
                .mapToObj(i -> enumerateOption(i + 1, options.get(i).toString()))
                .forEach(System.out::println);
    }

    @Override
    public void processMenu(){
        int idx = getOption(scanner, options.size());

        options.get(idx).method().run();
    }

    private static String enumerateOption(int number, String option){
        return number + ". " + option;
    }

    protected void addOption(String text, Runnable action){
        options.add(new MenuEntry(text, action));
    }

    protected void end(){
        state =  UIState.END;
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

        return option - 1;
    }

    /**
     * Processes input and extracts valid information into Person data wrapper.
     *
     * @param scanner Scanner pointing to the input data.
     * @return Wrapper containing data for Person creating (without id)
     */
    public static PersonData getPersonData(Scanner scanner, Person person){
        String firstName = getString(
                scanner,
                getQuestion("First name", person != null ? person.getFirstName() : ""),
                person != null ? person.getFirstName() : null
        );

        String lastName = getString(
                scanner,
                getQuestion("Last name", person != null ? person.getLastName() : ""),
                person != null ? person.getLastName() : null
        );

        LocalDate dateOfBirth = getDate(
                scanner,
                getQuestion("Date of birth", person != null ? person.getDateOfBirth().toString() : ""),
                person != null ? person.getDateOfBirth() : null
        );

        return new PersonData(firstName, lastName, dateOfBirth);
    }

    public static PersonData getPersonData(Scanner scanner){
        return getPersonData(scanner, null);
    }

    public static String getQuestion(String question){
        return getQuestion(question, "");
    }

    public static String getQuestion(String question, String defaultValue){
        StringBuilder fullQuestion = new  StringBuilder();
        fullQuestion.append(question);

        if (!defaultValue.isEmpty()) {
            fullQuestion.append(" [");
            fullQuestion.append(defaultValue);
            fullQuestion.append("]");
        }

        fullQuestion.append(": ");

        return fullQuestion.toString();
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
    public static String getString(Scanner scanner, String question, String defaultValue){
        String line;
        System.out.print(question);

        while (scanner.hasNextLine()){
            line = scanner.nextLine();

            if (!line.isEmpty()){
                return line.trim();
            } else if (defaultValue != null) {
                return defaultValue;
            }

            System.out.println("Invalid input: enter not empty string.");
            System.out.print(question);
        }

        throw new InputMismatchException("Scanner run out of lines and no correct string found.");
    }

    public static String getString(Scanner scanner, String question){
        return getString(scanner, question, null);
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
    public static LocalDate getDate(Scanner scanner, String question,LocalDate defaultValue){
        boolean acquireCorrectDate = false;
        LocalDate date = null; // Even thought the value here is null, if we get to the return statement in the date variable will always be valid date.

        while (!acquireCorrectDate){
            String line = getString(scanner, question, defaultValue != null ? defaultValue.toString() : null);

            try{
                date = LocalDate.parse(line.trim());

                acquireCorrectDate = true;
            } catch (DateTimeParseException _){}
        }

        return date;

        //throw new InputMismatchException("Scanner run out of lines and did not found valid date."); //TODO: const
    }

    public static LocalDate getDate(Scanner scanner, String question){
        return getDate(scanner, question, null);
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
        boolean acquireCorrectDateTime = false;
        LocalDateTime date = null;

        while (!acquireCorrectDateTime){
            String line = getString(scanner, question);

            if (line.contains(" ")){
                String[] parts = line.split(" ");
                line = String.join("T", parts);
            }

            try{
                date =  LocalDateTime.parse(line.trim());

                acquireCorrectDateTime = true;
            } catch (DateTimeParseException _){}
        }

        return date;

        //throw new InputMismatchException("Scanner run out of lines and did not found valid date and time."); //TODO: const
    }

    /**
     * Check whether the input can be classified as true.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as true.
     */
    private static boolean isValidBoolTrue(String answer){
        return answer.equals("Yes") || answer.equals("Y") || answer.equals("yes") || answer.equals("y");
    }

    /**
     * Check whether the input can be classified as false.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as false.
     */
    private static boolean isValidBoolFalse(String answer){
        return answer.equals("No") || answer.equals("N") || answer.equals("no") || answer.equals("n");
    }

    /**
     * Check whether the input can be classified as boolean value.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as boolean value.
     */
    private static boolean isValidBoolAnswer(String answer){
        return isValidBoolTrue(answer) || isValidBoolFalse(answer);
    }

    /**
     * Checks whether the user wants to create new object.
     *
     * @param scanner Scanner pointing to the input data.
     * @param what The object in question.
     * @return Whether the user wants to create new object.
     */
    public static boolean createNew(Scanner scanner, String what){
        String question = "Use existing " + what + " (Yes/No) : ";

        String line = getString(scanner, question);

        while (!isValidBoolAnswer(line)){
            System.out.println("Invalid input!");

            line = getString(scanner, question);
        }

        return isValidBoolTrue(line);
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
