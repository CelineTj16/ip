package pip.ui;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * UI helper that prints Pip's messages and (optionally) reads user input.
 * Now stream-based so it works for both CLI (System.out) and GUI (captured stream).
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private static final String INDENT = "     ";

    private final PrintStream out;

    /** Default: print to System.out (CLI). */
    public Ui() {
        this(System.out);
    }

    /**
     * Print to the provided stream (e.g., a ByteArrayOutputStream-wrapped PrintStream for GUI).
     */
    public Ui(PrintStream out) {
        this.out = out;
    }

    /** Prints a horizontal divider line. */
    public void showLine() {
        out.println(LINE);
    }

    /** Prints the welcome banner surrounded by divider lines. */
    public void showWelcome() {
        out.println("Hi! I'm Pip :)) What can I do for you?");
    }

    /**
     * Reads a single command line from the given scanner (used by CLI mode).
     *
     * @param sc Scanner bound to System.in.
     * @return The next line entered by the user (without the trailing newline).
     */
    public String readCommand(Scanner sc) {
        assert sc != null : "Scanner must not be null";
        return sc.nextLine();
    }

    /**
     * Prints the given text, indenting each line consistently.
     *
     * @param text Text to print; may contain embedded newlines.
     */
    public void show(String text) {
        if (text == null) {
            return;
        }

        for (String line : text.split("\\R", -1)) {
            out.println(INDENT + line);
        }
    }

    /**
     * Prints an error message with standard indentation.
     *
     * @param msg Error message to display.
     */
    public void showError(String msg) {
        out.println(INDENT + msg);
    }

    /** Prints a non-fatal loading warning and continues with an empty task list. */
    public void showLoadingError() {
        out.println(INDENT + "Warning: could not load save file. Starting with an empty list.");
    }
}
