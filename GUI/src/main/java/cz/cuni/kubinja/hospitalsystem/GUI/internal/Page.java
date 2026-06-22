package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public interface Page {
    String getTitle();
    void define();

    default void getTitleLabel(VBox box){
        Label title = new Label(getTitle());
        title.setAlignment(Pos.TOP_CENTER);

        box.getChildren().add(title);
    }

    default void addOption(String option, Runnable method, VBox box){
        Label label = new Label(option);
        label.setOnMouseClicked(event -> method.run());
        box.getChildren().add(label);
    }
}
