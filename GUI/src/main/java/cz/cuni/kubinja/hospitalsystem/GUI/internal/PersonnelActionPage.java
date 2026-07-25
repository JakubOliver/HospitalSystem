package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;

import java.util.Locale;

/**
 * Common naming and dependencies for personnel action workflows.
 *
 * @param <T> Type of personnel managed by the page.
 */
public abstract class PersonnelActionPage<T extends Person> extends ActionPage {
    private final String personnelName;

    protected PersonnelActionPage(
            Navigator navigator,
            Hospital hospital,
            String personnelName
    ) {
        super(navigator, hospital);
        this.personnelName = personnelName;
    }

    protected final String personnelName() {
        return personnelName;
    }

    protected final String personnelNameLowerCase() {
        return personnelName.toLowerCase(Locale.ROOT);
    }
}
