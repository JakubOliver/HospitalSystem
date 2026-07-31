package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.BasePage;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.time.format.DateTimeFormatter;

/**
 * Creates shared appointment detail presentations.
 */
final class AppointmentDetails {
    private static final DateTimeFormatter DATE_TIME =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AppointmentDetails() {}

    static GridPane create(AppointmentSummary appointment) {
        GridPane details = new GridPane();
        details.setHgap(18);
        details.setVgap(10);
        details.setPadding(new Insets(14));
        details.setAlignment(Pos.TOP_CENTER);

        add(details, 0, "Appointment ID", Integer.toString(appointment.id()));
        add(details, 1, "Patient", appointment.patientName() + " (ID " + appointment.patientId() + ")");
        add(details, 2, "Doctor", appointment.doctorName() + " (ID " + appointment.doctorId() + ")");
        add(details, 3, "Department", appointment.department());
        add(details, 4, "Start", DATE_TIME.format(appointment.startTime()));
        add(details, 5, "End", DATE_TIME.format(appointment.endTime()));

        return details;
    }

    private static void add(GridPane grid, int row, String name, String value) {
        Label nameLabel = new Label(name + ":");
        BasePage.applyEmphasizedTextStyle(nameLabel);
        grid.add(nameLabel, 0, row);
        grid.add(new Label(value), 1, row);
    }
}
