package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Table displaying typed appointment summaries.
 */
final class AppointmentTable extends TableView<AppointmentSummary> {
    AppointmentTable() {
        setPlaceholder(new Label("No appointments found."));
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<AppointmentSummary, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(
                cell -> new ReadOnlyIntegerWrapper(cell.getValue().id())
        );
        id.setMinWidth(55);

        TableColumn<AppointmentSummary, String> patient =
            new TableColumn<>("Patient");
        patient.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
            cell.getValue().patientName()
                    + " (" + cell.getValue().patientId() + ")"
        ));

        TableColumn<AppointmentSummary, String> doctor =
            new TableColumn<>("Doctor");
        doctor.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
            cell.getValue().doctorName()
                    + " (" + cell.getValue().doctorId() + ")"
        ));

        TableColumn<AppointmentSummary, String> department =
            new TableColumn<>("Department");
        department.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(cell.getValue().department())
        );

        TableColumn<AppointmentSummary, LocalDate> date =
            new TableColumn<>("Date");
        date.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
            cell.getValue().startTime().toLocalDate()
        ));

        TableColumn<AppointmentSummary, LocalTime> start =
            new TableColumn<>("Start");
        start.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
            cell.getValue().startTime().toLocalTime()
        ));

        TableColumn<AppointmentSummary, LocalTime> end =
            new TableColumn<>("End");
        end.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(
            cell.getValue().endTime().toLocalTime()
        ));

        getColumns().addAll(List.of(
            id,
            patient,
            doctor,
            department,
            date,
            start,
            end
        ));
    }
}
