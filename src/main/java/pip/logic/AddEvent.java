package pip.logic;

import pip.app.PipException;
import pip.model.Event;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Command that adds a new Event parsed from
 * {@code <desc> /from <start> /to <end>}.
 */
public class AddEvent extends Command {
    private final String raw;

    public AddEvent(String args) {
        this.raw = args == null ? "" : args.trim();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
        if (!raw.contains(TOKEN_FROM) || !raw.contains(TOKEN_TO)) {
            throw new PipException(MSG_USAGE_EVENT);
        }

        // Split into desc | remainderAfterFrom
        String[] beforeFrom = splitOnce(raw, TOKEN_FROM);
        String desc = requireNonEmpty(beforeFrom[0], MSG_EMPTY_EVENT);

        // Split remainder into start | end
        String[] fromTo = splitOnce(beforeFrom[1], TOKEN_TO);
        String start = requireNonEmpty(fromTo[0], MSG_EMPTY_EVENT);
        String end = requireNonEmpty(fromTo[1], MSG_EMPTY_EVENT);

        addAndPersist(new Event(desc, start, end), tasks, storage, ui);
    }
}
