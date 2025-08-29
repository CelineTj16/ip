package pip.app;

import pip.logic.Command;
import pip.logic.Parser;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

import java.util.Scanner;

public class Pip {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    public Pip(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);   // <— Storage becomes instance-based
        try {
            this.tasks = new TaskList(storage.load());
        } catch (PipException e) {
            ui.showLoadingError();
            this.tasks = new TaskList();
        }
    }

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
