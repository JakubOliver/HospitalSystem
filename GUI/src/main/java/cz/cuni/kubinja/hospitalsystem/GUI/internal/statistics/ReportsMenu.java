package cz.cuni.kubinja.hospitalsystem.GUI.internal.statistics;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.MenuPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import javafx.scene.layout.GridPane;

/**
 * Menu containing hospital reporting actions.
 */
public class ReportsMenu extends MenuPage {
    private final Hospital hospital;

    public ReportsMenu(Navigator navigator, Hospital hospital) {
        super(navigator);
        this.hospital = hospital;
    }

    @Override
    public String getTitle() {
        return "Statistics";
    }

    @Override
    protected void addOptions(GridPane options) {
        addOption(
                options,
                "Show statistics",
                () -> navigator.navigate(new StatisticsPage(navigator, hospital))
        );
    }
}
