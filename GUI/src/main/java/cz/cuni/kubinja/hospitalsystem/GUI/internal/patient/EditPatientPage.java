package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.EditPersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Node;

/**
 * Page for retrieving and editing a patient.
 */
final class EditPatientPage extends EditPersonnelPage<Patient> {
    private PatientForm form;

    EditPatientPage(Navigator navigator, Hospital hospital) {
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
    protected DataPacket<Patient> getPersonnel(int id) {
        return hospital.getPatient(id);
    }

    @Override
    protected void setFormPersonnel(Patient patient) {
        form.setPatient(patient);
    }

    @Override
    protected GeneralPacket updatePersonnel(int id) {
        PatientData data = form.getPatientData();
        return hospital.updatePatient(new Patient(
                new Person(id, data.person()),
                data.details()
        ));
    }
}
