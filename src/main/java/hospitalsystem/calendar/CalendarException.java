package hospitalsystem.calendar;

/**
 * Exception connected with the calendar and appointments.
 */
public class CalendarException extends Exception {
    /** Appointment ends before starting error message */
    public static String invalidOrdering = "Time interval ends before staring";
    /** Appointment does not meet the requirement of minimal time error message */
    public static String toShort = "Time interval should have at least 60 minutes";
    /** Appointment doest not meet the requirement of starting and ending at half or whole hours error message */
    public static String invalidAlignment = "Time interval should be staring and ending always at the half or full hour";
    /** Appointment exceed the opening hours of hospital */
    public static String invalidTimes = "Time interval have to be between 8:00 and 16:00";
    /** Patient has already another appointment in this time error message */
    public static String timeCollisionWithPatient = "In this time interval the patient has already some appointment";
    /** Doctor has already another appointment in this time error message */
    public static String timeCollisionWIthDoctor = "In this time interval the doctor has already some appointment";
    /** Start and end time of appointment does not share same date */
    public static String notSameDay = "Duration of appointments takes place within multiple days.";

    /**
     * Creates new calendar exception with the provided message.
     *
     * @param message Message of exception.
     */
    public CalendarException(String message) {
        super(message);
    }
}
