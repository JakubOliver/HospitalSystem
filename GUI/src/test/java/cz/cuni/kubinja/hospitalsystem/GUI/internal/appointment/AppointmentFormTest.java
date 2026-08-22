package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.label;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class AppointmentFormTest {
    private AppointmentForm form;
    private Patient patient;
    private Doctor doctor;

    @Start
    private void start(Stage stage) {
        patient = new Patient(
            1,
            "Pepa",
            "Novak",
            LocalDate.of(1990, 5, 12),
            "Broken leg"
        );
        doctor = new Doctor(
            2,
            "David",
            "Novak",
            LocalDate.of(1985, 3, 20),
            "Cardiology",
            "Cardiology"
        );

        form = new AppointmentForm();
        form.setOptions(new AppointmentOptions(
            List.of(patient),
            List.of(doctor),
            null
        ));

        stage.setScene(new Scene(form, 720, 650));
        stage.show();
    }

    @Test
    void selectedPersonnelAndDefaultTimesProduceValidData(FxRobot robot) {
        assertFalse(form.validProperty().get());
        selectPersonnel(robot);

        AppointmentData data = form.getAppointmentData();
        assertTrue(form.validProperty().get());
        assertEquals(patient.getId(), data.patientsId());
        assertEquals(doctor.getId(), data.doctorsId());
        assertEquals(LocalTime.of(8, 0), data.starTime().toLocalTime());
        assertEquals(LocalTime.of(9, 0), data.endTime().toLocalTime());
    }

    @Test
    void endTimeBeforeStartTimeIsRejected(FxRobot robot) {
        selectPersonnel(robot);

        ComboBox<LocalTime> start = timeChoice(robot, "#appointment-start-time");
        ComboBox<LocalTime> end = timeChoice(robot, "#appointment-end-time");
        robot.interact(() -> {
            start.setValue(LocalTime.of(10, 0));
            end.setValue(LocalTime.of(9, 0));
        });

        assertFalse(form.validProperty().get());
        assertEquals(
            "End time must be after start time.",
            label(robot, "#appointment-time-error").getText()
        );
    }

    @SuppressWarnings("unchecked")
    private void selectPersonnel(FxRobot robot) {
        ComboBox<Patient> patients = robot.lookup("#patient-choice")
            .queryAs(ComboBox.class);
        ComboBox<Doctor> doctors = robot.lookup("#doctor-choice")
            .queryAs(ComboBox.class);

        robot.interact(() -> {
            patients.setValue(patient);
            doctors.setValue(doctor);
        });
    }

    @SuppressWarnings("unchecked")
    private ComboBox<LocalTime> timeChoice(FxRobot robot, String selector) {
        return robot.lookup(selector).queryAs(ComboBox.class);
    }
}
