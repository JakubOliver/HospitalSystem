package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.EditPersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Node;

/**
 * Page for retrieving and editing a doctor.
 */
final class EditDoctorPage extends EditPersonnelPage<Doctor> {
    private DoctorForm form;

    EditDoctorPage(Navigator navigator, Hospital hospital) {
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
    protected DataPacket<Doctor> getPersonnel(int id) {
        return hospital.getDoctor(id);
    }

    @Override
    protected void setFormPersonnel(Doctor doctor) {
        form.setDoctor(doctor);
    }

    @Override
    protected GeneralPacket updatePersonnel(int id) {
        DoctorData data = form.getDoctorData();
        return hospital.updateDoctor(new Doctor(
                new Person(id, data.person()),
                data.details()
        ));
    }
}
