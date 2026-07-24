import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.*;
import cz.cuni.kubinja.hospitalsystem.testing.Tests;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestsUsingDatabase extends Tests{
    public Hospital api;

    ///////////////
    /// SAMPLES ///
    ///////////////

    static PersonData samplePersonData = new PersonData("Pepa", "Novak",  LocalDate.of(2001,1,1));

    static PatientsDetails samplePatientDetails = new PatientsDetails("Broken leg");
    static PatientData samplePatientData = new PatientData(samplePersonData, samplePatientDetails);

    static DoctorDetails sampleDoctorDetails = new DoctorDetails("Surgeon", "Traumatology");
    static DoctorData sampleDoctorData = new DoctorData(samplePersonData, sampleDoctorDetails);

    static AppointmentData sampleAppointmentData = new AppointmentData(
            1,
            2,
            LocalDateTime.of(2001, 1, 1, 10, 0),
            LocalDateTime.of(2001, 1, 1, 11, 0)
    );

    public TestsUsingDatabase() throws DatabaseException {
        api = new Hospital(testDB);
    }

    @BeforeEach
    public void cleanup() {
        api.delete();
    }
}
