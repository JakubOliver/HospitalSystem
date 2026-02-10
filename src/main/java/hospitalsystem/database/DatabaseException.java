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
    /** Failure of querying doctor from database error message */
    public static final String doctorGetDatabaseError = "Unable to get doctor from database";

    /**
     * Creates databaseException with the provided message.
     *
     * @param message Message of the databaseException.
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creates databaseException with the provided messages. (Concatenates messages into one)
     *
     * @param message1 First part of the message.
     * @param message2 Second part of the message.
     */
    public DatabaseException(String message1, String message2) {
        super(message1 + ": " + message2);
    }

    /**
     * Creates databaseException with the provided message parts. (Concatenates parts into one message)
     *
     * @param parts Parts of the message.
     */
    public DatabaseException(String... parts){
        super(String.join(", ", parts));
    }
}
