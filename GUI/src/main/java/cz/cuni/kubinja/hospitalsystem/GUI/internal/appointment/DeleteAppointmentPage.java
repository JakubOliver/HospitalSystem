package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
        details = createCenteredBox(0);

        delete = createActionButton("Delete appointment");
        applyErrorTextStyle(delete);
        delete.disableProperty().bind(busy.or(appointmentLoaded.not()));

        idInput = new IdInput("Appointment", "Load", this::loadAppointment);
        idInput.disableProperty().bind(busy);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> clearAppointment()
        );

        ProgressIndicator progress = createProgressIndicator(busy);

        delete.setOnAction(event -> confirmDelete());

        return createCenteredBox(
                18,
                progress,
                idInput,
                new Separator(),
                details,
                delete
        );
    }

    private void loadAppointment() {
        clearAppointment();
        int appointmentId = idInput.getEntityId();
        runBackgroundOperation(
                busy,
                () -> hospital.getAppointmentSummary(appointmentId),
                this::finishLoad
        );
    }

    private void finishLoad(DataPacket<AppointmentSummary> packet) {
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
        if (!confirmAction(
                "Delete appointment",
                "Delete appointment " + loadedAppointment.id() + "?",
                loadedAppointment.patientName() + " with "
                        + loadedAppointment.doctorName()
                        + "\nThis action cannot be undone."
        )) {
            return;
        }

        runBackgroundOperation(
                busy,
                () -> hospital.deleteAppointment(loadedAppointment.id()),
                this::finishDelete
        );
    }

    private void finishDelete(GeneralPacket packet) {
        if (!showApiError(packet)) {
            complete("Appointment " + loadedAppointment.id() + " was deleted.");
        }
    }
}
