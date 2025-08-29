package pip.app;

import pip.logic.Command;
import pip.logic.Parser;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

import java.util.Scanner;

/**
 * Entry point for the Pip CLI task manager.
 */
public class Pip {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a Pip application bound to a specific save file.
     * Attempts to load previously saved tasks; on failure, shows a warning and
     * starts with an empty list.
     *
     * @param filePath Path to the data file.
     */
    public Pip(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (PipException e) {
            ui.showLoadingError();
            this.tasks = new TaskList();
        }
    }

    /** Runs the interactive command loop until {@code bye} is issued */
    public void run() {
        ui.showWelcome();
        Scanner sc = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand(sc);
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (PipException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        sc.close();
    }

    public static void main(String[] args) {
        new Pip("data/pip.txt").run();
    }
}
