package hospitalsystem.personnel.util;

import java.time.LocalDate;

/**
 * Wrapper for data from which can be created person.
 *
 * @param firstName Firstname of the person.
 * @param lastName Lastname of the person.
 * @param dateOfBirth Date of birth of the person.
 */
public record PersonData(String firstName, String lastName, LocalDate dateOfBirth) { }
