package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.atomic.AtomicInteger;

import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.button;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.label;
import static cz.cuni.kubinja.hospitalsystem.GUI.testing.FxTestNodes.setText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class IdInputTest {
    private final AtomicInteger actionCount = new AtomicInteger();
    private IdInput input;

    @Start
    private void start(Stage stage) {
        input = new IdInput("Patient", "Find", actionCount::incrementAndGet);
        stage.setScene(new Scene(input, 400, 160));
        stage.show();
    }

    @Test
    void emptyInputShowsRequiredError(FxRobot robot) {
        assertEquals("Enter an ID.", label(robot, "#entity-id-error").getText());
        assertTrue(button(robot, "#entity-id-action").isDisabled());
    }

    @Test
    void nonNumericInputIsRejected(FxRobot robot) {
        setText(robot, "#entity-id", "patient-one");

        assertEquals(
            "ID must be a positive integer.",
            label(robot, "#entity-id-error").getText()
        );
        assertTrue(button(robot, "#entity-id-action").isDisabled());
    }

    @Test
    void nonPositiveInputIsRejected(FxRobot robot) {
        setText(robot, "#entity-id", "0");
        assertTrue(button(robot, "#entity-id-action").isDisabled());

        setText(robot, "#entity-id", "-3");
        assertEquals(
            "ID must be a positive integer.",
            label(robot, "#entity-id-error").getText()
        );
    }

    @Test
    void positiveInputEnablesAndInvokesAction(FxRobot robot) {
        setText(robot, "#entity-id", " 42 ");
        Button action = button(robot, "#entity-id-action");

        assertFalse(action.isDisabled());
        assertEquals("", label(robot, "#entity-id-error").getText());
        assertEquals(42, input.getEntityId());

        robot.interact(action::fire);
        assertEquals(1, actionCount.get());
    }
}
