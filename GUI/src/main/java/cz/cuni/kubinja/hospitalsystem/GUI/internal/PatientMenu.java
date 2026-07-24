package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.layout.GridPane;

/**
 * Menu containing the patient-related actions.
 */
public class PatientMenu extends MenuPage {
    public PatientMenu(Navigator navigator) {
        super(navigator);
    }

    @Override
    public String getTitle() {
        return "Patients";
    }

    @Override
    protected void addOptions(GridPane options) {
        addDisabledOption(options, "Add new patient");
        addDisabledOption(options, "Edit existing patient");
        addDisabledOption(options, "Delete existing patient");
        addDisabledOption(options, "Find patient by ID");
        addDisabledOption(options, "Show all patients");
    }
}
