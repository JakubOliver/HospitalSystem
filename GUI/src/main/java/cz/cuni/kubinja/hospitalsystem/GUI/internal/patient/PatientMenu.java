package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.PersonnelMenuPage;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;

/**
 * Menu containing the patient-related actions.
 */
public class PatientMenu extends PersonnelMenuPage {
    private final Hospital hospital;

    public PatientMenu(Navigator navigator, Hospital hospital) {
        super(navigator, "Patient", "Patients");
        this.hospital = hospital;
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
