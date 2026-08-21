package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
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
class DoctorFormTest {
    private DoctorForm form;

    @Start
    private void start(Stage stage) {
        form = new DoctorForm();
        stage.setScene(new Scene(form, 700, 540));
        stage.show();
    }

    @Test
    void emptyDoctorDetailsAreInvalid(FxRobot robot) {
        assertFalse(form.validProperty().get());
        assertEquals(
            "This field is required.",
            label(robot, "#specialization-error").getText()
        );
        assertEquals(
            "This field is required.",
            label(robot, "#department-error").getText()
        );
    }

    @Test
    void validValuesProduceTrimmedDoctorData(FxRobot robot) {
        setText(robot, "#first-name", "  David  ");
        setText(robot, "#last-name", "  Smith  ");
        setDate(robot, "#date-of-birth", LocalDate.of(1985, 3, 20));
        setText(robot, "#specialization", "  Cardiology  ");
        setText(robot, "#department", "  Cardiology  ");

        DoctorData data = form.getDoctorData();
        assertTrue(form.validProperty().get());
        assertEquals("David", data.person().firstName());
        assertEquals("Smith", data.person().lastName());
        assertEquals("Cardiology", data.details().specialization());
        assertEquals("Cardiology", data.details().department());
    }

    @Test
    void setDoctorPopulatesAValidForm(FxRobot robot) {
        Doctor doctor = new Doctor(
            23,
            "John",
            "Green",
            LocalDate.of(1979, 11, 4),
            "Surgeon",
            "Cardiology"
        );

        robot.interact(() -> form.setDoctor(doctor));

        DoctorData data = form.getDoctorData();
        assertTrue(form.validProperty().get());
        assertEquals("John", data.person().firstName());
        assertEquals("Surgeon", data.details().specialization());
        assertEquals("Cardiology", data.details().department());
    }
}
