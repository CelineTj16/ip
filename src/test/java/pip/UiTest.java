package pip;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pip.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UiTest {

    private static final String LINE = "    ____________________________________________________________";

    private ByteArrayOutputStream out;
    private Ui ui;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out, true, StandardCharsets.UTF_8);
        ui = new Ui(ps); // inject stream; don't touch System.out
    }

    private String grab() {
        String s = new String(out.toByteArray(), StandardCharsets.UTF_8);
        out.reset();
        return s.replace("\r\n", "\n");
    }

    @Test
    void showLine_printsDivider() {
        ui.showLine();
        assertEquals(LINE + "\n", grab());
    }

    @Test
    void showWelcome_printsSingleLineMessage() {
        ui.showWelcome();
        assertEquals("Hi! I'm Pip :)) What can I do for you?\n", grab());
    }

    @Test
    void show_printsEachLineWithIndent() {
        ui.show("hello\nworld");
        String expected = "     hello\n" + "     world\n";
        assertEquals(expected, grab());
    }

    @Test
    void show_singleLineStillIndented() {
        ui.show("only one line");
        assertEquals("     only one line\n", grab());
    }

    @Test
    void showError_printsIndentedMessage() {
        ui.showError("Oops!");
        assertEquals("     Oops!\n", grab());
    }

    @Test
    void showLoadingError_printsWarning() {
        ui.showLoadingError();
        assertEquals("     Warning: could not load save file. Starting with an empty list.\n", grab());
    }

    @Test
    void readCommand_readsNextLineFromScanner() {
        Scanner sc = new Scanner("first line\nsecond line\n");
        assertEquals("first line", ui.readCommand(sc));
        assertEquals("second line", ui.readCommand(sc));
    }
}
