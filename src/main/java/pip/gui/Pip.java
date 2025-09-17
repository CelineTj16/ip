package pip.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import pip.app.PipException;
import pip.logic.Command;
import pip.logic.Parser;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * GUI adapter for Pip: runs commands and returns the Ui output as text.
 */
public class Pip {
    private static final String DEFAULT_SAVE_PATH = "data/pip.txt";
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private boolean exitRequested = false;

    public Pip() {
        this(DEFAULT_SAVE_PATH);
    }

    /** Bridges Pip's core logic to the JavaFX UI.*/
    public Pip(String filePath) {
        PrintStream capture = new PrintStream(buffer, true);
        this.ui = new Ui(capture);
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (PipException e) {
            ui.showLoadingError();
            this.tasks = new TaskList();
        }

        ui.showWelcome();
    }

    /** If you want to show the greeting at app start, call this once from MainWindow.initialize(). */
    public String getStartupGreeting() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /** Called by the GUI for each user message; returns what Ui printed. */
    public String getResponse(String input) {
        buffer.reset();
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            exitRequested = c.isExit();

            if (exitRequested) {
                Platform.runLater(Platform::exit);
            }
        } catch (PipException e) {
            ui.showError(e.getMessage());
        }
        return buffer.toString(StandardCharsets.UTF_8).trim();
    }

    public boolean shouldExit() {
        return exitRequested;
    }
}
