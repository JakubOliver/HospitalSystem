package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.packet.GeneralPacket;

import java.util.Scanner;

public class ExportMenu extends Menu{
    public ExportMenu(Hospital api, Scanner scanner) {
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
        GeneralPacket packet = api.exportPatients();

        printAndWait(packet.resolveStatus());
    }

    public void exportDoctors(){
        GeneralPacket packet = api.exportDoctors();

        printAndWait(packet.resolveStatus());
    }

    public void exportAppointments(){
        GeneralPacket packet = api.exportAppointments();

        printAndWait(packet.resolveStatus());
    }

    public void exportAll(){
        GeneralPacket packet = api.export();

        printAndWait(packet.resolveStatus());
    }
}
