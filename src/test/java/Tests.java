import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class Tests {
    public Scanner getPrebuildInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        return new Scanner(System.in);
    }

    @BeforeAll
    static void output() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
    }
}
