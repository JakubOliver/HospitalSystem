package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.BackgroundOperation;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonKinds;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;

/**
 * Page listing appointments for one patient or doctor.
 */
final class PersonnelAppointmentsPage extends ActionPage {
    private final PersonKinds kind;
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private IdInput idInput;
    private AppointmentTable table;
    private int loadedPersonnelId;

    PersonnelAppointmentsPage(
            Navigator navigator,
            Hospital hospital,
            PersonKinds kind
    ) {
        super(navigator, hospital);
        this.kind = kind;
    }

    @Override
    public String getTitle() {
        return kind == PersonKinds.Patient
                ? "Patient appointments"
                : "Doctor appointments";
    }

    @Override
    public double getPreferredWidth() {
        return 1050;
    }

    @Override
    protected Node createBody() {
        table = new AppointmentTable();

        String personnelName = kind == PersonKinds.Patient ? "Patient" : "Doctor";
        idInput = new IdInput(personnelName, "Load", this::loadAppointments);
        idInput.disableProperty().bind(busy);
        idInput.textProperty().addListener((observable, oldValue, newValue) -> {
            loadedPersonnelId = 0;
            table.getItems().clear();
        });

        Button refresh = new Button("Refresh");
        refresh.setMinHeight(42);
        refresh.setPrefWidth(160);
        refresh.disableProperty().bind(busy);
        refresh.setOnAction(event -> {
            if (loadedPersonnelId > 0) {
                loadAppointments(loadedPersonnelId);
            }
        });

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(42, 42);
        progress.visibleProperty().bind(busy);
        progress.managedProperty().bind(progress.visibleProperty());

        VBox body = new VBox(14, idInput, progress, table, refresh);
        body.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(table, Priority.ALWAYS);
        return body;
    }

    private void loadAppointments() {
        loadedPersonnelId = idInput.getEntityId();
        loadAppointments(loadedPersonnelId);
    }

    private void loadAppointments(int personnelId) {
        busy.set(true);
        BackgroundOperation.run(
                () -> hospital.getAppointmentSummariesForPersonnel(
                        personnelId,
                        kind
                ),
                this::finishLoad,
                exception -> {
                    busy.set(false);
                    table.getItems().clear();
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishLoad(DataPacket<List<AppointmentSummary>> packet) {
        busy.set(false);
        if (showApiError(packet)) {
            table.getItems().clear();
            return;
        }

        table.getItems().setAll(packet.data);
    }
}
