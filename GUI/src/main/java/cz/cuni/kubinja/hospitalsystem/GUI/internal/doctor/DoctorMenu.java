package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.MenuPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.menu.PersonnelMenu;
import javafx.scene.layout.GridPane;

/**
 * Menu containing the doctor-related actions.
 */
public class DoctorMenu extends MenuPage implements PersonnelMenu {
    private final Hospital hospital;

    public DoctorMenu(Navigator navigator, Hospital hospital) {
        super(navigator);
        this.hospital = hospital;
    }

    @Override
    public String getTitle() {
        return "Doctors";
    }

    @Override
    protected void addOptions(GridPane options) {
        addOption(options, "Add new doctor", this::add);
        addOption(options, "Edit existing doctor", this::edit);
        addOption(options, "Delete existing doctor", this::delete);
        addOption(options, "Find doctor by ID", this::findById);
        addOption(options, "Show all doctors", this::all);
    }

    @Override
    public void add() {
        navigator.navigate(new AddDoctorPage(navigator, hospital));
    }

    @Override
    public void edit() {
        navigator.navigate(new EditDoctorPage(navigator, hospital));
    }

    @Override
    public void delete() {
        navigator.navigate(new DeleteDoctorPage(navigator, hospital));
    }

    @Override
    public void findById() {
        navigator.navigate(new FindDoctorPage(navigator, hospital));
    }

    @Override
    public void all() {
        navigator.navigate(new AllDoctorsPage(navigator, hospital));
    }
}
