package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import java.util.List;

//NOTE: In bigger "production" project the filtering would probably be done
// by iterating the database not loading all patients/doctors into some object
// and then doing filtering by the list.
//
// So I think that for the purposes of school project this approach with
// acknowledgment of these issues is enough.
//
// (Even for smaller hospitals is this approach ok, because they probably have
// below 100 of doctors and max few tens of thousand of patients.

/**
 * Patient and doctor choices required by an appointment form.
 */
record AppointmentOptions(
        List<Patient> patients,
        List<Doctor> doctors,
        GeneralPacket error
) {
    static AppointmentOptions load(Hospital hospital) {
        DataPacket<List<Patient>> patients = hospital.allPatients();

        if (!patients.successful) {
            return new AppointmentOptions(List.of(), List.of(), patients);
        }

        DataPacket<List<Doctor>> doctors = hospital.allDoctors();

        if (!doctors.successful) {
            return new AppointmentOptions(List.of(), List.of(), doctors);
        }

        return new AppointmentOptions(patients.data, doctors.data, null);
    }

    boolean successful() {
        return error == null;
    }
}
