package pip.logic;

import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Exits the application after displaying a farewell message.
 */
public class ExitApp extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show("Bye. Hope to see you again soon!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
