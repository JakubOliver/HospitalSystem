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
    @Test
    void correctOption(){
        Scanner scanner = getPrebuildInput("-1 \n 1000 \n 2");

        assertEquals(2, MainMenu.getOption(scanner, 5));
    }

    @Test
    void correctParsingPerson(){
        Scanner scanner = getPrebuildInput("Tomas \n Novak \n 2001-01-01");

        assertEquals(new PersonData("Tomas", "Novak", LocalDate.of(2001,1,1)), MainMenu.getPersonData(scanner));
    }

    @Test
    void correctDateCheck(){
        String input = "2001-01-01 \n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);

        assertEquals(MainMenu.getDate(scanner, "Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckRejectIncorrect(){
        String input = "1999-45-34 \n 2001-01-01";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);

        assertEquals(MainMenu.getDate(scanner, "Date: "), LocalDate.of(2001, 1, 1));
    }

    @Test
    void DateCheckFailure(){
        String input = "this is not correct date";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);

        InputMismatchException ex = assertThrows(InputMismatchException.class, () -> MainMenu.getDate(scanner, "Date: "));

        assertEquals("Scanner run out of lines and did not found valid date.", ex.getMessage());
    }

    @Test
    void DateTimeCorrectCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01T01:01");

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), MainMenu.getDateTime(scanner, "Date: "));
    }

    @Test
    void DateTimeAlternativeCorrectCheck(){
        Scanner scanner = getPrebuildInput("2001-01-01 01:01");

        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1), MainMenu.getDateTime(scanner, "Date: "));
    }
}
