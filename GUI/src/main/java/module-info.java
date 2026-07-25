/**
 * Provides the JavaFX graphical user interface for the hospital system.
 */
module GUI {
    requires com.calendarfx.view;
    requires core;
    requires menu;
    requires transitive javafx.controls;

    exports cz.cuni.kubinja.hospitalsystem.GUI;
}
