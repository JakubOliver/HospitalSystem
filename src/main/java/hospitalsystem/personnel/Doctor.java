package hospitalsystem.personnel;

import java.time.LocalDate;

/**
 * Represents a doctor in the hospital system.
 */
public class Doctor extends Person{
    private String specialization;

    /**
     * Creates doctor based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of doctor
     * @param lastName last name of doctor
     * @param dateOfBirth date of birth of doctor
     * @param specialization specialization of doctor
     */
    public Doctor(int id, String firstName, String lastName, LocalDate dateOfBirth,  String specialization) {
        super(id, firstName, lastName, dateOfBirth);

        this.specialization = specialization;
    }

    /**
     * Creates doctor based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of doctor
     * @param lastName last name of doctor
     * @param day day of birth
     * @param month month of birth
     * @param year year of birth
     * @param specialization specialization of doctor
     */
    public Doctor(int id, String firstName, String lastName, int day, int month, int year, String specialization) {
        super(id, firstName, lastName, day, month, year);

        this.specialization = specialization;
    }

    /** Returns specialization of doctor */
    public String getSpecialization() { return  specialization; }

    @Override
    public String toString() {
        return super.toString() + ", specialization: " + specialization;
    }
}
