package cz.cuni.kubinja.hospitalsystem.menu;

import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Presentation-independent validation rules shared by the TUI and GUI.
 */
public final class InputValidator {
    private InputValidator() {}

    /**
     * Checks whether a value contains non-whitespace text.
     *
     * @param value Value to check.
     * @return Whether the value contains text.
     */
    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Checks whether a value can be parsed as an integer.
     *
     * @param value Value to check.
     * @return Whether the value is an integer.
     */
    public static boolean isInteger(String value) {
        if (!hasText(value)) {
            return false;
        }

        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Checks whether a value is a positive integer.
     *
     * @param value Value to check.
     * @return Whether the value is a positive integer.
     */
    public static boolean isPositiveInteger(String value) {
        return isInteger(value) && parseInteger(value) > 0;
    }

    /**
     * Parses an integer previously checked by this validator.
     *
     * @param value Value to parse.
     * @return Parsed integer.
     * @throws NumberFormatException If the value is not an integer.
     */
    public static int parseInteger(String value) {
        return Integer.parseInt(value.trim());
    }

    /**
     * Checks whether a date is valid for personnel data.
     *
     * @param date Date to check.
     * @return Whether the date is after 1900-01-01 and not in the future.
     */
    public static boolean isValidPersonnelDate(LocalDate date) {
        return date != null && Calendar.isWithinValidDates(date);
    }

    /**
     * Checks whether a date and time is in the supported appointment range.
     *
     * @param dateTime Date and time to check.
     * @return Whether the date and time is supported for appointments.
     */
    public static boolean isValidAppointmentDateTime(LocalDateTime dateTime) {
        return dateTime != null && Calendar.isAppointmentWithinValidDateTime(dateTime);
    }
}
