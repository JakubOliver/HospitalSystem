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

    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void silence() {
        originalOut = System.out;
        originalErr = System.err;

        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
    }
}
