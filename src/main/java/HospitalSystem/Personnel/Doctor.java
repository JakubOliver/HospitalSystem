package HospitalSystem.Personnel;

import java.time.LocalDate;

public class Doctor extends Person{
    public Doctor(int id, String firstName, String lastName, LocalDate dateOfBirth) {
        super(id, firstName, lastName, dateOfBirth);
    }
}
