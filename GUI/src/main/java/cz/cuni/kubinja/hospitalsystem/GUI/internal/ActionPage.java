package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Shared page structure, navigation and feedback behavior for action workflows.
 */
public abstract class ActionPage implements Page {
    protected final Navigator navigator;
    protected final Hospital hospital;

    protected ActionPage(Navigator navigator, Hospital hospital) {
        this.navigator = navigator;
        this.hospital = hospital;
    }

    @Override
    public final Parent createContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 36, 30, 36));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label(getTitle());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Node body = createBody();

        if (shouldGrowBody()) {
            VBox.setVgrow(body, Priority.ALWAYS);
        }

        Separator separator = new Separator();

        Button back = new Button("Back");
        back.setMinHeight(42);
        back.setPrefWidth(160);
        back.setOnAction(event -> navigator.back());

        VBox footer = new VBox(18, separator, back);
        footer.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, body, footer);
        return root;
    }

    @Override
    public double getPreferredWidth() {
        return 700;
    }

    protected abstract Node createBody();

    protected boolean shouldGrowBody() {
        return true;
    }

    protected boolean showApiError(GeneralPacket packet) {
        if (packet.successful) {
            return false;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hospital System");
        alert.setHeaderText("The operation failed");
        alert.setContentText(packet.resolveStatus());
        alert.showAndWait();
        return true;
    }

    protected void showUnexpectedError(Throwable throwable) {
        Exception exception = throwable instanceof Exception error
                ? error
                : new Exception(throwable);
        showApiError(new GeneralPacket(exception));
    }

    protected void complete(String message) {
        showSuccess(message);
        navigator.back();
    }

    protected void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hospital System");
        alert.setHeaderText("Operation completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected GridPane personnelDetails(Person person, Detail... additionalDetails) {
        GridPane details = new GridPane();
        details.setHgap(18);
        details.setVgap(12);
        details.setAlignment(Pos.TOP_CENTER);

        addDetail(details, 0, "ID", Integer.toString(person.getId()));
        addDetail(details, 1, "First name", person.getFirstName());
        addDetail(details, 2, "Last name", person.getLastName());
        addDetail(details, 3, "Date of birth", person.getDateOfBirth().toString());

        for (int index = 0; index < additionalDetails.length; index++) {
            Detail detail = additionalDetails[index];
            addDetail(details, index + 4, detail.name(), detail.value());
        }

        return details;
    }

    public record Detail(String name, String value) {}

    private void addDetail(GridPane details, int row, String name, String value) {
        Label nameLabel = new Label(name + ":");
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(450);

        details.add(nameLabel, 0, row);
        details.add(valueLabel, 1, row);
    }
}
