package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.PersonnelForm;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.DoctorDetails;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Reusable doctor editor with inline validation.
 */
final class DoctorForm extends PersonnelForm {
    private final TextField specialization = new TextField();
    private final TextField department = new TextField();

    private final Label specializationError = errorLabel();
    private final Label departmentError = errorLabel();
    private final BooleanProperty detailsValid = new SimpleBooleanProperty(false);

    DoctorForm() {
        specialization.setPromptText("Specialization");
        department.setPromptText("Department");

        addField(6, "Specialization", specialization, specializationError);
        addField(8, "Department", department, departmentError);

        specialization.textProperty().addListener(
                (observable, oldValue, newValue) -> validate()
        );
        department.textProperty().addListener(
                (observable, oldValue, newValue) -> validate()
        );

        validate();
    }

    @Override
    protected BooleanExpression validProperty() {
        return super.validProperty().and(detailsValid);
    }

    DoctorData getDoctorData() {
        return new DoctorData(
                getPersonData(),
                new DoctorDetails(
                        specialization.getText().trim(),
                        department.getText().trim()
                )
        );
    }

    void setDoctor(Doctor doctor) {
        setPerson(doctor);
        specialization.setText(doctor.getSpecialization());
        department.setText(doctor.getDepartment());

        validate();
    }

    private void validate() {
        String specializationValidation = requiredTextError(specialization.getText());
        String departmentValidation = requiredTextError(department.getText());

        specializationError.setText(specializationValidation);
        departmentError.setText(departmentValidation);
        detailsValid.set(
                specializationValidation.isEmpty()
                        && departmentValidation.isEmpty()
        );
    }
}
