package hospitalsystem.UI;

import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

/**
 * Menu page containing options connected with doctors.
 */
public class DoctorMenu extends Submenu {
    /**
     * Creates doctor menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public DoctorMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void printMenu() {
        System.out.println("1. Add new doctor");
        System.out.println("2. Back"); //TODO: mozna spotit na volani s PetientMenu
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 2)){
            case 1:
                addDoctor();
        }
    }

    /**
     * Processes input data and calls for creating of new doctor in hospital system.
     */
    private void addDoctor(){
        PersonData personData = getPersonData(scanner);

        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();

        DoctorData  doctorData = new DoctorData(personData, new DoctorDetails(specialization));

        api.addDoctor(doctorData);

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
