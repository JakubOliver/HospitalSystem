package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Page displaying all patients in a table.
 */
final class AllPatientsPage extends PatientActionPage {
    private TableView<Patient> table;

    AllPatientsPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "All patients";
    }

    @Override
    public double getPreferredWidth() {
        return 920;
    }

    @Override
    protected Node createBody() {
        table = new TableView<>();
        table.setPlaceholder(new Label("No patients found."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Patient, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()));
        id.setMinWidth(60);

        TableColumn<Patient, String> firstName = new TableColumn<>("First name");
        firstName.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getFirstName())
        );

        TableColumn<Patient, String> lastName = new TableColumn<>("Last name");
        lastName.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getLastName())
        );

        TableColumn<Patient, LocalDate> dateOfBirth = new TableColumn<>("Date of birth");
        dateOfBirth.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(cell.getValue().getDateOfBirth())
        );

        TableColumn<Patient, String> anamnesis = new TableColumn<>("Anamnesis");
        anamnesis.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getAnamnesis())
        );

        table.getColumns().setAll(List.of(id, firstName, lastName, dateOfBirth, anamnesis));

        Button refresh = new Button("Refresh");
        refresh.setMinHeight(42);
        refresh.setPrefWidth(160);
        refresh.setOnAction(event -> loadPatients());

        VBox body = new VBox(16, table, refresh);
        body.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(table, Priority.ALWAYS);
        loadPatients();
        return body;
    }

    private void loadPatients() {
        DataPacket<List<Patient>> packet = hospital.allPatients();
        if (showApiError(packet)) {
            table.getItems().clear();
            return;
        }

        table.setItems(FXCollections.observableArrayList(packet.data));
    }
}
