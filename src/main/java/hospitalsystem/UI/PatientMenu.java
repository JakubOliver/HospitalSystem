package hospitalsystem.UI;

import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

public class PatientMenu extends Submenu {
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

    private void findById(){
        int id = getInteger(scanner, "ID: ");

        System.out.println(api.findPatient(id));

        waitForEnter(scanner);
    }

    private void addPatient(){
        PersonData personData = getPersonData(scanner);

        String anamnesis = getString(scanner, "Anamnesis: ");

        api.addPatient(new PatientData(personData, new PatientsDetails(anamnesis)));

        waitForEnter(scanner);
    }

    private void findAllPatient(){
        api.findAllPatients();

        waitForEnter(scanner);
    }
}
