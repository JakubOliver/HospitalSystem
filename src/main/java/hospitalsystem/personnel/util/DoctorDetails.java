package hospitalsystem.personnel.util;

/**
 * Wrapper for data which extends person into doctor.
 *
 * @param specialization Specialization of the doctor.
 */
public record DoctorDetails(String specialization, String department) { }