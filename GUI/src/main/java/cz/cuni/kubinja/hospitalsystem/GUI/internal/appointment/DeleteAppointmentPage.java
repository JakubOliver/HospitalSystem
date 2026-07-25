package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.BackgroundOperation;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for loading and deleting an appointment.
 */
final class DeleteAppointmentPage extends ActionPage {
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private final BooleanProperty appointmentLoaded = new SimpleBooleanProperty(false);
    private IdInput idInput;
    private VBox details;
    private Button delete;
    private AppointmentSummary loadedAppointment;

    DeleteAppointmentPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Delete appointment";
    }

    @Override
    protected Node createBody() {
        details = new VBox();
        details.setAlignment(Pos.TOP_CENTER);

        delete = new Button("Delete appointment");
        delete.setMinHeight(42);
        delete.setPrefWidth(190);
        applyErrorTextStyle(delete);
        delete.disableProperty().bind(busy.or(appointmentLoaded.not()));

        idInput = new IdInput("Appointment", "Load", this::loadAppointment);
        idInput.disableProperty().bind(busy);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> clearAppointment()
        );

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(42, 42);
        progress.visibleProperty().bind(busy);
        progress.managedProperty().bind(progress.visibleProperty());

        delete.setOnAction(event -> confirmDelete());

        VBox body = new VBox(
                18,
                progress,
                idInput,
                new Separator(),
                details,
                delete
        );
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void loadAppointment() {
        busy.set(true);
        clearAppointment();
        int appointmentId = idInput.getEntityId();
        BackgroundOperation.run(
                () -> hospital.getAppointmentSummary(appointmentId),
                this::finishLoad,
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishLoad(DataPacket<AppointmentSummary> packet) {
        busy.set(false);
        if (showApiError(packet)) {
            return;
        }

        loadedAppointment = packet.data;
        appointmentLoaded.set(true);
        details.getChildren().setAll(AppointmentDetails.create(packet.data));
    }

    private void clearAppointment() {
        loadedAppointment = null;
        appointmentLoaded.set(false);
        if (details != null) {
            details.getChildren().clear();
        }
    }

    private void confirmDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete appointment");
        confirmation.setHeaderText(
                "Delete appointment " + loadedAppointment.id() + "?"
        );
        confirmation.setContentText(
                loadedAppointment.patientName() + " with "
                        + loadedAppointment.doctorName()
                        + "\nThis action cannot be undone."
        );
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        busy.set(true);
        BackgroundOperation.run(
                () -> hospital.deleteAppointment(loadedAppointment.id()),
                this::finishDelete,
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishDelete(GeneralPacket packet) {
        busy.set(false);
        if (!showApiError(packet)) {
            complete("Appointment " + loadedAppointment.id() + " was deleted.");
        }
    }
}
