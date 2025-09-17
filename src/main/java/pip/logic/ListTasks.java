package pip.logic;

import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Displays the current task list to the user.
 * */
public class ListTasks extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show(tasks.render());
    }
}
