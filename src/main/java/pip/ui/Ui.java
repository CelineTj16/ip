package pip.ui;

/**
 * Console UI helper that prints Pip's messages and reads user input.
 * Centralizes formatting/indentation to keep output consistent.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";

    /** Prints a horizontal divider line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints the welcome banner surrounded by divider lines. */
    public void showWelcome() {
        showLine();
        System.out.println("     Hi! I'm Pip :))");
        System.out.println("     What can I do for you?");
        showLine();
    }

    /**
     * Reads a single command line from the given scanner.
     *
     * @param sc Scanner bound to System.in.
     * @return The next line entered by the user (without the trailing newline).
     */
    public String readCommand(java.util.Scanner sc) {
        return sc.nextLine();
    }

    /**
     * Prints the given text, indenting each line consistently.
     *
     * @param text Text to print; may contain embedded newlines.
     */
    public void show(String text) {
        for (String line : text.split("\n")) System.out.println("     " + line);
    }

    /**
     * Prints an error message with standard indentation.
     *
     * @param msg Error message to display.
     */
    public void showError(String msg) {
        System.out.println("     " + msg);
    }

    /** Prints a non-fatal loading warning and continues with an empty task list. */
    public void showLoadingError() {
        System.out.println("     Warning: could not load save file. Starting with an empty list.");
    }
}
