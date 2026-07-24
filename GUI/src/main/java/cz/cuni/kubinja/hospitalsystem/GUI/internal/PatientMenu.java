package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.menu.PersonnelMenu;
import javafx.scene.layout.GridPane;

/**
 * Menu containing the patient-related actions.
 */
public class PatientMenu extends MenuPage implements PersonnelMenu {
    private final Hospital hospital;

    public PatientMenu(Navigator navigator, Hospital hospital) {
        super(navigator);
        this.hospital = hospital;
    }

    @Override
    public String getTitle() {
        return "Patients";
    }

    @Override
    protected void addOptions(GridPane options) {
        addOption(options, "Add new patient", this::add);
        addOption(options, "Edit existing patient", this::edit);
        addOption(options, "Delete existing patient", this::delete);
        addOption(options, "Find patient by ID", this::findById);
        addOption(options, "Show all patients", this::all);
    }

    @Override
    public void add() {
        navigator.navigate(new AddPatientPage(navigator, hospital));
    }

    @Override
    public void edit() {
        navigator.navigate(new EditPatientPage(navigator, hospital));
    }

    @Override
    public void delete() {
        navigator.navigate(new DeletePatientPage(navigator, hospital));
    }

    @Override
    public void findById() {
        navigator.navigate(new FindPatientPage(navigator, hospital));
    }

    @Override
    public void all() {
        navigator.navigate(new AllPatientsPage(navigator, hospital));
    }
}
