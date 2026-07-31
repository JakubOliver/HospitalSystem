package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
 * Shared table structure for displaying patients and doctors.
 *
 * @param <T> Type of personnel displayed in the table.
 */
public abstract class PersonnelTablePage<T extends Person> extends ActionPage {
    private TableView<T> table;

    protected PersonnelTablePage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public double getPreferredWidth() {
        return 920;
    }

    @Override
    protected final Node createBody() {
        table = new TableView<>();
        table.setPlaceholder(new Label(getEmptyMessage()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<T, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()));
        id.setMinWidth(60);

        TableColumn<T, String> firstName = new TableColumn<>("First name");
        firstName.setCellValueFactory(
            cell -> new SimpleStringProperty(cell.getValue().getFirstName())
        );

        TableColumn<T, String> lastName = new TableColumn<>("Last name");
        lastName.setCellValueFactory(
            cell -> new SimpleStringProperty(cell.getValue().getLastName())
        );

        TableColumn<T, LocalDate> dateOfBirth = new TableColumn<>("Date of birth");
        dateOfBirth.setCellValueFactory(
            cell -> new SimpleObjectProperty<>(cell.getValue().getDateOfBirth())
        );

        table.getColumns().addAll(List.of(id, firstName, lastName, dateOfBirth));
        addSpecificColumns(table);

        Button refresh = createActionButton(
            "Refresh",
            SECONDARY_BUTTON_WIDTH
        );
        refresh.setOnAction(event -> loadPersonnel());

        VBox body = createCenteredBox(16, table, refresh);
        VBox.setVgrow(table, Priority.ALWAYS);

        loadPersonnel();

        return body;
    }

    protected abstract String getEmptyMessage();

    protected abstract void addSpecificColumns(TableView<T> table);

    protected abstract DataPacket<List<T>> getAllPersonnel();

    private void loadPersonnel() {
        DataPacket<List<T>> packet = getAllPersonnel();
        if (showApiError(packet)) {
            table.getItems().clear();
            return;
        }

        table.setItems(FXCollections.observableArrayList(packet.data));
    }
}
