package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.menu.InputValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Patient ID entry with inline validation and an associated action.
 */
final class IdInput extends VBox {
    private final TextField id = new TextField();
    private final Label error = new Label();
    private final Button action = new Button();
    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    IdInput(String actionText, Runnable actionHandler) {
        super(6);
        setAlignment(Pos.CENTER);

        id.setPromptText("Patient ID");
        id.setMaxWidth(180);

        action.setText(actionText);
        action.setMinWidth(100);
        action.setOnAction(event -> actionHandler.run());
        action.disableProperty().bind(valid.not());

        error.setStyle("-fx-text-fill: #b00020; -fx-font-size: 11px;");

        HBox row = new HBox(10, new Label("Patient ID:"), id, action);
        row.setAlignment(Pos.CENTER);
        getChildren().addAll(row, error);

        id.textProperty().addListener((observable, oldValue, newValue) -> validate());
        validate();
    }

    int getPatientId() {
        return InputValidator.parseInteger(id.getText());
    }

    StringProperty textProperty() {
        return id.textProperty();
    }

    private void validate() {
        String message;
        if (!InputValidator.hasText(id.getText())) {
            message = "Enter a patient ID.";
        } else if (!InputValidator.isPositiveInteger(id.getText())) {
            message = "Patient ID must be a positive integer.";
        } else {
            message = "";
        }

        error.setText(message);
        valid.set(message.isEmpty());
    }
}
