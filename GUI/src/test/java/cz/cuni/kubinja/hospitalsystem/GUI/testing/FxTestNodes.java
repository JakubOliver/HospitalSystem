package cz.cuni.kubinja.hospitalsystem.GUI.testing;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import org.testfx.api.FxRobot;

import java.time.LocalDate;

public final class FxTestNodes {
    private FxTestNodes() {}

    public static Button button(FxRobot robot, String selector) {
        return robot.lookup(selector).queryButton();
    }

    public static Label label(FxRobot robot, String selector) {
        return robot.lookup(selector).queryAs(Label.class);
    }

    public static void setText(FxRobot robot, String selector, String value) {
        TextInputControl control = robot.lookup(selector)
            .queryAs(TextInputControl.class);
        robot.interact(() -> control.setText(value));
    }

    public static void setDate(FxRobot robot, String selector, LocalDate value) {
        DatePicker picker = robot.lookup(selector).queryAs(DatePicker.class);
        robot.interact(() -> picker.setValue(value));
    }
}
