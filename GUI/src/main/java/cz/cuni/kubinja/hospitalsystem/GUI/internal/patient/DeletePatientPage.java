package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for retrieving and deleting a patient.
 */
final class DeletePatientPage extends ActionPage {
    private IdInput idInput;
    private VBox details;
    private Button delete;
    private Patient loadedPatient;

    DeletePatientPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Delete existing patient";
    }

    @Override
    protected Node createBody() {
        details = new VBox();
        details.setAlignment(Pos.TOP_CENTER);

        delete = new Button("Delete patient");
        delete.setMinHeight(42);
        delete.setPrefWidth(180);
        delete.setDisable(true);
        delete.setStyle("-fx-text-fill: #b00020;");
        delete.setOnAction(event -> confirmDelete());

        idInput = new IdInput("Patient", "Load", this::loadPatient);
        idInput.textProperty().addListener((observable, oldValue, newValue) -> clearPatient());

        VBox body = new VBox(22, idInput, new Separator(), details, delete);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void loadPatient() {
        DataPacket<Patient> packet = hospital.getPatient(idInput.getPersonnelId());
        if (showApiError(packet)) {
            clearPatient();
            return;
        }

        loadedPatient = packet.data;
        details.getChildren().setAll(personnelDetails(
                loadedPatient,
                new Detail("Anamnesis", loadedPatient.getAnamnesis())
        ));
        delete.setDisable(false);
    }

    private void clearPatient() {
        loadedPatient = null;
        if (details != null) {
            details.getChildren().clear();
        }
        if (delete != null) {
            delete.setDisable(true);
        }
    }

    private void confirmDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete patient");
        confirmation.setHeaderText("Delete patient " + loadedPatient.getId() + "?");
        confirmation.setContentText(
                loadedPatient.getFirstName() + " " + loadedPatient.getLastName()
                        + "\nThis action cannot be undone."
        );

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        GeneralPacket packet = hospital.deletePatient(loadedPatient.getId());
        if (!showApiError(packet)) {
            complete("Patient " + loadedPatient.getId() + " was deleted.");
        }
    }
}
