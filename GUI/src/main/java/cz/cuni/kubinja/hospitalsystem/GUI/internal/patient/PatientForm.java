package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.PersonnelForm;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientsDetails;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * Reusable patient editor with inline validation.
 */
final class PatientForm extends PersonnelForm {
    private final TextArea anamnesis = new TextArea();

    private final Label anamnesisError = errorLabel();
    private final BooleanProperty anamnesisValid = new SimpleBooleanProperty(false);

    PatientForm() {
        anamnesis.setPromptText("Anamnesis");
        anamnesis.setPrefRowCount(4);
        anamnesis.setWrapText(true);

        addField(6, "Anamnesis", anamnesis, anamnesisError);

        anamnesis.textProperty().addListener((observable, oldValue, newValue) -> validate());

        validate();
    }

    @Override
    protected BooleanExpression validProperty() {
        return super.validProperty().and(anamnesisValid);
    }

    PatientData getPatientData() {
        return new PatientData(
                getPersonData(),
                new PatientsDetails(anamnesis.getText().trim())
        );
    }

    void setPatient(Patient patient) {
        setPerson(patient);
        anamnesis.setText(patient.getAnamnesis());

        validate();
    }

    private void validate() {
        String anamnesisValidation = requiredTextError(anamnesis.getText());

        anamnesisError.setText(anamnesisValidation);
        anamnesisValid.set(anamnesisValidation.isEmpty());
    }
}
