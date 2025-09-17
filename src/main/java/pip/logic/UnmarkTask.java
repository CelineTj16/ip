package pip.logic;

import pip.app.PipException;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Marks a single task as not completed, identified by a user-supplied index.
 * */
public class UnmarkTask extends Command {
    private final String args;

    public UnmarkTask(String args) { this.args = args; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
        int idx = Parser.parseIndex(args, tasks.size());
        Task t = tasks.get(idx);
        t.unmark();
        storage.save(tasks.asList());
        ui.show("OK, I've marked this task as not done yet:\n  " + t);
    }
}
