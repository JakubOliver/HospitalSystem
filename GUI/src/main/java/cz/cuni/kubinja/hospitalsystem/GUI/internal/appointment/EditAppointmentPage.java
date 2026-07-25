package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Appointment;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;

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

        Button save = createActionButton("Save changes");
        save.setDefaultButton(true);
        save.disableProperty().bind(
                appointmentLoaded.not()
                        .or(busy)
                        .or(form.validProperty().not())
        );
        save.setOnAction(event -> saveAppointment());

        ProgressIndicator progress = createProgressIndicator(busy);

        loadOptions();
        return createCenteredBox(
                18,
                progress,
                idInput,
                new Separator(),
                form,
                save
        );
    }

    private void loadOptions() {
        runBackgroundOperation(
                busy,
                () -> AppointmentOptions.load(hospital),
                options -> {
                    if (!options.successful()) {
                        showApiError(options.error());
                        return;
                    }

                    form.setOptions(options);
                    optionsReady.set(true);
                }
        );
    }

    private void loadAppointment() {
        appointmentLoaded.set(false);
        int appointmentId = idInput.getEntityId();
        runBackgroundOperation(
                busy,
                () -> hospital.getAppointment(appointmentId),
                packet -> finishLoad(appointmentId, packet)
        );
    }

    private void finishLoad(int appointmentId, DataPacket<Appointment> packet) {
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

        runBackgroundOperation(
                busy,
                () -> hospital.updateAppointment(appointment),
                this::finishSave
        );
    }

    private void finishSave(GeneralPacket packet) {
        if (!showApiError(packet)) {
            complete("Appointment " + loadedAppointmentId + " was updated.");
        }
    }
}
