package cz.cuni.kubinja.hospitalsystem.testing;

import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class Tests {
    public static String testDB = "jdbc:sqlite:memory";

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
