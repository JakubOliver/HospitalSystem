package cz.cuni.kubinja.hospitalsystem.core.personnel.util;

/**
 * Wrapper for data from which can be created patient.
 *
 * @param person Person data that describe possible patient.
 * @param details Patient details that describe possible patient.
 */
public record PatientData(PersonData person, PatientsDetails details){}