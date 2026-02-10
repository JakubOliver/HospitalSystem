package hospitalsystem.UI;

import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.List;
import java.util.Scanner;

/**
 * Menu page containing options connected with patients.
 */
public class PatientMenu extends Submenu {
    /**
     * Creates patient menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public PatientMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void printMenu() {
        System.out.println("1. Add new patient");
        System.out.println("2. Edit existing patient");
        System.out.println("3. Delete existing patient");
        System.out.println("4. Find patient by ID");
        System.out.println("5. Find patient by name");
        System.out.println("6. Show all patients");
        System.out.println("7. Back");
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 7)){
            case 1:
                addPatient(); break;
            case 4:
                findById(); break;
            case 6:
                findAllPatient(); break;
        }
    }

    /**
     * Prints information about patient with provided ID.
     * <p>
     * ID is provided via input data in class scanner.
     */
    private void findById(){
        int id = getInteger(scanner, "ID: ");

        System.out.println(api.findPatient(id));

        waitForEnter(scanner);
    }

    /**
     * Processes input data and calls for creating of new patient in hospital system.
     */
    private void addPatient(){
        PersonData personData = getPersonData(scanner);

        String anamnesis = getString(scanner, "Anamnesis: ");

        api.addPatient(new PatientData(personData, new PatientsDetails(anamnesis)));

        waitForEnter(scanner);
    }

    /**
     * Prints all patients in the hospital system.
     */
    private void findAllPatient(){
        List<String> patients = api.findAllPatients();

        for(String patient : patients){
            System.out.println(patient);
        }

        waitForEnter(scanner);
    }
}
