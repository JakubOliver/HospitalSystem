package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;

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
    Hospital api;
    Scanner scanner;

    List<MenuEntry> options = new ArrayList<>();
    UIState state = UIState.RUN;

    /**
     * Abstract constructor used in the chain of construction.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     */
    public Menu(Hospital api, Scanner scanner) {
        this(api, scanner, false);
    }

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
        IntStream.range(0, options.size())
                .mapToObj(i -> enumerateOption(i + 1, options.get(i).toString()))
                .forEach(System.out::println);
    }

    @Override
    public void processMenu(){
        int idx = getOption(options.size());

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
     * @param scanner Scanner pointing to the input data.
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

    public PersonData getPersonData(){
        return getPersonData(null);
    }

    //TODO: zvazit zda neprehodit tyto metody jako metody trid jako details atd. pomoci nejake motady jako of

    public PatientsDetails getPatientDetails(Patient patient){
        String anamnesis = getString(
                getQuestion("Anamnesis", patient != null ? patient.getAnamnesis() : ""),
                patient != null ? patient.getAnamnesis() : null
        );

        return new PatientsDetails(anamnesis);
    }

    public PatientsDetails getPatientDetails(){
        return getPatientDetails(null);
    }

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

    public DoctorDetails getDoctorDetails(){
        return getDoctorDetails(null);
    }

    public String getQuestion(String question){
        return getQuestion(question, "");
    }

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
    public int getInteger(String question){
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
    public String getString(String question, String defaultValue){
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

    public String getString(String question){
        return getString(question, null);
    }

    /**
     * Processes input and extracts first line satisfying regular expression.
     *
     * @param scanner Scanner pointing to the input data.
     * @param question Question with which will be user prompted.
     * @param regex Regular expression which has to be satisfied.
     * @return Not empty string satisfying provided regular expression.
     */
    public String satisfyRegex(String question, String regex){
        Pattern pattern = Pattern.compile(regex);

        String text = getString(question);

        while (pattern.matcher(text).matches()){
            text = getString(question);
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
    public LocalDate getDate(String question,LocalDate defaultValue){
        boolean acquireCorrectDate = false;
        LocalDate date = null; // Even thought the value here is null, if we get to the return statement in the date variable will always be valid date.

        while (!acquireCorrectDate){
            String line = getString(question, defaultValue != null ? defaultValue.toString() : null);

            try{
                date = LocalDate.parse(line.trim());

                acquireCorrectDate = true;
            } catch (DateTimeParseException _){}
        }

        return date;
    }

    public LocalDate getDate(String question){
        return getDate(question, null);
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

                acquireCorrectDateTime = true;
            } catch (DateTimeParseException _){}
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
     * @param scanner Scanner pointing to the input data.
     * @param what The object in question.
     * @return Whether the user wants to create new object.
     */
    public boolean createNew(String what){
        String question = "Create new " + what + " (Yes/No) : ";

        String line = getString(question);

        while (!isValidBoolAnswer(line)){
            System.out.println("Invalid input!");

            line = getString(question);
        }

        return isValidBoolTrue(line);
    }

    /**
     * Show waiting text and prompts user into pressing any key.
     *
     * @param scanner Scanner pointing to the input data.
     */
    public void waitForEnter(){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    public void printAndWait(String text){
        System.out.println(text);
        waitForEnter();
    }

    private boolean processPacketStatus(GeneralPacket packet, boolean silence){
        if (!silence || !packet.successful){
            System.out.println(packet.resolveStatus());

            waitForEnter();

            return packet.successful;
        }

        return true;
    }

    public boolean processPacketStatus(GeneralPacket packet){
        return processPacketStatus(packet, false);
    }

    public boolean processPacketStatusInSilence(GeneralPacket packet){
        return processPacketStatus(packet, true);
    }
}
