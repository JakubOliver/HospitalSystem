package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.FindPersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;

/**
 * Page for finding and displaying a doctor by ID.
 */
final class FindDoctorPage extends FindPersonnelPage<Doctor> {
    FindDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital, "Doctor");
    }

    @Override
    protected DataPacket<Doctor> getPersonnel(int id) {
        return hospital.getDoctor(id);
    }

    @Override
    protected Detail[] additionalDetails(Doctor doctor) {
        return new Detail[]{
                new Detail("Specialization", doctor.getSpecialization()),
                new Detail("Department", doctor.getDepartment())
        };
    }
}
