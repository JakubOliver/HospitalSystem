package hospitalsystem.util;

import hospitalsystem.Hospital;
import hospitalsystem.calendar.util.CalendarEntryData;
import hospitalsystem.database.Database;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;

public class HospitalAPI {
    Hospital hospital;

    public HospitalAPI(Hospital hospital) {
        this.hospital = hospital;
    }

    public void addPatient(PatientData patientData) {
        hospital.addPatient(patientData);
    }

    public String findPatient(int id){
        return hospital.getPatientInfo(id);
    }

    public void findAllPatients(){
        hospital.findAllPatient();
    }

    public void addDoctor(DoctorData doctorData) {
        hospital.addDoctor(doctorData);
    }

    public void addAppointment(CalendarEntryData calendarEntryData) {
        hospital.addAppointment(calendarEntryData);
    }
}
