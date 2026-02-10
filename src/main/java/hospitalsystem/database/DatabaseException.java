package hospitalsystem.database;

/**
 * Custom exception connected with the database queries errors tailored for the hospital system.
 */
public class DatabaseException extends Exception{
    /** Failure of inserting patient into database error message */
    public static final String patientInsertDatabaseError = "Unable to correctly add patient into database";
    /** Failure of inserting doctor into database error message */
    public static final String doctorInsertDatabaseError = "Unable to correctly add doctor into database";
    /** Failure of inserting appointment into database error message */
    public static final String appointmentInsertDatabaseError = "Unable to correctly add appointment into database";

    /** Failure of querying patients from database error message */
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
