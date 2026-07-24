package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for retrieving and editing a patient.
 */
final class EditPatientPage extends PatientActionPage {
    private final BooleanProperty patientLoaded = new SimpleBooleanProperty(false);
    private IdInput idInput;
    private PatientForm form;
    private int loadedPatientId;

    EditPatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Edit existing patient";
    }

    @Override
    protected Node createBody() {
        form = new PatientForm();
        form.disableProperty().bind(patientLoaded.not());

        Button save = new Button("Save changes");
        save.setMinHeight(42);
        save.setPrefWidth(180);
        save.setDefaultButton(true);
        save.disableProperty().bind(patientLoaded.not().or(form.validProperty().not()));
        save.setOnAction(event -> savePatient());

        idInput = new IdInput("Load", this::loadPatient);
        idInput.textProperty().addListener((observable, oldValue, newValue) -> patientLoaded.set(false));

        VBox body = new VBox(22, idInput, new Separator(), form, save);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void loadPatient() {
        DataPacket<Patient> packet = hospital.getPatient(idInput.getPatientId());
        if (showApiError(packet)) {
            patientLoaded.set(false);
            return;
        }

        loadedPatientId = packet.data.getId();
        form.setPatient(packet.data);
        patientLoaded.set(true);
    }

    private void savePatient() {
        PatientData patientData = form.getPatientData();

        GeneralPacket packet = hospital.updatePatient(new Patient(
                new Person(loadedPatientId, patientData.person()),
                patientData.details()
        ));
        if (!showApiError(packet)) {
            complete("Patient " + loadedPatientId + " was updated.");
        }
    }
}
