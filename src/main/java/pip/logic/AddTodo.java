package pip.logic;

import pip.app.PipException;
import pip.model.TaskList;
import pip.model.Todo;
import pip.storage.Storage;
import pip.ui.Ui;

/** Command that adds a new Todo task to the list. */
public class AddTodo extends Command {
    private final String desc;

    public AddTodo(String args) {
        this.desc = (args == null ? "" : args).trim();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
        String d = requireNonEmpty(desc, MSG_EMPTY_TODO);
        addAndPersist(new Todo(d), tasks, storage, ui);
    }
}
