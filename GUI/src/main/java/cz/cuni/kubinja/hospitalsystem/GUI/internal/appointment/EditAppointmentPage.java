package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.BackgroundOperation;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Appointment;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for loading and editing an appointment.
 */
final class EditAppointmentPage extends ActionPage {
    private final BooleanProperty optionsReady = new SimpleBooleanProperty(false);
    private final BooleanProperty appointmentLoaded = new SimpleBooleanProperty(false);
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private IdInput idInput;
    private AppointmentForm form;
    private int loadedAppointmentId;

    EditAppointmentPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Edit appointment";
    }

    @Override
    protected Node createBody() {
        form = new AppointmentForm();
        form.disableProperty().bind(appointmentLoaded.not().or(busy));

        idInput = new IdInput("Appointment", "Load", this::loadAppointment);
        idInput.disableProperty().bind(optionsReady.not().or(busy));
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> appointmentLoaded.set(false)
        );

        Button save = new Button("Save changes");
        save.setMinHeight(42);
        save.setPrefWidth(180);
        save.setDefaultButton(true);
        save.disableProperty().bind(
                appointmentLoaded.not()
                        .or(busy)
                        .or(form.validProperty().not())
        );
        save.setOnAction(event -> saveAppointment());

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(42, 42);
        progress.visibleProperty().bind(busy);
        progress.managedProperty().bind(progress.visibleProperty());

        VBox body = new VBox(
                18,
                progress,
                idInput,
                new Separator(),
                form,
                save
        );
        body.setAlignment(Pos.TOP_CENTER);
        loadOptions();
        return body;
    }

    private void loadOptions() {
        busy.set(true);
        BackgroundOperation.run(
                () -> AppointmentOptions.load(hospital),
                options -> {
                    busy.set(false);
                    if (!options.successful()) {
                        showApiError(options.error());
                        return;
                    }

                    form.setOptions(options);
                    optionsReady.set(true);
                },
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void loadAppointment() {
        busy.set(true);
        appointmentLoaded.set(false);
        int appointmentId = idInput.getEntityId();
        BackgroundOperation.run(
                () -> hospital.getAppointment(appointmentId),
                packet -> finishLoad(appointmentId, packet),
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishLoad(int appointmentId, DataPacket<Appointment> packet) {
        busy.set(false);
        if (showApiError(packet)) {
            return;
        }

        loadedAppointmentId = appointmentId;
        form.setAppointment(packet.data);
        appointmentLoaded.set(true);
    }

    private void saveAppointment() {
        AppointmentData data = form.getAppointmentData();
        Appointment appointment = new Appointment(
                loadedAppointmentId,
                form.getPatient(),
                form.getDoctor(),
                data.starTime(),
                data.endTime()
        );

        busy.set(true);
        BackgroundOperation.run(
                () -> hospital.updateAppointment(appointment),
                this::finishSave,
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void finishSave(GeneralPacket packet) {
        busy.set(false);
        if (!showApiError(packet)) {
            complete("Appointment " + loadedAppointmentId + " was updated.");
        }
    }
}
