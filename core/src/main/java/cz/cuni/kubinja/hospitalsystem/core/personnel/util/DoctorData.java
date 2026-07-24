package cz.cuni.kubinja.hospitalsystem.core.personnel.util;

/**
 * Wrapper for data from which can be created doctor.
 *
 * @param person Person data that describe possible doctor.
 * @param details Doctor details that describe possible doctor.
 */
public record DoctorData(PersonData person, DoctorDetails details) {}
