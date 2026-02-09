package hospitalsystem.UI;

import hospitalsystem.calendar.util.CalendarEntryData;
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
                addAppointment(scanner); break;
        }
    }

    private void addAppointment(Scanner scanner){
        //TODO: volba zda si vybrat (id nebo jmeno) nebo vytvořit noveho

        int patientsId = getInteger(scanner, "Patient's ID: ");
        int doctorsId = getInteger(scanner, "Doctor's ID: ");
        LocalDateTime startTime = getDateTime(scanner, "Start Time: ");
        LocalDateTime endTime = getDateTime(scanner, "End Time: ");

        api.addAppointment(new CalendarEntryData(
                patientsId,
                doctorsId,
                startTime,
                endTime
        ));

        waitForEnter(scanner);
    }
}
