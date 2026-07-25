package cz.cuni.kubinja.hospitalsystem.GUI;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.MenuPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment.AppointmentMenu;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor.DoctorMenu;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.export.ExportMenu;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.patient.PatientMenu;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.statistics.ReportsMenu;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import javafx.scene.layout.GridPane;

/**
 * Main crossroad to all GUI sections.
 */
public class MainMenu extends MenuPage {
    private final Hospital hospital;
    protected static String PAGE_TITLE_STYLE =
            "-fx-font-size: 40px; " + EMPHASIZED_TEXT_STYLE;

    /**
     * Creates a new MainMenu instance.
     *
     * @param navigator 2-way navigation between pages.
     * @param hospital Hospital instance.
     */
    public MainMenu(Navigator navigator, Hospital hospital) {
        super(navigator);
        this.hospital = hospital;
    }

    @Override
    protected String getTitleStyle(){
        return PAGE_TITLE_STYLE;
    }

    @Override
    public String getTitle() {
        return "Hospital System";
    }

    @Override
    protected void addOptions(GridPane options) {
        addOption(options, "Patients", () -> navigator.navigate(new PatientMenu(navigator, hospital)));
        addOption(options, "Doctors", () -> navigator.navigate(new DoctorMenu(navigator, hospital)));
        addOption(options, "Calendar", () -> navigator.navigate(new AppointmentMenu(navigator, hospital)));
        addOption(options, "Export", () -> navigator.navigate(new ExportMenu(navigator, hospital)));
        addOption(options, "Statistics", () -> navigator.navigate(new ReportsMenu(navigator, hospital)));
    }

    @Override
    protected String getFooterButtonText() {
        return "End";
    }

    @Override
    protected Runnable getFooterButtonAction() {
        return navigator::close;
    }
}
