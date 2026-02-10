package hospitalsystem.database;

/**
 * Custom exception connected with the database queries errors tailored for the hospital system.
 */
public class DatabaseException extends Exception{
    public static final String patientInsertDatabaseError = "Unable to correctly add patient into database";
    public static final String doctorInsertDatabaseError = "Unable to correctly add doctor into database";
    public static final String appointmentInsertDatabaseError = "Unable to correctly add appointment into database";

    public static final String patientGetDatabaseError = "Unable to get patient from database";

    /**
     * Creates databaseException with the provided message.
     *
     * @param message Message of the databaseException.
     */
    public DatabaseException(String message) {
        super(message);
    }
}
