package cz.cuni.kubinja.hospitalsystem.personnel.util;

/**
 * Wrapper for data which extends person into doctor.
 *
 * @param specialization Specialization of the doctor.
 * @param department Name of department in which is doctor working.
 */
public record DoctorDetails(String specialization, String department) { }