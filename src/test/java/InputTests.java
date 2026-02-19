import hospitalsystem.Hospital;
import hospitalsystem.UI.PatientMenu;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PersonData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InputTests extends Tests{
    Hospital hospital = new Hospital("jdbc:sqlite:memory:");

    @Test
    void correctOption(){
        Scanner scanner = getPrebuildInput("-1 \n 1000 \n 2");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(1, menu.getOption(5));
    }

    @Test
    void correctParsingPerson(){
        Scanner scanner = getPrebuildInput("Tomas \n Novak \n 2001-01-01");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(new PersonData("Tomas", "Novak", LocalDate.of(2001,1,1)), menu.getPersonData());
    }

    @Test
    void correctDateCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01 \n");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(menu.getDate("Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckRejectIncorrect(){
        Scanner scanner = getPrebuildInput("1999-45-34 \n 2001-01-01");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(menu.getDate("Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckFailure(){
        Scanner scanner = getPrebuildInput("this is not correct date");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        InputMismatchException ex = assertThrows(InputMismatchException.class, () -> menu.getDate("Date: "));

        assertEquals("Scanner run out of lines and no correct string found.", ex.getMessage());
    }

    @Test
    void DateTimeCorrectCheck(){
        Scanner scanner = getPrebuildInput("    \n \n \n          2001-01-01T01:01   ");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), menu.getDateTime("Date: "));
    }

    @Test
    void DateTimeAlternativeCorrectCheck(){
        Scanner scanner = getPrebuildInput("        2001-01-01 01:01            ");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), menu.getDateTime("Date: "));
    }

    @Test
    void patientsDetails(){
        Scanner scanner = getPrebuildInput("   broken leg           ");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals("broken leg", menu.getPatientDetails().anamnesis());
    }

    @Test
    void patientsDetailsWithDefaultValues(){
        Scanner scanner = getPrebuildInput("\n");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals("Broken leg", menu.getPatientDetails(new Patient(1, "Pepa", "Novak", LocalDate.parse("2001-01-01"), "Broken leg")).anamnesis());
    }

    @Test
    void doctorsDetails(){
        Scanner scanner = getPrebuildInput("     surgeon                  \n  \n    \n          neurology      ");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        DoctorDetails details = menu.getDoctorDetails();

        assertEquals("surgeon", details.specialization());
        assertEquals("neurology", details.department());
    }

    @Test
    void doctorsDetailsWithDefaultValues(){
        Scanner scanner = getPrebuildInput("    \n             \n");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        Doctor doctor = new Doctor(1, "Pepa", "Novak", LocalDate.of(2001, 1, 1), "surgeon", "neurology");
        DoctorDetails details = menu.getDoctorDetails(doctor);

        assertEquals("surgeon", details.specialization());
        assertEquals("neurology", details.department());
    }

    @Test
    void createNewQuestion(){
        Scanner scanner = getPrebuildInput("asi ne   \n    nevim   \n     No");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertFalse(menu.createNew("Dummy question"));
    }

    @Test
    void createNewQuestion2(){
        Scanner scanner = getPrebuildInput("nejspi jo \n   mozna  \n     yes \n   fdafasfa \n");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertTrue(menu.createNew("Dummy question"));
    }

    @Test
    void personData(){
        Scanner scanner = getPrebuildInput("  Pepa    \n    Novak         \n          2001-01-01          ");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        Person person = new Person(1, "Pepa", "Novak", LocalDate.of(2001, 1, 1));
        PersonData data =  menu.getPersonData(person);

        assertEquals(person.toString(), new Person(1, data).toString());
    }

    @Test
    void getInteger(){
        Scanner scanner = getPrebuildInput("   \n fdafa \n dfsafas2fd afa \n     0xg5 \n    100   \n dafasfa");
        PatientMenu menu = new PatientMenu(hospital, scanner, true);

        assertEquals(100, menu.getInteger(menu.getQuestion("Cislo?:")));
    }
}
