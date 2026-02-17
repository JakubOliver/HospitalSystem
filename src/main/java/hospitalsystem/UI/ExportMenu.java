package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.packet.GeneralPacket;

import java.util.Scanner;

/**
 * Menu page containing options connected with exporting of hospital data.
 */
public class ExportMenu extends Menu{
    /**
     * Creates exports menu page.
     *
     * @param api Hospital giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
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

    /**
     * Exports patients data into file.
     */
    public void exportPatients(){
        GeneralPacket packet = api.exportPatients();

        printAndWait(packet.resolveStatus());
    }

    /**
     * Exports doctors data into file.
     */
    public void exportDoctors(){
        GeneralPacket packet = api.exportDoctors();

        printAndWait(packet.resolveStatus());
    }

    /**
     * Exports appointment data into file.
     */
    public void exportAppointments(){
        GeneralPacket packet = api.exportAppointments();

        printAndWait(packet.resolveStatus());
    }

    /**
     * Exports all hospital data into file.
     */
    public void exportAll(){
        GeneralPacket packet = api.export();

        printAndWait(packet.resolveStatus());
    }
}
