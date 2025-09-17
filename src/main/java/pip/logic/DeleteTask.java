package pip.logic;

import pip.app.PipException;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Deletes a single task identified by a user-supplied index.
 * */
public class DeleteTask extends Command {
    private final String args;

    public DeleteTask(String args) {
        this.args = args;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
        if (tasks.size() == 0) {
            throw new PipException(MSG_EMPTY_LIST);
        }
        int idx = Parser.parseIndex(args, tasks.size());
        Task removed = tasks.remove(idx);
        storage.save(tasks.asList());
        ui.show("Noted. I've removed this task:\n  " + removed
                + MSG_COUNT_PREFIX + tasks.size() + MSG_COUNT_SUFFIX);
    }
}
