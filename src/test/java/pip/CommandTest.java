package pip;

import pip.logic.Command;
import org.junit.jupiter.api.*;
import pip.app.PipException;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    /** In-memory Storage double to avoid filesystem I/O. */
    static class FakeStorage extends Storage {
        List<Task> lastSaved;
        FakeStorage() { super("ignored"); }
        @Override public List<Task> load() { return new ArrayList<>(); }
        @Override public void save(List<Task> items) { lastSaved = new ArrayList<>(items); }
    }

    private ByteArrayOutputStream out;
    private PrintStream originalOut;
    private Ui ui;
    private TaskList tasks;
    private FakeStorage storage;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        ui = new Ui();
        tasks = new TaskList();
        storage = new FakeStorage();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String grabOut() {
        String s = new String(out.toByteArray(), StandardCharsets.UTF_8);
        out.reset();
        return s.replace("\r\n", "\n");
    }

    @Test
    void addDeadline_parsesFormats_saves_andPrints() throws PipException {
        new Command.AddDeadline("return book /by 2/12/2019 1800").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertNotNull(storage.lastSaved);
        assertEquals(1, storage.lastSaved.size());

        assertTrue(tasks.get(0).toDataString().endsWith("2019-12-02T18:00"));

        String printed = grabOut();
        assertTrue(printed.contains("Got it. I've added this task:"));
        assertTrue(printed.contains("Now you have 1 tasks"));
    }

    @Test
    void addTodo_empty_throws() {
        PipException ex = assertThrows(PipException.class,
                () -> new Command.AddTodo("   ").execute(tasks, ui, storage));
        assertEquals("The description of a todo cannot be empty :((", ex.getMessage());
    }

    @Test
    void addEvent_missingFromOrTo_throws() {
        PipException ex = assertThrows(PipException.class,
                () -> new Command.AddEvent("camp only has from /from mon").execute(tasks, ui, storage));
        assertEquals("Usage: event <desc> /from <start> /to <end>", ex.getMessage());
    }

    @Test
    void delete_validIndex_removes_andSaves() throws PipException {
        new Command.AddTodo("a").execute(tasks, ui, storage);
        new Command.AddTodo("b").execute(tasks, ui, storage);
        grabOut();

        new Command.Delete("1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertNotNull(storage.lastSaved);
        assertEquals(1, storage.lastSaved.size());

        String printed = grabOut();
        assertTrue(printed.contains("Noted. I've removed this task:"));
    }

    @Test
    void mark_thenUnmark_toggles_andSaves() throws PipException {
        new Command.AddTodo("task").execute(tasks, ui, storage);
        grabOut();

        new Command.Mark("1").execute(tasks, ui, storage);
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertNotNull(storage.lastSaved);

        new Command.Unmark("1").execute(tasks, ui, storage);
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertNotNull(storage.lastSaved);

        String printed = grabOut();
        assertTrue(printed.contains("marked this task as done")
                || printed.contains("marked this task as not done yet"));
    }
}
