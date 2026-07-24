package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.layout.GridPane;

/**
 * Menu containing hospital reporting actions.
 */
public class ReportsMenu extends MenuPage {
    public ReportsMenu(Navigator navigator) {
        super(navigator);
    }

    @Override
    public String getTitle() {
        return "Statistics";
    }

    @Override
    protected void addOptions(GridPane options) {
        addDisabledOption(options, "Show statistics");
    }
}
