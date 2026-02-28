package cz.cuni.kubinja.hospitalsystem.database;

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

    /** Failure of querying person from database error message */
    public static final String personGetDatabaseError = "Unable to get person from database";
    /** Failure of querying patients from database error message */
    public static final String patientGetDatabaseError = "Unable to get patient from database";
    /** Failure of querying doctor from database error message */
    public static final String doctorGetDatabaseError = "Unable to get doctor from database";
    /** Failure of querying appointments from database error message */
    public static final String appointmentGetDatabaseError = "Unable to get appointment from database";

    /** Failure to update patient data to the database error message */
    public static final String patientUpdateDatabaseError = "Unable to update patient in database";
    /** Failure to update doctor data to the database error message */
    public static final String doctorUpdateDatabaseError = "Unable to update doctor in database";
    /** Failure to update appointments data to the database error message */
    public static final String appointmentUpdateDatabaseError = "Unable to update appointment in database";

    /** Failure to delete patient from the database error message */
    public static final String patientDeleteDatabaseError = "Unable to delete patient from database";
    /** Failure to delete doctor from the database error message */
    public static final String doctorDeleteDatabaseError = "Unable to delete doctor from database";
    /** Failure to delete appointment from the database error message */
    public static final String appointmentDeleteDatabaseError = "Unable to delete appointment from database";

    /** Fails to delete data from database error message */
    public static final String generalDeleteDatabaseError = "Unable to delete data from database";

    /** Type of person that is retrieved from database by id is not same as expected type */
    public static final String invalidTypeOfPersonDatabaseError = "Person with id {0} is not {1}";

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
