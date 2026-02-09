package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PatientMenu extends Submenu {
    PatientMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);

        printMenu();
        processMenu();
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
                addPatient(scanner); break;
            case 6:
                findAllPatient(scanner); break;
        }
    }

    private void addPatient(Scanner scanner){
        PersonData personData = getPersonData(scanner);

        System.out.print("Anamnesis: ");
        String anamnesis = scanner.nextLine();

        api.addPatient(new PatientData(personData, new PatientsDetails(anamnesis)));

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    private void findAllPatient(Scanner scanner){
        api.findAllPatients();

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
