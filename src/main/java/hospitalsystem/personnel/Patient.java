package hospitalsystem.personnel;

import java.time.LocalDate;

/**
 * Represents a patient in the hospital system.
 */
public class Patient extends Person{
    private String anamnesis;

    /**
     * Creates patient based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of patient
     * @param lastName last name of patient
     * @param dateOfBirth date of birth of patient
     * @param anamnesis anamnesis of patient
     */
    public Patient(int id, String firstName, String lastName, LocalDate dateOfBirth, String anamnesis) {
        super(id, firstName, lastName, dateOfBirth);

        this.anamnesis = anamnesis;
    }

    /**
     * Creates patient based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of person
     * @param lastName last name of person
     * @param day day of birth
     * @param month month of birth
     * @param year year of birth
     * @param anamnesis anamnesis of patient
     */
    public Patient(int id, String firstName, String lastName, int day, int month, int year, String anamnesis) {
        super(id, firstName, lastName, day, month, year);

        this.anamnesis = anamnesis;
    }

    /** Returns anamnesis of patient */
    public String getAnamnesis() { return anamnesis; }

    @Override
    public String toString() {
        return super.toString() + ", anamnesis: " + anamnesis;
    }
}
