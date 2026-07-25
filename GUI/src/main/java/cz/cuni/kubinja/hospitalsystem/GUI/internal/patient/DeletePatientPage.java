package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.DeletePersonnelPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;

/**
 * Page for retrieving and deleting a patient.
 */
final class DeletePatientPage extends DeletePersonnelPage<Patient> {
    DeletePatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital, "Patient");
    }

    @Override
    protected DataPacket<Patient> getPersonnel(int id) {
        return hospital.getPatient(id);
    }

    @Override
    protected GeneralPacket deletePersonnel(int id) {
        return hospital.deletePatient(id);
    }

    @Override
    protected Detail[] additionalDetails(Patient patient) {
        return new Detail[]{
                new Detail("Anamnesis", patient.getAnamnesis())
        };
    }
}
