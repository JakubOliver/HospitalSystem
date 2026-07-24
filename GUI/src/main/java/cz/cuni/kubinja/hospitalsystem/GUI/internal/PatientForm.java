package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientsDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;
import cz.cuni.kubinja.hospitalsystem.menu.InputValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Reusable patient editor with inline validation.
 */
final class PatientForm extends GridPane {
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final DatePicker dateOfBirth = new DatePicker();
    private final TextArea anamnesis = new TextArea();

    private final Label firstNameError = errorLabel();
    private final Label lastNameError = errorLabel();
    private final Label dateOfBirthError = errorLabel();
    private final Label anamnesisError = errorLabel();
    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    PatientForm() {
        setHgap(14);
        setVgap(6);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(650);

        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(110);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        fields.setFillWidth(true);
        getColumnConstraints().addAll(labels, fields);

        firstName.setPromptText("First name");
        lastName.setPromptText("Last name");
        dateOfBirth.setPromptText("YYYY-MM-DD");
        dateOfBirth.setEditable(false);
        anamnesis.setPromptText("Anamnesis");
        anamnesis.setPrefRowCount(4);
        anamnesis.setWrapText(true);

        addField(0, "First name", firstName, firstNameError);
        addField(2, "Last name", lastName, lastNameError);
        addField(4, "Date of birth", dateOfBirth, dateOfBirthError);
        addField(6, "Anamnesis", anamnesis, anamnesisError);

        firstName.textProperty().addListener((observable, oldValue, newValue) -> validate());
        lastName.textProperty().addListener((observable, oldValue, newValue) -> validate());
        dateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> validate());
        anamnesis.textProperty().addListener((observable, oldValue, newValue) -> validate());

        validate();
    }

    BooleanProperty validProperty() {
        return valid;
    }

    PatientData getPatientData() {
        return new PatientData(
                new PersonData(
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        dateOfBirth.getValue()
                ),
                new PatientsDetails(anamnesis.getText().trim())
        );
    }

    void setPatient(Patient patient) {
        firstName.setText(patient.getFirstName());
        lastName.setText(patient.getLastName());
        dateOfBirth.setValue(patient.getDateOfBirth());
        anamnesis.setText(patient.getAnamnesis());

        validate();
    }

    private void addField(int row, String text, javafx.scene.Node field, Label error) {
        Label label = new Label(text + ":");

        add(label, 0, row);
        add(field, 1, row);
        add(error, 1, row + 1);

        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void validate() {
        String firstError = requiredTextError(firstName.getText());
        String lastError = requiredTextError(lastName.getText());
        String dateError = birthDateError();
        String anamnesisValidation = requiredTextError(anamnesis.getText());

        firstNameError.setText(firstError);
        lastNameError.setText(lastError);
        dateOfBirthError.setText(dateError);
        anamnesisError.setText(anamnesisValidation);

        valid.set(firstError.isEmpty()
                && lastError.isEmpty()
                && dateError.isEmpty()
                && anamnesisValidation.isEmpty()
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

    private static String requiredTextError(String value) {
        return InputValidator.hasText(value) ? "" : "This field is required.";
    }

    private static Label errorLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: #b00020; -fx-font-size: 11px;");

        return label;
    }
}
