/**
 * Provides hospital data management, persistence, scheduling, exports and statistics.
 */
module core {
    requires org.xerial.sqlitejdbc;
    //requires test.support;

    exports cz.cuni.kubinja.hospitalsystem.core;
    exports cz.cuni.kubinja.hospitalsystem.core.database.exceptions;

    exports cz.cuni.kubinja.hospitalsystem.core.personnel;
    exports cz.cuni.kubinja.hospitalsystem.core.personnel.util;

    exports cz.cuni.kubinja.hospitalsystem.core.calendar;

    exports cz.cuni.kubinja.hospitalsystem.core.packet;
    exports cz.cuni.kubinja.hospitalsystem.core.statistics;

    exports cz.cuni.kubinja.hospitalsystem.core.util; //TODO: better
}
