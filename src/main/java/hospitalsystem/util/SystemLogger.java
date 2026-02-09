package hospitalsystem.util;

import hospitalsystem.personnel.Patient;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemLogger {
    private static final Logger logger = Logger.getLogger("Hospital System");

    public static void successfullNewPatient(Patient patient){
        logger.log(Level.INFO, "New patient ({0}) successfully added to the database.", patient);
    }
}
