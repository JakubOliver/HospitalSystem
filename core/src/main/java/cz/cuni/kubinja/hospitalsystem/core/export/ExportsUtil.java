package cz.cuni.kubinja.hospitalsystem.core.export;

import java.time.LocalDate;

/**
 * Contains contains connected with exporting data into files.
 */
public final class ExportsUtil {
    /** Location of the exports directory */
    public static String exportDirectoryDestination = "exports";
    /** Location of the file where will be exported patients data */
    public static String patientExportDestination = "patient.csv";
    /** Location of the file where will be exported doctor data */
    public static String doctorExportDestination = "doctor.csv";
    /** Location of the file where will be exported appointment data */
    public static String appointmentExportDestination = "appointment.csv";

    /** Exception message which denotes that while exporting was unsuccessfully created export directory */
    public static String unableToPrepareExportsDirectoryErrMsg = "Unable to create directory for exports";

    /**
     * Private constructor of export util class. (This class should not be instantiable, therefore is default constructor private/unreachable)
     */
    private ExportsUtil(){}

    /**
     * Returns name of the file with export date prefix.
     *
     * @param name Base name of the file.
     * @return Name of the file with export date prefix.
     */
    public static String getExportFileName(String name){
        return LocalDate.now() + "-" + name;
    }
}
