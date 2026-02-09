package hospitalsystem.database;

public class DatabaseException extends Exception{
    public static final String patientDatabaseError = "Unable to correctly add patient into database";
    public static final String doctorDatabaseError = "Unable to correctly add doctor into database";
    public static final String appointmentDatabaseError = "Unable to correctly add appointment into database";

    public DatabaseException(String message) {
        super(message);
    }
}
