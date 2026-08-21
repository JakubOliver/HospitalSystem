package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.testing.GuiTestBase;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.nio.file.Path;
import java.util.List;

import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.button;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.setDate;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.setText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class PatientWorkflowTest extends GuiTestBase {
    @TempDir
    private Path temporaryDirectory;

    @Start
    private void start(Stage stage) throws Exception {
        initialize(stage, temporaryDirectory);
        show(new PatientMenu(navigator, hospital));
    }

    @Test
    void addPatientPersistsFormData(FxRobot robot) {
        robot.clickOn("Add new patient");
        assertTrue(button(robot, "#save-personnel").isDisabled());

        setText(robot, "#first-name", "Pepa");
        setText(robot, "#last-name", "Novak");
        setDate(robot, "#date-of-birth", SAMPLE_BIRTH_DATE);
        setText(robot, "#anamnesis", "Broken leg");

        assertFalse(button(robot, "#save-personnel").isDisabled());
        robot.clickOn("#save-personnel");
        robot.clickOn("OK");

        DataPacket<Patient> packet = hospital.getPatient(1);
        assertTrue(packet.successful, packet::resolveStatus);
        assertEquals("Pepa", packet.data.getFirstName());
        assertEquals("Broken leg", packet.data.getAnamnesis());
    }

    @Test
    void findPatientDisplaysStoredDetails(FxRobot robot) {
        Patient patient = addSamplePatient();
        robot.clickOn("Find patient by ID");

        setText(robot, "#entity-id", Integer.toString(patient.getId()));
        robot.clickOn("#entity-id-action");

        assertTrue(robot.lookup("Pepa").tryQuery().isPresent());
        assertTrue(robot.lookup("Novak").tryQuery().isPresent());
        assertTrue(robot.lookup("Broken leg").tryQuery().isPresent());
    }

    @Test
    void allPatientsTableContainsStoredPatients(FxRobot robot) {
        Patient patient = addSamplePatient();
        robot.clickOn("Show all patients");

        TableView<?> table = robot.lookup("#personnel-table")
                .queryAs(TableView.class);
        List<String> columnNames = table.getColumns().stream()
                .map(column -> column.getText())
                .toList();

        assertEquals(List.of(
                "ID",
                "First name",
                "Last name",
                "Date of birth",
                "Anamnesis"
        ), columnNames);
        assertEquals(1, table.getItems().size());
        Patient displayedPatient = (Patient) table.getItems().getFirst();
        assertEquals(patient.getId(), displayedPatient.getId());
        assertEquals(patient.getFirstName(), displayedPatient.getFirstName());
        assertEquals(patient.getAnamnesis(), displayedPatient.getAnamnesis());
    }

    @Test
    void deleteCanBeCancelledAndThenConfirmed(FxRobot robot) {
        Patient patient = addSamplePatient();
        robot.clickOn("Delete existing patient");
        setText(robot, "#entity-id", Integer.toString(patient.getId()));
        robot.clickOn("#entity-id-action");

        robot.clickOn("#delete-personnel");
        robot.clickOn("Cancel");
        assertTrue(hospital.getPatient(patient.getId()).successful);

        robot.clickOn("#delete-personnel");
        robot.clickOn("OK");
        robot.clickOn("OK");
        assertFalse(hospital.getPatient(patient.getId()).successful);
    }
}
