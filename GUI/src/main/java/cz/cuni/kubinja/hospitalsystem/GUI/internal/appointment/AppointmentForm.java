package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Appointment;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.menu.InputValidator;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared fields and validation for creating and editing appointments.
 */
final class AppointmentForm extends GridPane {
    private static final double HORIZONTAL_GAP = 16;
    private static final double VERTICAL_GAP = 8;
    private static final double FORM_PADDING = 10;
    private static final double FORM_MAX_WIDTH = 700;

    private final PersonnelSelector<Patient> patient = new PersonnelSelector<>("Patient");
    private final PersonnelSelector<Doctor> doctor = new PersonnelSelector<>("Doctor");

    private final DatePicker date = new DatePicker(LocalDate.now());

    private final ComboBox<LocalTime> startTime = new ComboBox<>();
    private final ComboBox<LocalTime> endTime = new ComboBox<>();

    private final Label personnelError = errorLabel();
    private final Label timeError = errorLabel();

    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    AppointmentForm() {
        setHgap(HORIZONTAL_GAP);
        setVgap(VERTICAL_GAP);
        setPadding(new Insets(FORM_PADDING));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(FORM_MAX_WIDTH);

        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);
        getColumnConstraints().add(fields);

        startTime.getItems().setAll(timeSlots(
            LocalTime.of(Calendar.minStartingTime, 0),
            LocalTime.of(Calendar.maxEndingTime - 1, 0)
        ));
        endTime.getItems().setAll(timeSlots(
            LocalTime.of(Calendar.minStartingTime + 1, 0),
            LocalTime.of(Calendar.maxEndingTime, 0)
        ));

        startTime.setValue(LocalTime.of(Calendar.minStartingTime, 0));
        endTime.setValue(LocalTime.of(Calendar.minStartingTime + 1, 0));

        date.setId("appointment-date");
        startTime.setId("appointment-start-time");
        endTime.setId("appointment-end-time");
        personnelError.setId("appointment-personnel-error");
        timeError.setId("appointment-time-error");

        startTime.setMaxWidth(Double.MAX_VALUE);
        endTime.setMaxWidth(Double.MAX_VALUE);

        date.setEditable(false);
        date.setMaxWidth(Double.MAX_VALUE);
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            setDisable(
                empty
                || !InputValidator.isValidAppointmentDateTime(
                    item.atStartOfDay()
                )
            );
            }
        });

        // TODO: create some automatic/dynamic system for assigning row indexes
        //  but add is super gridpane function so would need to create some wrapper etc.
        add(patient, 0, 0);
        add(doctor, 0, 1);
        add(personnelError, 0, 2);
        add(new Label("Date:"), 0, 3);
        add(date, 0, 4);
        add(new Label("Start time:"), 0, 5);
        add(startTime, 0, 6);
        add(new Label("End time:"), 0, 7);
        add(endTime, 0, 8);
        add(timeError, 0, 9);

        patient.selectedProperty().addListener(
            (observable, oldValue, newValue) -> validate()
        );
        doctor.selectedProperty().addListener(
            (observable, oldValue, newValue) -> validate()
        );
        date.valueProperty().addListener(
            (observable, oldValue, newValue) -> validate()
        );
        startTime.valueProperty().addListener(
            (observable, oldValue, newValue) -> validate()
        );
        endTime.valueProperty().addListener(
            (observable, oldValue, newValue) -> validate()
        );

        validate();
    }

    void setOptions(AppointmentOptions options) {
        patient.setPeople(options.patients());
        doctor.setPeople(options.doctors());

        validate();
    }

    void setAppointment(Appointment appointment) {
        patient.selectById(appointment.patientId);
        doctor.selectById(appointment.doctorId);

        date.setValue(appointment.startTime.toLocalDate());
        startTime.setValue(appointment.startTime.toLocalTime());
        endTime.setValue(appointment.endTime.toLocalTime());

        validate();
    }

    AppointmentData getAppointmentData() {
        return new AppointmentData(
            patient.getSelected().getId(),
            doctor.getSelected().getId(),
            LocalDateTime.of(date.getValue(), startTime.getValue()),
            LocalDateTime.of(date.getValue(), endTime.getValue())
        );
    }

    Patient getPatient() {
        return patient.getSelected();
    }

    Doctor getDoctor() {
        return doctor.getSelected();
    }

    BooleanExpression validProperty() {
        return valid;
    }

    private void validate() {
        String personnelMessage = patient.getSelected() == null
            ? "Select a patient."
            : doctor.getSelected() == null
            ? "Select a doctor."
            : "";
        String timeMessage = validateTime();

        personnelError.setText(personnelMessage);
        timeError.setText(timeMessage);
        valid.set(personnelMessage.isEmpty() && timeMessage.isEmpty());
    }

    private String validateTime() {
        if (
            date.getValue() == null
            || startTime.getValue() == null
            || endTime.getValue() == null
        ) {
            return "Select the appointment date and times.";
        }

        LocalDateTime start = LocalDateTime.of(date.getValue(), startTime.getValue());
        LocalDateTime end = LocalDateTime.of(date.getValue(), endTime.getValue());
        if (
            !InputValidator.isValidAppointmentDateTime(start)
            || !InputValidator.isValidAppointmentDateTime(end)
        ) {
            return "Date must be between 2000-01-01 and 3000-01-01.";
        }

        if (!end.isAfter(start)) {
            return "End time must be after start time.";
        }

        if (Duration.between(start, end).toMinutes() < Calendar.minimumLengthOfAppointment) {
            return "Appointment must last at least "
                + Calendar.minimumLengthOfAppointment + " minutes.";
        }

        return "";
    }

    private static List<LocalTime> timeSlots(LocalTime first, LocalTime last) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = first;
        while (!current.isAfter(last)) {
            slots.add(current);
            current = current.plusMinutes(Calendar.alignmentOfAppointment);
        }
        return slots;
    }

    private static Label errorLabel() {
        Label label = new Label();
        ActionPage.applyInlineErrorTextStyle(label);
        label.setWrapText(true);
        return label;
    }
}
