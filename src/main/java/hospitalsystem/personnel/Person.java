package hospitalsystem.personnel;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents generic person in hospital system.
 */
abstract public class Person {
    private final int id;
    private String firstName;
    private String lastName;
    private final LocalDate dateOfBirth;

    /**
     * Creates person based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of person
     * @param lastName last name of person
     * @param dateOfBirth date of birth of person
     */
    public Person(int id, String firstName, String lastName, LocalDate dateOfBirth) {
        this.id = id; //TODO: changed to the system, that the ID is computed from database
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Creates person based on provided parameters.
     * @param id unique identification number
     * @param firstName first name of person
     * @param lastName last name of person
     * @param day day of birth
     * @param month month of birth
     * @param year year of birth
     */
    public Person(int id, String firstName, String lastName, int day,  int month, int year) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = LocalDate.of(year, month, day);
    }

    /**
     * Provides unique identification number of person
     * @return Unique identification number
     */
    public int getId() { return id; }

    /** Returns firstname of person */
    public String getFirstName() { return firstName; }

    /** Returns lastname of person */
    public String getLastName() { return lastName; }

    /**
     * Returns date of birth of person
     * @return LocalDate object representing date of birth of person
     */
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    /** Returns age of person */
    public int getAge() { return Period.between(dateOfBirth, LocalDate.now()).getYears(); }

    public static String getClassIdentifier() { return "person"; }

    @Override
    public String toString() {
        return "id: " + id + ", firstname: " + firstName + ", lastname: " + lastName + ", age: " + getAge();
    }
}
