package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.MenuPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonKinds;
import javafx.scene.layout.GridPane;

/**
 * Menu containing appointment and calendar actions.
 */
public class AppointmentMenu extends MenuPage {
    private final Hospital hospital;

    /**
     * Creates appointment menu page.
     *
     * @param navigator 2-way navigation between pages.
     * @param hospital Hospital instance.
     */
    public AppointmentMenu(Navigator navigator, Hospital hospital) {
        super(navigator);
        this.hospital = hospital;
    }

    @Override
    public String getTitle() {
        return "Calendar";
    }

    @Override
    protected void addOptions(GridPane options) {
        addOption(
            options,
            "Add new appointment",
            () -> navigator.navigate(new AddAppointmentPage(navigator, hospital))
        );
        addOption(
            options,
            "Edit appointment",
            () -> navigator.navigate(new EditAppointmentPage(navigator, hospital))
        );
        addOption(
            options,
            "Delete appointment",
            () -> navigator.navigate(new DeleteAppointmentPage(navigator, hospital))
        );
        addOption(
            options,
            "Show all appointments for patient",
            () -> navigator.navigate(new PersonnelAppointmentsPage(
                navigator,
                hospital,
                PersonKinds.Patient
            ))
        );
        addOption(
            options,
            "Show all appointments for doctor",
            () -> navigator.navigate(new PersonnelAppointmentsPage(
                navigator,
                hospital,
                PersonKinds.Doctor
            ))
        );
        addOption(
            options,
            "Show calendar for department",
            () -> navigator.navigate(new CalendarPage(
                navigator,
                hospital,
                true
            ))
        );
        addOption(
            options,
            "Show calendar",
            () -> navigator.navigate(new CalendarPage(
                navigator,
                hospital,
                false
            ))
        );
    }
}
