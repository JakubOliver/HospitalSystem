package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for retrieving and editing a doctor.
 */
final class EditDoctorPage extends PersonnelActionPage {
    private final BooleanProperty doctorLoaded = new SimpleBooleanProperty(false);
    private IdInput idInput;
    private DoctorForm form;
    private int loadedDoctorId;

    EditDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Edit existing doctor";
    }

    @Override
    protected Node createBody() {
        form = new DoctorForm();
        form.disableProperty().bind(doctorLoaded.not());

        Button save = new Button("Save changes");
        save.setMinHeight(42);
        save.setPrefWidth(180);
        save.setDefaultButton(true);
        save.disableProperty().bind(doctorLoaded.not().or(form.validProperty().not()));
        save.setOnAction(event -> saveDoctor());

        idInput = new IdInput("Doctor", "Load", this::loadDoctor);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> doctorLoaded.set(false)
        );

        VBox body = new VBox(22, idInput, new Separator(), form, save);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void loadDoctor() {
        DataPacket<Doctor> packet = hospital.getDoctor(idInput.getPersonnelId());
        if (showApiError(packet)) {
            doctorLoaded.set(false);
            return;
        }

        loadedDoctorId = packet.data.getId();
        form.setDoctor(packet.data);
        doctorLoaded.set(true);
    }

    private void saveDoctor() {
        DoctorData doctorData = form.getDoctorData();

        GeneralPacket packet = hospital.updateDoctor(new Doctor(
                new Person(loadedDoctorId, doctorData.person()),
                doctorData.details()
        ));

        if (!showApiError(packet)) {
            complete("Doctor " + loadedDoctorId + " was updated.");
        }
    }
}
