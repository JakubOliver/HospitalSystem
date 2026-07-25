package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.PersonnelMenuPage;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;

/**
 * Menu containing the doctor-related actions.
 */
public class DoctorMenu extends PersonnelMenuPage {
    private final Hospital hospital;

    public DoctorMenu(Navigator navigator, Hospital hospital) {
        super(navigator, "Doctor", "Doctors");
        this.hospital = hospital;
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
