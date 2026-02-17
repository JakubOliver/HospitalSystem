import hospitalsystem.Hospital;
import hospitalsystem.UI.PatientMenu;
import hospitalsystem.personnel.util.PersonData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.UI.MainMenu;

public class InputTests extends Tests{
    Hospital hospital = new Hospital("jdbc:sqlite:memory:");

    @Test
    void correctOption(){
        Scanner scanner = getPrebuildInput("-1 \n 1000 \n 2");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(2, menu.getOption(5));
    }

    @Test
    void correctParsingPerson(){
        Scanner scanner = getPrebuildInput("Tomas \n Novak \n 2001-01-01");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(new PersonData("Tomas", "Novak", LocalDate.of(2001,1,1)), menu.getPersonData());
    }

    @Test
    void correctDateCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01 \n");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(menu.getDate("Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckRejectIncorrect(){
        Scanner scanner = getPrebuildInput("1999-45-34 \n 2001-01-01");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(menu.getDate("Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckFailure(){
        Scanner scanner = getPrebuildInput("this is not correct date");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        InputMismatchException ex = assertThrows(InputMismatchException.class, () -> menu.getDate("Date: "));

        assertEquals("Scanner run out of lines and did not found valid date.", ex.getMessage());
    }

    @Test
    void DateTimeCorrectCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01T01:01");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), menu.getDateTime("Date: "));
    }

    @Test
    void DateTimeAlternativeCorrectCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01 01:01");
        PatientMenu menu = new PatientMenu(hospital, scanner);

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), menu.getDateTime("Date: "));
    }
}
