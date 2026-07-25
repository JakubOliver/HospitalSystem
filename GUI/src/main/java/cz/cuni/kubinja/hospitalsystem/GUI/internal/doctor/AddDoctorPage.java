package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.AddPersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Node;

/**
 * Page for creating a doctor.
 */
final class AddDoctorPage extends AddPersonnelPage<Doctor> {
    private DoctorForm form;

    AddDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital, "Doctor");
    }

    @Override
    protected Node createPersonnelForm() {
        form = new DoctorForm();
        return form;
    }

    @Override
    protected BooleanExpression formValidProperty() {
        return form.validProperty();
    }

    @Override
    protected DataPacket<Doctor> addPersonnel() {
        return hospital.addDoctor(form.getDoctorData());
    }
}
