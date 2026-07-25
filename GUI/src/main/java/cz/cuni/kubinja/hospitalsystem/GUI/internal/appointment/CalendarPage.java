package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Read-only CalendarFX page for all departments or one selected department.
 */
final class CalendarPage extends ActionPage {
    private final boolean departmentOnly;
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private final ComboBox<String> department = new ComboBox<>();
    private final Label status = new Label();
    private CalendarView calendarView;
    private List<AppointmentSummary> appointments = List.of();

    CalendarPage(
            Navigator navigator,
            Hospital hospital,
            boolean departmentOnly
    ) {
        super(navigator, hospital);
        this.departmentOnly = departmentOnly;
    }

    @Override
    public String getTitle() {
        return departmentOnly ? "Department calendar" : "Hospital calendar";
    }

    @Override
    public double getPreferredWidth() {
        return 1200;
    }

    @Override
    public double getPreferredHeight() {
        return 850;
    }

    @Override
    protected Node createBody() {
        calendarView = new CalendarView();
        configureCalendar();

        department.setPromptText("Select department");
        department.setMinWidth(220);
        department.disableProperty().bind(busy);
        department.setOnAction(event -> rebuildCalendar());

        Button refresh = createActionButton(
                "Refresh",
                SECONDARY_BUTTON_WIDTH
        );
        refresh.disableProperty().bind(busy);
        refresh.setOnAction(event -> loadAppointments());

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER);
        if (departmentOnly) {
            controls.getChildren().add(department);
        }
        controls.getChildren().add(refresh);

        ProgressIndicator progress = createProgressIndicator(busy);

        status.setWrapText(true);

        VBox body = createCenteredBox(
                10,
                controls,
                progress,
                status,
                calendarView
        );
        VBox.setVgrow(calendarView, Priority.ALWAYS);
        loadAppointments();
        return body;
    }

    private void configureCalendar() {
        calendarView.showWeekPage();

        calendarView.setToday(LocalDate.now());
        calendarView.setDate(LocalDate.now());
        calendarView.setTime(LocalTime.now());

        calendarView.setRequestedTime(LocalTime.of(8, 0));
        calendarView.setStartTime(LocalTime.of(8, 0));
        calendarView.setEndTime(LocalTime.of(16, 0));

        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowSourceTray(false);
        calendarView.setShowSourceTrayButton(false);
        calendarView.setShowSearchField(false);
        calendarView.setShowSearchResultsTray(false);
        calendarView.setShowPrintButton(false);

        calendarView.setCalendarSourceFactory(parameter -> null);
        calendarView.setEntryFactory(parameter -> null);
        calendarView.setEntryEditPolicy(parameter -> false);
        calendarView.setContextMenuCallback(parameter -> null);
        calendarView.setEntryContextMenuCallback(parameter -> null);

        calendarView.setEntryDetailsPopOverContentCallback(parameter -> {
            Entry<?> entry = parameter.getEntry();
            if (entry.getUserObject() instanceof AppointmentSummary appointment) {
                return AppointmentDetails.create(appointment);
            }

            return new Label("Appointment details are unavailable.");
        });
    }

    private void loadAppointments() {
        status.setText("Loading appointments...");
        runBackgroundOperation(
                busy,
                hospital::getAppointmentSummaries,
                this::finishLoad,
                exception -> {
                    appointments = List.of();
                    calendarView.getCalendarSources().clear();
                    status.setText("Appointments could not be loaded. Use Refresh to try again.");
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishLoad(DataPacket<List<AppointmentSummary>> packet) {
        if (showApiError(packet)) {
            appointments = List.of();
            calendarView.getCalendarSources().clear();
            status.setText("Appointments could not be loaded. Use Refresh to try again.");
            return;
        }

        String previousDepartment = department.getValue();
        appointments = packet.data;
        if (departmentOnly) {
            List<String> departments = appointments.stream()
                    .map(AppointmentSummary::department)
                    .distinct()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
            department.getItems().setAll(departments);

            if (departments.contains(previousDepartment)) {
                department.setValue(previousDepartment);
            } else if (!departments.isEmpty()) {
                department.setValue(departments.getFirst());
            } else {
                department.setValue(null);
            }
        }

        rebuildCalendar();
    }

    private void rebuildCalendar() {
        String selectedDepartment = departmentOnly ? department.getValue() : null;
        if (departmentOnly && selectedDepartment == null) {
            calendarView.getCalendarSources().clear();
            status.setText("No departments with appointments were found.");
            return;
        }

        CalendarSource source = CalendarFxAdapter.createSource(
                appointments,
                selectedDepartment
        );
        calendarView.getCalendarSources().setAll(source);
        calendarView.refreshData();

        boolean empty = selectedDepartment == null
                ? appointments.isEmpty()
                : appointments.stream().noneMatch(
                appointment -> appointment.department().equals(selectedDepartment)
        );
        status.setText(empty ? "No appointments found." : "");
    }
}
