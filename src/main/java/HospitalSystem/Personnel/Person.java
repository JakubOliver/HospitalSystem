package HospitalSystem.Personnel;

import java.time.LocalDate;
import java.time.Period;

public class Person {
    private final int id;
    private String firstName;
    private String lastName;
    private final LocalDate dateOfBirth;

    public Person(int id, String firstName, String lastName, LocalDate dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public int getAge() { return Period.between(LocalDate.now(), dateOfBirth).getYears(); }
}
