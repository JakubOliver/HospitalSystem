package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for retrieving and deleting a doctor.
 */
final class DeleteDoctorPage extends PersonnelActionPage {
    private IdInput idInput;
    private VBox details;
    private Button delete;
    private Doctor loadedDoctor;

    DeleteDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Delete existing doctor";
    }

    @Override
    protected Node createBody() {
        details = new VBox();
        details.setAlignment(Pos.TOP_CENTER);

        delete = new Button("Delete doctor");
        delete.setMinHeight(42);
        delete.setPrefWidth(180);
        delete.setDisable(true);
        delete.setStyle("-fx-text-fill: #b00020;");
        delete.setOnAction(event -> confirmDelete());

        idInput = new IdInput("Doctor", "Load", this::loadDoctor);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> clearDoctor()
        );

        VBox body = new VBox(22, idInput, new Separator(), details, delete);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void loadDoctor() {
        DataPacket<Doctor> packet = hospital.getDoctor(idInput.getPersonnelId());
        if (showApiError(packet)) {
            clearDoctor();
            return;
        }

        loadedDoctor = packet.data;
        details.getChildren().setAll(personnelDetails(
                loadedDoctor,
                new Detail("Specialization", loadedDoctor.getSpecialization()),
                new Detail("Department", loadedDoctor.getDepartment())
        ));
        delete.setDisable(false);
    }

    private void clearDoctor() {
        loadedDoctor = null;
        if (details != null) {
            details.getChildren().clear();
        }
        if (delete != null) {
            delete.setDisable(true);
        }
    }

    private void confirmDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete doctor");
        confirmation.setHeaderText("Delete doctor " + loadedDoctor.getId() + "?");
        confirmation.setContentText(
                loadedDoctor.getFirstName() + " " + loadedDoctor.getLastName()
                        + "\nThis action cannot be undone."
        );

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        GeneralPacket packet = hospital.deleteDoctor(loadedDoctor.getId());
        if (!showApiError(packet)) {
            complete("Doctor " + loadedDoctor.getId() + " was deleted.");
        }
    }
}
