package cz.cuni.kubinja.hospitalsystem.GUI.testing;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Page;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientsDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GuiTestBase {
    protected static final LocalDate SAMPLE_BIRTH_DATE =
            LocalDate.of(1990, 5, 12);

    protected Hospital hospital;
    protected Navigator navigator;
    protected Stage stage;

    protected final void initialize(Stage stage, Path temporaryDirectory) throws DatabaseException {
        this.stage = stage;
        hospital = new Hospital(
                "jdbc:sqlite:"
                        + temporaryDirectory.resolve("hospital-test.db").toAbsolutePath()
        );
        navigator = new Navigator(stage);
    }

    protected final void show(Page page) {
        navigator.start(page);
    }

    protected final Patient addSamplePatient() {
        DataPacket<Patient> packet = hospital.addPatient(new PatientData(
                new PersonData("Pepa", "Novak", SAMPLE_BIRTH_DATE),
                new PatientsDetails("Broken leg")
        ));

        assertTrue(packet.successful, packet::resolveStatus);
        return packet.data;
    }

    protected final Doctor addSampleDoctor() {
        DataPacket<Doctor> packet = hospital.addDoctor(new DoctorData(
                new PersonData("Pepa", "Novak", LocalDate.of(1985, 3, 20)),
                new DoctorDetails("Cardiology", "Cardiology")
        ));

        assertTrue(packet.successful, packet::resolveStatus);
        return packet.data;
    }
}
