package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for finding and displaying a patient by ID.
 */
final class FindPatientPage extends ActionPage {
    private IdInput idInput;
    private VBox details;

    FindPatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Find patient by ID";
    }

    @Override
    protected Node createBody() {
        details = new VBox();
        details.setAlignment(Pos.TOP_CENTER);

        idInput = new IdInput("Patient", "Find", this::findPatient);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> details.getChildren().clear()
        );

        VBox body = new VBox(22, idInput, new Separator(), details);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void findPatient() {
        DataPacket<Patient> packet = hospital.getPatient(idInput.getPersonnelId());
        if (showApiError(packet)) {
            details.getChildren().clear();
            return;
        }

        details.getChildren().setAll(personnelDetails(
                packet.data,
                new Detail("Anamnesis", packet.data.getAnamnesis())
        ));
    }
}
