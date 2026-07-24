package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.layout.GridPane;

/**
 * Menu containing the doctor-related actions.
 */
public class DoctorMenu extends MenuPage {
    public DoctorMenu(Navigator navigator) {
        super(navigator);
    }

    @Override
    public String getTitle() {
        return "Doctors";
    }

    @Override
    protected void addOptions(GridPane options) {
        addDisabledOption(options, "Add new doctor");
        addDisabledOption(options, "Edit existing doctor");
        addDisabledOption(options, "Delete existing doctor");
        addDisabledOption(options, "Find doctor by ID");
        addDisabledOption(options, "Show all doctors");
    }
}
