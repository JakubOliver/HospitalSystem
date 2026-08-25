package cz.cuni.kubinja.hospitalsystem.core.personnel;

import cz.cuni.kubinja.hospitalsystem.core.personnel.util.PersonData;
import cz.cuni.kubinja.hospitalsystem.core.export.Exportable;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents generic person in hospital system.
 */
public class Person implements Exportable {
    private final int id;
    private final String firstName;
    private final String lastName;
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
        this.id = id;
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
     * Creates new Person as a copy of provided Person.
     * @param person Person that the constructor should copy.
     */
    public Person(Person person){
        this.id = person.id;
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.dateOfBirth = person.dateOfBirth;
    }

    /**
     * Creates person based on id and data wrapper for rest of needed parameters.
     *
     * @param id Identification number of the person.
     * @param personData Data wrapper for the data needed for person creation.
     */
    public Person(int id, PersonData personData){
        this(
            id,
            personData.firstName(),
            personData.lastName(),
            personData.dateOfBirth()
        );
    }

    /**
     * Provides unique identification number of person
     * @return Unique identification number
     */
    public int getId() { return id; }

    /**
     * Returns firstname of person.
     * @return firstname of person.
     */
    public String getFirstName() { return firstName; }

    /**
     * Returns lastname of person.
     * @return lastname of person.
     */
    public String getLastName() { return lastName; }

    /**
     * Returns date of birth of person
     * @return LocalDate object representing date of birth of person
     */
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    /**
     * Returns age of the person.
     * @return age of the person.
     */
    public int getAge() { return Period.between(dateOfBirth, LocalDate.now()).getYears(); }

    /**
     * Returns custom identifier for Person class.
     * @return custom identifier for Person class.
     */
    public static String getClassIdentifier() { return "person"; }

    @Override
    public String toString() {
        return "id: " + id + ", firstname: " + firstName + ", lastname: " + lastName + ", age: " + getAge();
    }

    @Override
    public String export(){
        return id + "," + firstName + "," + lastName + "," + getAge();
    }
}
