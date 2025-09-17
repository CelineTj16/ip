package pip.logic;

import java.time.LocalDateTime;

import pip.app.PipException;
import pip.model.Deadline;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/**
 * Command that adds a new Deadline parsed from
 * {@code <desc> /by <time>} (supports multiple date/time formats).
 */
public class AddDeadline extends Command {
    private final String raw;

    public AddDeadline(String args) {
        this.raw = args == null ? "" : args.trim();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
        if (!raw.contains(TOKEN_BY)) {
            throw new PipException(MSG_USAGE_DEADLINE);
        }

        String[] parts = splitOnce(raw, TOKEN_BY);
        String desc = requireNonEmpty(parts[0], MSG_EMPTY_DEADLINE);
        String by = requireNonEmpty(parts[1], MSG_EMPTY_DEADLINE);

        LocalDateTime dt = DateTimeParser.parseDateTimeFlexible(by);
        addAndPersist(new Deadline(desc, dt), tasks, storage, ui);
    }
}
