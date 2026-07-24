package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainGUI extends Application {
    private static final String DATABASE_PATH = "jdbc:sqlite:database.db";

    @Override
    public void start(Stage primaryStage) {
        Hospital hospital;
        try {
            hospital = new Hospital(DATABASE_PATH);
        } catch (DatabaseException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("Hospital System could not be started");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            Platform.exit();
            return;
        }

        Navigator navigator = new Navigator(primaryStage);
        navigator.start(new MainMenu(navigator, hospital));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
