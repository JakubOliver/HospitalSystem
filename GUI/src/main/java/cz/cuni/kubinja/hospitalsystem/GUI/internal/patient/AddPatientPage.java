package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.AddPersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Node;

/**
 * Page for creating a patient.
 */
final class AddPatientPage extends AddPersonnelPage<Patient> {
    private PatientForm form;

    AddPatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital, "Patient");
    }

    @Override
    protected Node createPersonnelForm() {
        form = new PatientForm();
        return form;
    }

    @Override
    protected BooleanExpression formValidProperty() {
        return form.validProperty();
    }

    @Override
    protected DataPacket<Patient> addPersonnel() {
        return hospital.addPatient(form.getPatientData());
    }
}
