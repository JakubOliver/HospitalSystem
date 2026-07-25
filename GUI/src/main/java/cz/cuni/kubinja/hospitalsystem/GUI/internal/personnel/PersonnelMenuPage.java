package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.MenuPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.menu.PersonnelMenu;
import javafx.scene.layout.GridPane;

/**
 * Shared menu options for patient and doctor management.
 */
public abstract class PersonnelMenuPage extends MenuPage implements PersonnelMenu {
    private final String singularName;
    private final String pluralName;

    protected PersonnelMenuPage(
            Navigator navigator,
            String singularName,
            String pluralName
    ) {
        super(navigator);
        this.singularName = singularName;
        this.pluralName = pluralName;
    }

    @Override
    public final String getTitle() {
        return pluralName;
    }

    @Override
    protected final void addOptions(GridPane options) {
        String name = singularName.toLowerCase();
        String names = pluralName.toLowerCase();
        addOption(options, "Add new " + name, this::add);
        addOption(options, "Edit existing " + name, this::edit);
        addOption(options, "Delete existing " + name, this::delete);
        addOption(options, "Find " + name + " by ID", this::findById);
        addOption(options, "Show all " + names, this::all);
    }
}
