package hospitalsystem.UI;

import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

public class ExportMenu extends Submenu{
    public ExportMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void printMenu() {
        System.out.println("1. Export patients");
        System.out.println("2. Export doctors");
        System.out.println("3. Export appointments");
        System.out.println("4. Export all");
        System.out.println("5. Back");
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 5)){
            case 1:
                exportPatients(); break;
            case 2:
                exportDoctors(); break;
            case 3:
                exportAppointments(); break;
            case 4:
                exportAll(); break;
            case 5:
                end(); break;
        }
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
