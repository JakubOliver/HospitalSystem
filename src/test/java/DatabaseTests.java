import hospitalsystem.Hospital;
import hospitalsystem.UI.PatientMenu;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.*;
import hospitalsystem.util.HospitalAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTests extends Tests{
    HospitalAPI api;

    ///////////////
    /// SAMPLES ///
    ///////////////

    static PersonData samplePersonData = new PersonData("Pepa", "Novak",  LocalDate.of(2001,1,1));

    static PatientsDetails samplePatientDetails = new PatientsDetails("Broken leg");
    static PatientData samplePatientData = new PatientData(samplePersonData, samplePatientDetails);

    static DoctorDetails sampleDoctorDetails = new DoctorDetails("Surgeon");
    static DoctorData sampleDoctorData = new DoctorData(samplePersonData, sampleDoctorDetails);

    @BeforeEach
    public void setup() throws SQLException {
        api = new HospitalAPI(new Hospital("jdbc:sqlite:memory:"));
    }

    @Test
    void addPatientCMD() {
        Scanner patientsScanner = getPrebuildInput("1 \n Pepa \n Novak \n 2001-01-01 \n Broken leg \n enter");

        PatientMenu patientMenu = new PatientMenu(api, patientsScanner);
    }

    @Test
    void addPatientApi() {
        api.addPatient(samplePatientData);

        api.findPatient(1);

        assertEquals(new Patient(1, "Pepa", "Novak", 1, 1, 2001, "Broken leg").toString(), api.findPatient(1));
    }

    @Test
    void addDoctorApi(){
        api.addDoctor(sampleDoctorData);
    }

    @Test
    void addAppointment(){
        api.addPatient(samplePatientData);
        api.addDoctor(sampleDoctorData);

        api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.of(2001, 1, 1, 1, 1),
                LocalDateTime.of(2001, 1, 1, 1, 2)
        ));
    }
}
