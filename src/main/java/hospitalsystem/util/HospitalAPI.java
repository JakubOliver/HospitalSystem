package hospitalsystem.util;

import hospitalsystem.Hospital;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;

import java.util.List;

/**
 * API for hospital system, creates extra layer between menu and the system.
 */
public class HospitalAPI {
    private final Hospital hospital;

    /**
     * Creates API for the provided hospital.
     *
     * @param hospital Hospital for which the API will be created.
     */
    public HospitalAPI(Hospital hospital) {
        this.hospital = hospital;
    }

    /**
     * Calls for creating of new patient in hospital system.
     *
     * @param patientData Patient data that will be used for patient creation.
     */
    public GeneralPacket addPatient(PatientData patientData) {
        return hospital.addPatient(patientData);
    }

    /**
     * Returns information about patient based on provided id.
     *
     * @param id Id that identifies patient.
     * @return String containing info about patient with provided id.
     */
    public GeneralPacket findPatient(int id){
        return hospital.getPatientInfo(id);
    }

    public GeneralPacket updatePatient(Patient patient) {
        return hospital.updatePatientInfo(patient);
    }

    public GeneralPacket deletePatient(int id) {
        return hospital.deletePatient(id);
    }

    /**
     * Returns list information about every patient in hospital system.
     * @return List of information about every patient in hospital system.
     */
    public List<String> findAllPatients(){
        return hospital.findAllPatient();
    }

    /**
     * Calls for creation of new doctor in hospital system.
     *
     * @param doctorData Doctor data that will be used for doctor creation.
     */
    public void addDoctor(DoctorData doctorData) {
        hospital.addDoctor(doctorData);
    }

    public GeneralPacket findDoctor(int id){
        return hospital.getDoctor(id);
    }

    public GeneralPacket updateDoctor(Doctor doctor){
        return hospital.updateDoctor(doctor);
    }

    public GeneralPacket deleteDoctor(int id) {
        return hospital.deleteDoctor(id);
    }

    public GeneralPacket  findAllDoctors(){
        return hospital.findAllDoctors();
    }

    /**
     * Calls for creation of new appointment in hospital system.
     *
     * @param appointmentData Calendar Entry data that will be used for creation of new appointment.
     */
    public void addAppointment(AppointmentData appointmentData) {
        hospital.addAppointment(appointmentData);
    }

    public void showCalendar(){
        hospital.showCalendar();
    }
}
