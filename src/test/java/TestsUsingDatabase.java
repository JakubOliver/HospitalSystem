import cz.cuni.kubinja.hospitalsystem.Hospital;
import cz.cuni.kubinja.hospitalsystem.calendar.util.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.database.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.personnel.util.*;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestsUsingDatabase extends Tests{
    Hospital api;

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
