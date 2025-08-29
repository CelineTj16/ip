package pip;

import pip.ui.Ui;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UiTest {

    private static final String LINE = "    ____________________________________________________________";

    private ByteArrayOutputStream out;
    private PrintStream originalOut;
    private Ui ui;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
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
    void showWelcome_printsGreetingWrappedByDividers() {
        ui.showWelcome();
        String expected = LINE + "\n"
                + "     Hi! I'm Pip :))\n"
                + "     What can I do for you?\n"
                + LINE + "\n";
        assertEquals(expected, grab());
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
