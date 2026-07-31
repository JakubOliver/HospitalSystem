package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
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
 * Personnel ID entry with inline validation and an associated action.
 */
public final class IdInput extends VBox {
    private static final double VERTICAL_SPACING = 6;
    private static final double ID_FIELD_MAX_WIDTH = 180;
    private static final double ACTION_BUTTON_MIN_WIDTH = 100;
    private static final double ROW_SPACING = 10;

    private final TextField id = new TextField();
    private final Label error = new Label();
    private final Button action = new Button();
    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    /**
     * Constructor of IdInput class.
     *
     * @param personnelName Name of the personnel (patient or doctor)
     * @param actionText Text of the action button
     * @param actionHandler Handler for the action button
     */
    public IdInput(
            String personnelName,
            String actionText,
            Runnable actionHandler
    ) {
        super(VERTICAL_SPACING);
        setAlignment(Pos.CENTER);

        id.setPromptText(personnelName + " ID");
        id.setMaxWidth(ID_FIELD_MAX_WIDTH);

        action.setText(actionText);
        action.setMinWidth(ACTION_BUTTON_MIN_WIDTH);
        action.setOnAction(event -> actionHandler.run());
        action.disableProperty().bind(valid.not());

        ActionPage.applyInlineErrorTextStyle(error);

        HBox row = new HBox(
            ROW_SPACING,
            new Label(personnelName + " ID:"),
            id,
            action
        );
        row.setAlignment(Pos.CENTER);
        getChildren().addAll(row, error);

        id.textProperty().addListener((observable, oldValue, newValue) -> validate());
        validate();
    }

    /**
     * Returns the ID of the personnel.
     *
     * @return ID of the personnel.
     */
    public int getPersonnelId() {
        return getEntityId();
    }

    /**
     * Returns the ID of the entity.
     *
     * @return ID of the entity.
     */
    public int getEntityId() {
        return InputValidator.parseInteger(id.getText());
    }

    /**
     * Returns the text property of the ID text field.
     *
     * @return The text property of the ID text field.
     */
    public StringProperty textProperty() {
        return id.textProperty();
    }

    private void validate() {
        String message;
        if (!InputValidator.hasText(id.getText())) {
            message = "Enter an ID.";
        } else if (!InputValidator.isPositiveInteger(id.getText())) {
            message = "ID must be a positive integer.";
        } else {
            message = "";
        }

        error.setText(message);
        valid.set(message.isEmpty());
    }
}
