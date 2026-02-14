package hospitalsystem.UI;

import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Menu page containing options connected with appointments.
 */
public class AppointmentMenu extends Submenu{

    /**
     * Creates appointment menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    AppointmentMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Add new appointment", this::addAppointment);
        addOption("Edit appointment", this::editAppointment);
        addOption("Delete appointment", () -> {}); //TODO
        addOption("List appointments", this::showCalendar);
        addOption("Back", this::end);
    }

    /**
     * Processes input data and calls for creating of new appointment in hospital system.
     */
    public void addAppointment(){
        //TODO: volba zda si vybrat (id nebo jmeno) nebo vytvořit noveho

        int patientsId;
        if (createNew(scanner, "patient")){
            //TODO: dodelat pres packety
            patientsId = getInteger(scanner, "Patient's ID: ");
        } else {
            patientsId = getInteger(scanner, "Patient's ID: ");
        }

        int doctorsId;
        if (createNew(scanner, "doctor")){
            //TODO: dodelat pres packety
            doctorsId = getInteger(scanner, "Doctor's ID: ");
        } else {
            doctorsId = getInteger(scanner, "Doctor's ID: ");
        }
        LocalDateTime startTime = getDateTime(scanner, "Start Time: ");
        LocalDateTime endTime = getDateTime(scanner, "End Time: ");

        api.addAppointment(new AppointmentData(
                patientsId,
                doctorsId,
                startTime,
                endTime
        ));

        waitForEnter(scanner);
    }

    public void editAppointment(){
        int id = getInteger(scanner, "Appointment ID: ");
    }

    public void showCalendar(){
        api.showCalendar();

        waitForEnter(scanner);
    }
}
