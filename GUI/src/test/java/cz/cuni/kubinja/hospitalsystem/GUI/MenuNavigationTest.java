package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.testing.GuiTestBase;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class MenuNavigationTest extends GuiTestBase {
    @TempDir
    private Path temporaryDirectory;

    @Start
    private void start(Stage stage) throws Exception {
        initialize(stage, temporaryDirectory);
        show(new MainMenu(navigator, hospital));
    }

    @Test
    void mainMenuShowsAllSections(FxRobot robot) {
        Set<String> buttonTexts = robot.lookup(".button").queryAll().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .map(Button::getText)
                .collect(Collectors.toSet());

        assertTrue(buttonTexts.containsAll(Set.of(
                "Patients",
                "Doctors",
                "Calendar",
                "Export",
                "Statistics",
                "End"
        )));
    }

    @Test
    void patientMenuCanNavigateBackToMainMenu(FxRobot robot) {
        robot.clickOn("Patients");
        assertEquals("Patients", stage.getTitle());

        robot.clickOn("Back");
        assertEquals("Hospital System", stage.getTitle());
    }

    @Test
    void endClosesThePrimaryStage(FxRobot robot) {
        robot.clickOn("End");

        assertFalse(stage.isShowing());
    }
}
