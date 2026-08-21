package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

/**
 * Page for creating an appointment.
 */
final class AddAppointmentPage extends ActionPage {
    private final BooleanProperty ready = new SimpleBooleanProperty(false);
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private AppointmentForm form;

    AddAppointmentPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Add new appointment";
    }

    @Override
    protected Node createBody() {
        form = new AppointmentForm();
        form.disableProperty().bind(ready.not().or(busy));

        Button save = createActionButton("Save appointment");
        save.setDefaultButton(true);
        save.disableProperty().bind(
            ready.not().or(busy).or(form.validProperty().not())
        );
        save.setOnAction(event -> saveAppointment());

        ProgressIndicator progress = createProgressIndicator(busy);

        loadOptions();

        return createCenteredBox(18, progress, form, save);
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
                ready.set(true);
            }
        );
    }

    private void saveAppointment() {
        runBackgroundOperation(
            busy,
            () -> hospital.addAppointment(form.getAppointmentData()),
            this::finishSave
        );
    }

    private void finishSave(GeneralPacket packet) {
        if (!showApiError(packet)) {
            complete("Appointment was added.");
        }
    }
}
