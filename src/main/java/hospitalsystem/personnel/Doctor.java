package hospitalsystem.personnel;

import hospitalsystem.personnel.util.DoctorDetails;

import java.time.LocalDate;

/**
 * Represents a doctor in the hospital system.
 */
public class Doctor extends Person{
    private String specialization;
    private String department;

    /**
     * Creates doctor based on provided parameters.
     *
     * @param id unique identification number
     * @param firstName first name of doctor
     * @param lastName last name of doctor
     * @param dateOfBirth date of birth of doctor
     * @param specialization specialization of doctor
     * @param department department in which the doctor works
     */
    public Doctor(int id, String firstName, String lastName, LocalDate dateOfBirth,  String specialization, String department) {
        super(id, firstName, lastName, dateOfBirth);

        this.specialization = specialization;
        this.department = department;
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
     * @param department department in which the doctor works
     */
    public Doctor(int id, String firstName, String lastName, int day, int month, int year, String specialization, String department) {
        super(id, firstName, lastName, day, month, year);

        this.specialization = specialization;
        this.department = department;
    }

    /**
     * Creates doctor based on provided parameters.
     *
     * @param person Person for who we want to create patient object.
     * @param details Details that extends person.
     */
    public Doctor(Person person, DoctorDetails details){
        super(person);

        this.specialization = details.specialization();
        this.department = details.department();
    }

    /**
     * Returns specialization of the doctor.
     * @return specialization of the doctor.
     */
    public String getSpecialization() { return  specialization; }

    public String getDepartment() { return department; }

    /**
     * Returns custom doctor class identifier.
     * @return custom doctor class identifier.
     */
    public static String getClassIdentifier() { return "doctor"; }

    @Override
    public String toString() {
        return super.toString() + ", specialization: " + specialization;
    }
}
