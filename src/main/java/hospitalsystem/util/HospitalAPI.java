package hospitalsystem.util;

import hospitalsystem.Hospital;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;

public class HospitalAPI {
    Hospital hospital;

    public HospitalAPI(Hospital hospital) {
        this.hospital = hospital;
    }

    public void addDoctor(DoctorData doctorData) {
        hospital.addDoctor(doctorData);
    }

    public void addPatient(PatientData patientData) {
        hospital.addPatient(patientData);
    }

    public void findAllPatients(){
        hospital.findAllPatient();
    }
}
