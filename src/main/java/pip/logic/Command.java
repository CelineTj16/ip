package pip.logic;

import pip.app.PipException;
import pip.model.Task;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;

/** Base type for all executable commands in Pip. */
public abstract class Command {
    protected static final String TOKEN_BY = "/by";
    protected static final String TOKEN_FROM = "/from";
    protected static final String TOKEN_TO = "/to";

    protected static final String MSG_ADDED_PREFIX = "Got it. I've added this task:\n  ";
    protected static final String MSG_COUNT_PREFIX = "\nNow you have ";
    protected static final String MSG_COUNT_SUFFIX = " tasks in the list.";
    protected static final String MSG_EMPTY_TODO = "The description of a todo cannot be empty :((";
    protected static final String MSG_USAGE_DEADLINE = "Usage: deadline <desc> /by <time>";
    protected static final String MSG_EMPTY_DEADLINE = "Deadline description/time cannot be empty :((";
    protected static final String MSG_USAGE_EVENT = "Usage: event <desc> /from <start> /to <end>";
    protected static final String MSG_EMPTY_EVENT = "Event description/times cannot be empty :((";
    protected static final String MSG_EMPTY_LIST = "Your list is empty! Add some tasks first :))";

    /** Executes the command against the given model, UI, and storage. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PipException;

    /** Whether the application should exit after this command completes. */
    public boolean isExit() { return false; }

    /** Ensures text is non-empty after trim; throws with given message if empty. */
    protected static String requireNonEmpty(String raw, String onEmptyMessage) throws PipException {
        String t = raw == null ? "" : raw.trim();
        if (t.isEmpty()) {
            throw new PipException(onEmptyMessage);
        }
        return t;
    }

    /** Adds a task, persists list, and shows standard “added” UI. */
    protected static void addAndPersist(Task t, TaskList tasks, Storage storage, Ui ui) throws PipException {
        tasks.add(t);
        storage.save(tasks.asList());
        showAdded(t, tasks, ui);
    }

    /** Shows standardized “task added” message. */
    protected static void showAdded(Task t, TaskList tasks, Ui ui) {
        ui.show(MSG_ADDED_PREFIX + t + MSG_COUNT_PREFIX + tasks.size() + MSG_COUNT_SUFFIX);
    }

    /** Splits text by the first occurrence of token; returns {left, right} trimmed. */
    protected static String[] splitOnce(String text, String token) {
        int p = text.indexOf(token);
        if (p < 0) {
            return new String[] { text.trim(), "" };
        }
        String left = text.substring(0, p).trim();
        String right = text.substring(p + token.length()).trim();
        return new String[] { left, right };
    }
}
