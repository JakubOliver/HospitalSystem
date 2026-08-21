package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class NavigatorTest {
    private Stage stage;
    private Navigator navigator;
    private TestPage initialPage;

    @Start
    private void start(Stage stage) {
        this.stage = stage;
        navigator = new Navigator(stage);
        initialPage = new TestPage("Initial", 640, 480);
        navigator.start(initialPage);
    }

    @Test
    void startShowsInitialPage() {
        assertTrue(stage.isShowing());
        assertEquals("Initial", stage.getTitle());
        assertSame(initialPage.latestContent, stage.getScene().getRoot());
    }

    @Test
    void backOnInitialPageDoesNothing(FxRobot robot) {
        robot.interact(navigator::back);

        assertEquals("Initial", stage.getTitle());
        assertSame(initialPage.latestContent, stage.getScene().getRoot());
    }

    @Test
    void navigateShowsNewPage(FxRobot robot) {
        TestPage nextPage = new TestPage("Next", 800, 600);

        robot.interact(() -> navigator.navigate(nextPage));

        assertEquals("Next", stage.getTitle());
        assertSame(nextPage.latestContent, stage.getScene().getRoot());
    }

    @Test
    void backRestoresPreviousPage(FxRobot robot) {
        TestPage nextPage = new TestPage("Next", 800, 600);
        robot.interact(() -> navigator.navigate(nextPage));

        robot.interact(navigator::back);

        assertEquals("Initial", stage.getTitle());
        assertSame(initialPage.latestContent, stage.getScene().getRoot());
    }

    @Test
    void closeHidesStage(FxRobot robot) {
        robot.interact(navigator::close);

        assertFalse(stage.isShowing());
    }

    private static final class TestPage implements Page {
        private final String title;
        private final double width;
        private final double height;
        private Parent latestContent;

        private TestPage(String title, double width, double height) {
            this.title = title;
            this.width = width;
            this.height = height;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Parent createContent() {
            latestContent = new StackPane(new Label("content"));
            return latestContent;
        }

        @Override
        public double getPreferredWidth() {
            return width;
        }

        @Override
        public double getPreferredHeight() {
            return height;
        }
    }
}
