package cz.cuni.kubinja.hospitalsystem.TUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientsDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;

import cz.cuni.kubinja.hospitalsystem.menu.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Abstract ancestor for menu pages. Implementing various methods with processing input data.
 */
public abstract class Menu implements Page{
    /** Connection to the hospital system */
    protected Hospital api;
    /** Scanner pointing to the input data */
    protected Scanner scanner;

    /** Options of the menu page */
    protected List<MenuEntry> options = new ArrayList<>();
    /** State in which menu page is */
    protected UIState state = UIState.RUN;

    /** Sequence that forcefully stop entering data */
    protected static final String exitSequence = "cancel";

    /**
     * Abstract constructor used in the chain of construction.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public Menu(Hospital api, Scanner scanner) {
        this(api, scanner, false);
    }

    /**
     * Creates dummy menu page.
     * <p>
     * This way how to create menu is only used for testing, because we want to have option how to call directly menu options and not only via prepared input data.
     *
     * @param api Hospital api gives menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     * @param dummy Decides whether the mu page will be dummy (do not show menu, but only processes direct method calls)
     */
    public Menu(Hospital api, Scanner scanner, boolean dummy) {
        this.api = api;
        this.scanner = scanner;

        if (!dummy) {
            defineMenu();

            while (state == UIState.RUN){
                printMenu();
                processMenu();
            }
        }
    }

    @Override
    public void printMenu(){
        clearConsole();
        System.out.println(header);
        System.out.println();
        IntStream.range(0, options.size())
                .mapToObj(i -> enumerateOption(i + 1, options.get(i).toString()))
                .forEach(System.out::println);
    }

    @Override
    public void processMenu(){
        int idx = getOption(options.size());

        try {
            options.get(idx).method().run();
        } catch (CancelException _) {}

        /*
        There are many ways how to process that the user wants to stop filing inputs
        for updating, creating etc. I choose the option when the input that triggers
        the stop of filling also triggers custom exception.
        Maybe better way how to counter this problem is to crete another state machine
        similar to the UIState. Unfortunately this implies that after (or before) all calls for
        data we would be required to check whether the state changed, and we would have huge
        amount of ifs everywhere. We can say same conclusion about the approach using Optional
        or some default values.
        Another approach similar to the previous is using some functional interface, that would
        have set some default function like beforeDo and this method would determine whether
        the main function would be called. But for the correct functionality we would have to
        wrap almost very function in this functional interface (methods for processing inputs and
        also hospital system calls).
         */
    }

    /**
     * Contacts number and text of option.
     *
     * @param number Number of option.
     * @param option Text of option.
     * @return concatenated number nad text of option.
     */
    private static String enumerateOption(int number, String option){
        return number + ". " + option;
    }

    /**
     * Adds new option into the menu.
     *
     * @param text Text of the option.
     * @param action Action that will be called every time the option is selected.
     */
    protected void addOption(String text, Runnable action){
        options.add(new MenuEntry(text, action));
    }

    /**
     * Sets state of the menu to the end.
     */
    protected void end(){
        state =  UIState.END;
    }

    /**
     * Processes input and returns extracted option.
     *
     * @param range Upper bound for not zero positive range of options, thus (0, range]
     * @return First valid option in the input stream.
     */
    public int getOption(int range){
        int option = getInteger("Select an option: ");

        while (option > range || option <= 0){
            System.out.println("Invalid option: out of range");
            option = getInteger("Select an option: ");
        }

        return option - 1;
    }

    /**
     * Processes input and extracts valid information into Person data wrapper.
     *
     * @param person Person whose data will be user as the default values.
     * @return Wrapper containing data for Person creating (without id)
     */
    public PersonData getPersonData(Person person){
        String firstName = getString(
                getQuestion("First name", person != null ? person.getFirstName() : ""),
                person != null ? person.getFirstName() : null
        );

        String lastName = getString(
                getQuestion("Last name", person != null ? person.getLastName() : ""),
                person != null ? person.getLastName() : null
        );

        LocalDate dateOfBirth = getDate(
                getQuestion("Date of birth", person != null ? person.getDateOfBirth().toString() : ""),
                person != null ? person.getDateOfBirth() : null
        );

        return new PersonData(firstName, lastName, dateOfBirth);
    }

    /**
     * Processes input and extracts valid information into Person data wrapper.
     *
     * @return Person data wrapper containing data for person creation.
     */
    public PersonData getPersonData(){
        return getPersonData(null);
    }

    /**
     * Processes input and extracts valid information into patient details data wrapper.
     *
     * @param patient Patient object that will be used as default values.
     * @return Patient details data wrapper.
     */
    public PatientsDetails getPatientDetails(Patient patient){
        String anamnesis = getString(
                getQuestion("Anamnesis", patient != null ? patient.getAnamnesis() : ""),
                patient != null ? patient.getAnamnesis() : null
        );

        return new PatientsDetails(anamnesis);
    }

    /**
     * Processes input and extracts valid information into patient details data wrapper.
     *
     * @return Patient details data wrapper.
     */
    public PatientsDetails getPatientDetails(){
        return getPatientDetails(null);
    }

    /**
     * Processes input and extracts valid information into doctors details data wrapper.
     *
     * @param doctor Doctor object that will be used as default values.
     * @return Doctors details data wrapper.
     */
    public DoctorDetails getDoctorDetails(Doctor doctor){
        String specialization = getString(
                getQuestion("Specialization", doctor != null ? doctor.getSpecialization() : ""),
                doctor != null ? doctor.getSpecialization() : null
        );

        String department = getString(
                getQuestion("Department", doctor != null ? doctor.getDepartment() : ""),
                doctor != null ? doctor.getDepartment() : null
        );

        return new DoctorDetails(specialization, department);
    }

    /**
     * Processes input and extracts valid information into doctors details data wrapper.
     *
     * @return Doctor details data wrapper.
     */
    public DoctorDetails getDoctorDetails(){
        return getDoctorDetails(null);
    }

    /**
     * Returns question, where the default value is not present.
     *
     * @param question Text of the question.
     * @return question, where the default value is not present.
     */
    public String getQuestion(String question){
        return getQuestion(question, "");
    }

    /**
     * Returns question, where will be displayed default value.
     *
     * @param question Text of the question.
     * @param defaultValue Default value for the question.
     * @return Question, where will be displayed default value.
     */
    public String getQuestion(String question, String defaultValue){
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
    public void clearConsole()
    {
        //Used based on: https://intellipaat.com/blog/java-clear-the-console/
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
     * @param question Question with which will be user prompted.
     * @return Valid integer.
     */
    public int getInteger(String question){
        boolean acquiredCorrectInteger = false;
        int number = 0;
        String line;

        while (!acquiredCorrectInteger) {
            line = getString(question);

            try{
                number = Integer.parseInt(line.trim());
                acquiredCorrectInteger = true;
            } catch (NumberFormatException e){
                System.out.println("Invalid input: not a valid number");
            }
        }

        return number;
    }

    /**
     * Processes input and extracts first not empty line.
     *
     * @param question Question with which will be user prompted.
     * @param defaultValue Default value that will be suggested to the user.
     * @return Not empty string.
     */
    public String getString(String question, String defaultValue){
        String line;
        System.out.print(question);

        while (scanner.hasNextLine()){
            line = scanner.nextLine().trim();

            if (line.equals(exitSequence)){
                throw new CancelException(CancelException.userCancel);
            }

            if (!line.isEmpty()){
                return line;
            } else if (defaultValue != null) {
                return defaultValue;
            }

            System.out.println("Invalid input: enter not empty string.");
            System.out.print(question);
        }

        throw new InputMismatchException("Scanner run out of lines and no correct string found.");
    }

    /**
     * Processes input and extracts first not empty line.
     *
     * @param question Question with which will be user prompted.
     * @return First not empty line.
     */
    public String getString(String question){
        return getString(question, null);
    }

    /**
     * Processes input and extracts first line containing valid date (in format YYYY-MM-DD)
     *
     * @param question Question with which will be user prompted.
     * @param defaultValue Default value that will be suggested to the user.
     * @return LocalDate object containing valid date.
     */
    public LocalDate getDate(String question,LocalDate defaultValue){
        boolean acquireCorrectDate = false;
        LocalDate date = null; // Even thought the value here is null, if we get to the return statement in the date variable will always be valid date.

        while (!acquireCorrectDate){
            String line = getString(question, defaultValue != null ? defaultValue.toString() : null);

            try{
                date = LocalDate.parse(line.trim());

                if (!Calendar.isWithinValidDates(date)){
                    System.out.println("Invalid input: date have to be between year 1900 and present");
                } else {
                    acquireCorrectDate = true;
                }
            } catch (DateTimeParseException _){
                System.out.println("Invalid input: not a valid date, date must be in format YYYY-MM-DD.");
            }
        }

        return date;
    }

    /**
     * Processes input and extracts first line containing valid date (int format YYYY-MM-DD)
     *
     * @param question Question with which will be user prompted.
     * @return LocalDate object containing valid date.
     */
    public LocalDate getDate(String question){
        return getDate(question, null);
    }

    /**
     * Processes input and extracts first line containing valid date and time.
     * <p>
     * (in format YYYY-MM-DDTHH:MM or YYYY-MM-DD HH:MM)
     *
     * @param question Question with which will be user prompted.
     * @return LocalDateTime object containing valid date and time.
     */
    public LocalDateTime getDateTime(String question){
        boolean acquireCorrectDateTime = false;
        LocalDateTime date = null;

        while (!acquireCorrectDateTime){
            String line = getString(question);

            if (line.contains(" ")){
                String[] parts = line.split(" ");
                line = String.join("T", parts);
            }

            try{
                date =  LocalDateTime.parse(line.trim());

                if (!Calendar.isAppointmentWithinValidDateTime(date)){
                    System.out.println("Invalid input: date have to be between year 2000-01-01 and 3000-01-01.");
                } else {
                    acquireCorrectDateTime = true;
                }
            } catch (DateTimeParseException _){
                System.out.println("Invalid input: not a valid date, date must be in format YYYY-MM-DD HH:MM.");
            }
        }

        return date;
    }

    /**
     * Check whether the input can be classified as true.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as true.
     */
    private boolean isValidBoolTrue(String answer){
        return answer.equals("Yes") || answer.equals("Y") || answer.equals("yes") || answer.equals("y");
    }

    /**
     * Check whether the input can be classified as false.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as false.
     */
    private boolean isValidBoolFalse(String answer){
        return answer.equals("No") || answer.equals("N") || answer.equals("no") || answer.equals("n");
    }

    /**
     * Check whether the input can be classified as boolean value.
     *
     * @param answer Input string.
     * @return Whether the input can be classified as boolean value.
     */
    private boolean isValidBoolAnswer(String answer){
        return isValidBoolTrue(answer) || isValidBoolFalse(answer);
    }

    /**
     * Checks whether the user wants to create new object.
     *
     * @param what The object in question.
     * @return Whether the user wants to create new object.
     */
    public boolean createNew(String what){
        return getBool("Create new " + what);
    }

    /**
     * Processes input from scanner and returns value after processing first line that contains some text that can be converted to bool value.
     *
     * @param question Question with which will be user prompted.
     * @return Value after processing first line that contains some text that can be converted to bool value.
     */
    public boolean getBool(String question){
        question += " (Yes/No) : ";
        String line = getString(question);

        while (!isValidBoolAnswer(line)){
            System.out.println("Invalid input!");

            line = getString(question);
        }

        return isValidBoolTrue(line);
    }

    /**
     * Show waiting text and prompts user into pressing any key.
     */
    public void waitForEnter(){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    /**
     * Prints text and wait for the confirmation action from the user.
     *
     * @param text Text that will be displayed.
     */
    public void printAndWait(String text){
        System.out.println(text);
        waitForEnter();
    }

    /**
     * Processes packet status and prints the resolved status.
     *
     * @param packet Packet that will be processed.
     * @param silence Whether we do not want to display text when the packet does not contain exception.
     * @return Whether the packet came from successful API request.
     */
    private boolean processPacketStatus(GeneralPacket packet, boolean silence){
        if (!silence || !packet.successful){
            System.out.println(packet.resolveStatus());

            waitForEnter();

            return packet.successful;
        }

        return true;
    }

    /**
     * Processes packet status and prints the resolved status.
     *
     * @param packet Packet that will be processed.
     * @return Whether the packet came from successful API request.
     */
    public boolean processPacketStatus(GeneralPacket packet){
        return processPacketStatus(packet, false);
    }

    /**
     * Processes packet status and prints the status if the API request was not successful.
     *
     * @param packet Packet that will be processed.
     * @return Whether the packet came from successful API request.
     */
    public boolean processPacketStatusInSilence(GeneralPacket packet){
        return processPacketStatus(packet, true);
    }

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
}
