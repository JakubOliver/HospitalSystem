package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.PatientMenu;
import cz.cuni.kubinja.hospitalsystem.menu.Page;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application{
    Page[] menues = {new PatientMenu()};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Hospital System");
        root.getChildren().add(title);

        addMenu(root);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addMenu(VBox root){
        for (Page menu : menues){
            root.getChildren().add(createMenuEntry(menu));
        }
    }

    private VBox createMenuEntry(Page entryPage){
        VBox entry = new VBox();

        entry.getChildren().add(new Label("Patient"));

        return entry;
    }
}
