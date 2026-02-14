package hospitalsystem.UI;

import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

public class ExportMenu extends Submenu{
    public ExportMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Export patients", this::exportPatients);
        addOption("Export doctors", this::exportDoctors);
        addOption("Export appointments", this::exportAppointments);
        addOption("Export all", this::exportAll);
        addOption("Back", this::end);
    }

    public void exportPatients(){

    }

    public void exportDoctors(){

    }

    public void exportAppointments(){

    }

    public void exportAll(){
        //exportPatients();
        //exportDoctors();
        //exportAppointments();

        GeneralPacket packet = api.export();

        System.out.println(packet.resolveStatus());
        waitForEnter(scanner);
    }
}
