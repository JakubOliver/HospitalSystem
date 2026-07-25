/**
 * Provides the command-line user interface for the hospital system.
 */
module TUI {
    requires core;
    //requires test.support;
    requires menu;
    requires org.jetbrains.annotations;

    exports cz.cuni.kubinja.hospitalsystem.TUI;
}
