package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.BackgroundOperation;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

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

        Button save = new Button("Save appointment");
        save.setMinHeight(42);
        save.setPrefWidth(190);
        save.setDefaultButton(true);
        // The binds are a little bit strange way how in JavaFx can be encoded conditions with logic
        save.disableProperty().bind(
                ready.not().or(busy).or(form.validProperty().not())
        );
        save.setOnAction(event -> saveAppointment());

        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(42, 42);
        progress.visibleProperty().bind(busy);
        progress.managedProperty().bind(progress.visibleProperty());

        VBox body = new VBox(18, progress, form, save);
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
                    ready.set(true);
                },
                exception -> {
                    busy.set(false);
                    showUnexpectedError(exception);
                }
        );
    }

    private void saveAppointment() {
        busy.set(true);
        BackgroundOperation.run(
                () -> hospital.addAppointment(form.getAppointmentData()),
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
            complete("Appointment was added.");
        }
    }
}
