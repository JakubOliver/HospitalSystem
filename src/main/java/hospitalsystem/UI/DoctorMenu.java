package hospitalsystem.UI;

import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

public class DoctorMenu extends Submenu {
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
