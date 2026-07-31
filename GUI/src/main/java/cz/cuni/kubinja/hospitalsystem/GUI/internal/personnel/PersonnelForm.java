package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;
import cz.cuni.kubinja.hospitalsystem.menu.InputValidator;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Common editor fields and validation for patients and doctors.
 */
public abstract class PersonnelForm extends GridPane {
    private static final double HORIZONTAL_GAP = 14;
    private static final double VERTICAL_GAP = 6;
    private static final double FORM_PADDING = 10;
    private static final double FORM_MAX_WIDTH = 650;
    private static final double LABEL_COLUMN_MIN_WIDTH = 110;

    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final DatePicker dateOfBirth = new DatePicker();

    private final Label firstNameError = errorLabel();
    private final Label lastNameError = errorLabel();
    private final Label dateOfBirthError = errorLabel();
    private final BooleanProperty commonValid = new SimpleBooleanProperty(false);

    protected PersonnelForm() {
        setHgap(HORIZONTAL_GAP);
        setVgap(VERTICAL_GAP);
        setPadding(new Insets(FORM_PADDING));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(FORM_MAX_WIDTH);

        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(LABEL_COLUMN_MIN_WIDTH);

        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);

        getColumnConstraints().addAll(labels, fields);

        firstName.setPromptText("First name");
        lastName.setPromptText("Last name");
        dateOfBirth.setPromptText("YYYY-MM-DD");
        dateOfBirth.setEditable(false);

        addField(0, "First name", firstName, firstNameError);
        addField(2, "Last name", lastName, lastNameError);
        addField(4, "Date of birth", dateOfBirth, dateOfBirthError);

        firstName.textProperty().addListener((observable, oldValue, newValue) -> validate());
        lastName.textProperty().addListener((observable, oldValue, newValue) -> validate());
        dateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        validate();
    }

    protected BooleanExpression validProperty() {
        return commonValid;
    }

    protected final PersonData getPersonData() {
        return new PersonData(
                firstName.getText().trim(),
                lastName.getText().trim(),
                dateOfBirth.getValue()
        );
    }

    protected final void setPerson(Person person) {
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        dateOfBirth.setValue(person.getDateOfBirth());

        validate();
    }

    protected final void addField(int row, String text, Node field, Label error) {
        add(new Label(text + ":"), 0, row);
        add(field, 1, row);
        add(error, 1, row + 1);

        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    protected static String requiredTextError(String value) {
        return InputValidator.hasText(value) ? "" : "This field is required.";
    }

    protected static Label errorLabel() {
        Label label = new Label();
        ActionPage.applyInlineErrorTextStyle(label);

        return label;
    }

    private void validate() {
        String firstError = requiredTextError(firstName.getText());
        String lastError = requiredTextError(lastName.getText());
        String dateError = birthDateError();

        firstNameError.setText(firstError);
        lastNameError.setText(lastError);
        dateOfBirthError.setText(dateError);
        commonValid.set(
                firstError.isEmpty()
                        && lastError.isEmpty()
                        && dateError.isEmpty()
        );
    }

    private String birthDateError() {
        if (dateOfBirth.getValue() == null) {
            return "Select a date of birth.";
        }

        return InputValidator.isValidPersonnelDate(dateOfBirth.getValue())
                ? ""
                : "Date must be after 1900-01-01 and not in the future.";
    }
}
