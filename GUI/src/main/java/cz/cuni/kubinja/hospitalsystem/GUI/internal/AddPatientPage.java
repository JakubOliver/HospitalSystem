package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Page for creating a patient.
 */
final class AddPatientPage extends PersonnelActionPage {
    AddPatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Add new patient";
    }

    @Override
    protected Node createBody() {
        PatientForm form = new PatientForm();
        Button save = new Button("Save patient");

        save.setMinHeight(42);
        save.setPrefWidth(180);
        save.setDefaultButton(true);
        save.disableProperty().bind(form.validProperty().not());

        save.setOnAction(event -> {
            DataPacket<Patient> packet = hospital.addPatient(form.getPatientData());
            if (!showApiError(packet)) {
                complete("Patient was added with ID " + packet.data.getId() + ".");
            }
        });

        VBox body = new VBox(22, form, save);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }
}
