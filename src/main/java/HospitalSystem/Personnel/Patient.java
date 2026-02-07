package HospitalSystem.Personnel;

import java.time.LocalDate;

public class Patient extends Person{
    public Patient(int id, String firstName, String lastName, LocalDate dateOfBirth) {
        super(id, firstName, lastName, dateOfBirth);
    }
}
