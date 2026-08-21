package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;

import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.label;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.setDate;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.setText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class PatientFormTest {
    private PatientForm form;

    @Start
    private void start(Stage stage) {
        form = new PatientForm();
        stage.setScene(new Scene(form, 700, 500));
        stage.show();
    }

    @Test
    void emptyFormIsInvalid(FxRobot robot) {
        assertFalse(form.validProperty().get());
        assertEquals(
            "This field is required.",
            label(robot, "#first-name-error").getText()
        );
        assertEquals(
            "Select a date of birth.",
            label(robot, "#date-of-birth-error").getText()
        );
        assertEquals(
            "This field is required.",
            label(robot, "#anamnesis-error").getText()
        );
    }

    @Test
    void validValuesProduceTrimmedPatientData(FxRobot robot) {
        fillValidForm(robot);

        PatientData data = form.getPatientData();
        assertTrue(form.validProperty().get());
        assertEquals("Pepa", data.person().firstName());
        assertEquals("Novak", data.person().lastName());
        assertEquals(LocalDate.of(1990, 5, 12), data.person().dateOfBirth());
        assertEquals("Broken leg", data.details().anamnesis());
    }

    @Test
    void futureBirthDateIsRejected(FxRobot robot) {
        fillValidForm(robot);
        setDate(robot, "#date-of-birth", LocalDate.now().plusDays(1));

        assertFalse(form.validProperty().get());
        assertEquals(
            "Date must be after 1900-01-01 and not in the future.",
            label(robot, "#date-of-birth-error").getText()
        );
    }

    @Test
    void setPatientPopulatesAValidForm(FxRobot robot) {
        Patient patient = new Patient(
            17,
            "Eva",
            "Brown",
            LocalDate.of(1988, 7, 9),
            "Allergy"
        );

        robot.interact(() -> form.setPatient(patient));

        PatientData data = form.getPatientData();
        assertTrue(form.validProperty().get());
        assertEquals("Eva", data.person().firstName());
        assertEquals("Brown", data.person().lastName());
        assertEquals("Allergy", data.details().anamnesis());
    }

    private void fillValidForm(FxRobot robot) {
        setText(robot, "#first-name", "  Pepa  ");
        setText(robot, "#last-name", "  Novak  ");
        setDate(robot, "#date-of-birth", LocalDate.of(1990, 5, 12));
        setText(robot, "#anamnesis", "  Broken leg  ");
    }
}
