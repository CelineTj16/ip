package pip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pip.app.PipException;
import pip.logic.Command;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

class CommandTest {

    /** In-memory Storage double to avoid filesystem I/O. */
    static class FakeStorage extends Storage {
        private List<Task> lastSaved;

        FakeStorage() {
            super("ignored");
        }

        @Override
        public List<Task> load() {
            return new ArrayList<>();
        }

        @Override
        public void save(List<Task> items) {
            this.lastSaved = new ArrayList<>(items);
        }

        List<Task> getLastSaved() {
            return lastSaved;
        }
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
    void addDeadline_parsesFormatsAndSaves() throws PipException {
        new Command.AddDeadline("return book /by 2/12/2019 1800")
                .execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertNotNull(storage.getLastSaved());
        assertEquals(1, storage.getLastSaved().size());
        assertTrue(tasks.get(0).toDataString().endsWith("2019-12-02T18:00"));

        String printed = grabOut();
        assertTrue(printed.contains("Got it. I've added this task:"));
        assertTrue(printed.contains("Now you have 1 tasks"));
    }

    @Test
    void addTodo_emptyThrows() {
        PipException ex = assertThrows(
                PipException.class, () -> new Command.AddTodo("   ").execute(tasks, ui, storage)
        );
        assertEquals("The description of a todo cannot be empty :((", ex.getMessage());
    }

    @Test
    void addEvent_missingFromOrToThrows() {
        PipException ex = assertThrows(
                PipException.class, () -> new Command.AddEvent("camp only has from /from mon")
                        .execute(tasks, ui, storage)
        );
        assertEquals("Usage: event <desc> /from <start> /to <end>", ex.getMessage());
    }

    @Test
    void delete_validIndexRemovesAndSaves() throws PipException {
        new Command.AddTodo("a").execute(tasks, ui, storage);
        new Command.AddTodo("b").execute(tasks, ui, storage);
        grabOut();

        new Command.Delete("1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertNotNull(storage.getLastSaved());
        assertEquals(1, storage.getLastSaved().size());

        String printed = grabOut();
        assertTrue(printed.contains("Noted. I've removed this task:"));
    }

    @Test
    void mark_thenUnmarkTogglesAndSaves() throws PipException {
        new Command.AddTodo("task").execute(tasks, ui, storage);
        grabOut();

        new Command.Mark("1").execute(tasks, ui, storage);
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertNotNull(storage.getLastSaved());

        new Command.Unmark("1").execute(tasks, ui, storage);
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertNotNull(storage.getLastSaved());

        String printed = grabOut();
        assertTrue(
                printed.contains("marked this task as done")
                        || printed.contains("marked this task as not done yet")
        );
    }
}
