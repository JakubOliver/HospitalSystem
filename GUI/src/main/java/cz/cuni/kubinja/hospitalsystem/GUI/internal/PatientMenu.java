package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientData;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PatientsDetails;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;
import cz.cuni.kubinja.hospitalsystem.menu.PersonnelMenu;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PatientMenu implements Page, PersonnelMenu {
    Stage stage;

    public PatientMenu(Stage stage){
        this.stage = stage;
    }

    @Override
    public String getTitle() {
        return "Patient Menu";
    }

    @Override
    public void define() {
        stage.setTitle(getTitle());

        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        getTitleLabel(root);

        addOption("Add", this::add, root);
        addOption("Edit", this::edit, root);
        addOption("Delete", this::delete, root);
        addOption("Find by ID", this::findById, root);
        addOption("Show all", this::all, root);

        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    @Override
    public void add() {
        Dialog<PatientData> dialog = new Dialog<>();

        dialog.setTitle("Add Patient");
        dialog.setHeaderText("Add new patient");
        dialog.setContentText("Enter patient's data");

        TextField name = new TextField();
        name.setPromptText("Name");
        TextField surname = new TextField();
        surname.setPromptText("Surname");
        TextField dateOfBirth = new TextField();
        dateOfBirth.setPromptText("Date of birth");
        TextField anamnesis = new TextField();
        anamnesis.setPromptText("Anamnesis");

        dialog.getDialogPane().setContent(new VBox(
                new Label("Name"), name,
                new Label("Surname"), surname,
                new Label("Date of birth"), dateOfBirth,
                new Label("Anamnesis"), anamnesis
        ));

        ButtonType saveButtonType = new ButtonType("Uložit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                return new PatientData(
                        new PersonData(
                                name.getText(),
                                surname.getText(),
                                LocalDate.parse(dateOfBirth.getText().trim())
                        ),
                        new PatientsDetails(
                                anamnesis.getText()
                        )
                );
            }

            return null;
        });

        dialog.showAndWait();

        PatientData data = dialog.getResult();

        System.out.println(data);
    }

    @Override
    public void edit() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void findById() {

    }

    @Override
    public void all() {

    }
}
