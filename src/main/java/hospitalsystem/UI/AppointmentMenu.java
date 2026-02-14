package hospitalsystem.UI;

import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.TextPacket;
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

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                patientsId,
                doctorsId,
                startTime,
                endTime
        ));


        processPacketStatus(scanner, packet);
    }

    public void editAppointment(){
        int id = getInteger(scanner, "Appointment ID: ");
    }

    //TODO: calendar per deparmtne
    //TODO: calendar per week
    //TODO: showCalender from today

    public void showCalendar(){
        GeneralPacket packet = api.showCalendar();

        if (!processPacketStatusInSilence(scanner, packet)) return;

        if (!(packet instanceof TextPacket textPacket)){
            printAndWait(scanner, GeneralPacket.Msg.invalidPacket);
            return;
        }

        printAndWait(scanner, textPacket.text); //TODO: vymyslet trošku lepší způsub mozna aby i kalendář vypisovat, poněvadž mi přijde zvlášní, že se to ukládá to jednoho stringu, ale zas to v calendar je také
    }
}
