package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.layout.GridPane;

/**
 * Menu containing export actions.
 */
public class ExportMenu extends MenuPage {
    public ExportMenu(Navigator navigator) {
        super(navigator);
    }

    @Override
    public String getTitle() {
        return "Export";
    }

    @Override
    protected void addOptions(GridPane options) {
        addDisabledOption(options, "Export patients");
        addDisabledOption(options, "Export doctors");
        addDisabledOption(options, "Export appointments");
        addDisabledOption(options, "Export all");
    }
}
