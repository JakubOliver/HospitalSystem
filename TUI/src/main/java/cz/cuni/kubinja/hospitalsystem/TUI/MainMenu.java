package cz.cuni.kubinja.hospitalsystem.TUI;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.TUI.internal.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Menu page containing crossroad to other submenus.
 */
public class MainMenu extends Menu {
    /**
     * Creates main menu page and runs the UI cycle.
     *
     * @param api Hospital providing the menu options how to interact with hospital system.
     */
    public MainMenu(Hospital api) {
        super(api, new Scanner(System.in, StandardCharsets.UTF_8));
    }

    /**
     * Creates main menu page and runs the UI cycle.
     *
     * @param api Hospital providing the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public MainMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Patients", () -> new PatientMenu(api, scanner));
        addOption("Doctors",  () -> new DoctorMenu(api, scanner));
        addOption("Calendar",  () -> new AppointmentMenu(api, scanner));
        addOption("Export", () -> new ExportMenu(api, scanner));
        addOption("Statistics", () -> new ReportsMenu(api, scanner));
        addOption("End", this::end);
    }

    @Override
    public void printMenu(){
        super.printMenu();
    }
}
