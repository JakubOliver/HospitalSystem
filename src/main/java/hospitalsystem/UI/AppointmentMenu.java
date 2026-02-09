package hospitalsystem.UI;

import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class AppointmentMenu extends Submenu{
    AppointmentMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void printMenu() {
        System.out.println("1. Add new appointment");
        System.out.println("2. Back"); //TODO: mozna spotit na volani s PetientMenu
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 2)){
            case 1:
                addAppointment(scanner);
        }
    }

    private void addAppointment(Scanner scanner){
        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }
}
