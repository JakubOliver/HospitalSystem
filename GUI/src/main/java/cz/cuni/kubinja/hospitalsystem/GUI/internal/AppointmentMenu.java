package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.layout.GridPane;

/**
 * Menu containing appointment and calendar actions.
 */
public class AppointmentMenu extends MenuPage {
    public AppointmentMenu(Navigator navigator) {
        super(navigator);
    }

    @Override
    public String getTitle() {
        return "Calendar";
    }

    @Override
    protected void addOptions(GridPane options) {
        addDisabledOption(options, "Add new appointment");
        addDisabledOption(options, "Edit appointment");
        addDisabledOption(options, "Delete appointment");
        addDisabledOption(options, "Show all appointments for patient");
        addDisabledOption(options, "Show all appointments for doctor");
        addDisabledOption(options, "Show calendar for department");
        addDisabledOption(options, "Show calendar");
    }
}
