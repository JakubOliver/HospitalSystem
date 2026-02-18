import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Tests {
    public Scanner getPrebuildInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        return new Scanner(System.in);
    }

    @BeforeAll
    static void silence() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
    }
}
