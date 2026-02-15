package hospitalsystem.calendar;

public class CalendarException extends Exception {
    public static String invalidOrdering = "Time interval ends before staring";
    public static String toShort = "Time interval should have at least 60 minutes";
    public static String invalidAlignment = "Time interval should be staring and ending always at the half or full hour";
    public static String invalidTimes = "Time interval have to be between 8:00 and 16:00";
    public static String timeCollisionWithPatient = "In this time interval the patient has already some appointment";
    public static String timeCollisionWIthDoctor = "In this time interval the doctor has already some appointment";

    public CalendarException(String message) {
        super(message);
    }
}
