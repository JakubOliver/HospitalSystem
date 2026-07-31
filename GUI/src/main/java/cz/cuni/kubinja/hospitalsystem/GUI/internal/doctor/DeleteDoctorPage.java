package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.DeletePersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;

/**
 * Page for retrieving and deleting a doctor.
 */
final class DeleteDoctorPage extends DeletePersonnelPage<Doctor> {
    DeleteDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital, "Doctor");
    }

    @Override
    protected DataPacket<Doctor> getPersonnel(int id) {
        return hospital.getDoctor(id);
    }

    @Override
    protected GeneralPacket deletePersonnel(int id) {
        return hospital.deleteDoctor(id);
    }

    @Override
    protected Detail[] additionalDetails(Doctor doctor) {
        return new Detail[]{
            new Detail("Specialization", doctor.getSpecialization()),
            new Detail("Department", doctor.getDepartment())
        };
    }
}
