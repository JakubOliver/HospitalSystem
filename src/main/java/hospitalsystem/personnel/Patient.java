package hospitalsystem.personnel;

import hospitalsystem.personnel.util.PatientsDetails;

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

    /**
     * Creates patient based on provided Person and details needed for the patient.
     *
     * @param person Person for who we want to create patient object.
     * @param details Details that extends person.
     */
    public Patient(Person person, PatientsDetails details){
        super(person);

        this.anamnesis = details.anamnesis();
    }

    /**
     * Returns anamnesis of the patient.
     * @return anamnesis of the patient.
     */
    public String getAnamnesis() { return anamnesis; }

    /**
     * Returns custom patient class identifier.
     * @return custom patient class identifier.
     */
    public static String getClassIdentifier() { return "patient"; }

    @Override
    public String toString() {
        return super.toString() + ", anamnesis: " + anamnesis;
    }

    @Override
    public String export(){
        return super.export() + "," + anamnesis;
    }
}
