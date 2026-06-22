package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Page;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.PatientMenu;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu {
    Page[] tiles;
    Stage primaryStage;

    MainMenu(Stage primaryStage){
        this.primaryStage = primaryStage;

        this.tiles = new Page[]{new PatientMenu(primaryStage)};

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
        for (Page menu : tiles){
            root.getChildren().add(createMenuEntry(menu));
        }
    }

    private VBox createMenuEntry(Page entryPage){
        VBox entry = new VBox();

        Button button = new Button(entryPage.getTitle());
        entry.getChildren().add(button);
        button.setOnAction(event -> {
            entryPage.define();
        });

        return entry;
    }
}
