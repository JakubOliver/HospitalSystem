package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        Navigator navigator = new Navigator(primaryStage);
        navigator.start(new MainMenu(navigator));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
