package pip.logic;

import java.time.LocalDateTime;

import pip.app.PipException;
import pip.model.Deadline;
import pip.model.Event;
import pip.model.Task;
import pip.model.TaskList;
import pip.model.Todo;
import pip.storage.Storage;
import pip.ui.Ui;

/** Base type for all executable commands in Pip.*/
public abstract class Command {
    static final String TOKEN_BY = "/by";
    static final String TOKEN_FROM = "/from";
    static final String TOKEN_TO = "/to";

    static final String MSG_ADDED_PREFIX = "Got it. I've added this task:\n  ";
    static final String MSG_COUNT_PREFIX = "\nNow you have ";
    static final String MSG_COUNT_SUFFIX = " tasks in the list.";
    static final String MSG_EMPTY_TODO = "The description of a todo cannot be empty :((";
    static final String MSG_USAGE_DEADLINE = "Usage: deadline <desc> /by <time>";
    static final String MSG_EMPTY_DEADLINE = "Deadline description/time cannot be empty :((";
    static final String MSG_USAGE_EVENT = "Usage: event <desc> /from <start> /to <end>";
    static final String MSG_EMPTY_EVENT = "Event description/times cannot be empty :((";
    static final String MSG_EMPTY_LIST = "Your list is empty! Add some tasks first :))";
    static final String MSG_FIND_USAGE = "Usage: find <keyword>";

    /**
     * Executes the command against the given model, UI, and storage.
     *
     * @param tasks   Current task list to read/mutate.
     * @param ui      UI for user-visible output.
     * @param storage Storage for persistence.
     * @throws PipException If execution cannot proceed (e.g., invalid input).
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws PipException;

    /**
     * Returns whether the application should exit after this command completes.
     *
     * @return true if the REPL should terminate; false otherwise.
     */
    public boolean isExit() {
        return false;
    }

    /** Ensures text is non-empty after trim; throws with given message if empty. */
    static String requireNonEmpty(String raw, String onEmptyMessage) throws PipException {
        String t = raw == null ? "" : raw.trim();
        if (t.isEmpty()) {
            throw new PipException(onEmptyMessage);
        }
        return t;
    }

    /** Adds a task, persists list, and shows standard “added” UI. */
    static void addAndPersist(Task t, TaskList tasks, Storage storage, Ui ui) throws PipException {
        tasks.add(t);
        storage.save(tasks.asList());
        showAdded(t, tasks, ui);
    }

    /** Shows standardized “task added” message. */
    static void showAdded(Task t, TaskList tasks, Ui ui) {
        ui.show(MSG_ADDED_PREFIX + t + MSG_COUNT_PREFIX + tasks.size() + MSG_COUNT_SUFFIX);
    }

    /** Splits text by the first occurrence of token; returns {left, right} trimmed. */
    static String[] splitOnce(String text, String token) {
        int p = text.indexOf(token);
        if (p < 0) {
            return new String[] { text.trim(), "" };
        }
        String left = text.substring(0, p).trim();
        String right = text.substring(p + token.length()).trim();
        return new String[] { left, right };
    }

    /** Command that adds a new Todo task to the list. */
    public static class AddTodo extends Command {
        private final String desc;
        public AddTodo(String args) { this.desc = (args == null ? "" : args).trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
            String d = requireNonEmpty(desc, MSG_EMPTY_TODO);
            addAndPersist(new Todo(d), tasks, storage, ui);
        }
    }

    /**
     * Command that adds a new Deadline parsed from
     * {@code <desc> /by <time>} (supports multiple date/time formats).
     */
    public static class AddDeadline extends Command {
        private final String raw;
        public AddDeadline(String args) { this.raw = args == null ? "" : args.trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
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

    /**
     * Command that adds a new Event parsed from
     * {@code <desc> /from <start> /to <end>}.
     */
    public static class AddEvent extends Command {
        private final String raw;
        public AddEvent(String args) { this.raw = args == null ? "" : args.trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
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

    /** Command that deletes the task at a user-specified 1-based index. */
    public static class Delete extends Command {
        private final String args;
        public Delete(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
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

    /** Command that marks the task at a user-specified 1-based index as done. */
    public static class Mark extends Command {
        private final String args;
        public Mark(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
            int idx = Parser.parseIndex(args, tasks.size());
            Task t = tasks.get(idx);
            t.mark();
            storage.save(tasks.asList());
            ui.show("Nice! I've marked this task as done:\n  " + t);
        }
    }

    /** Command that marks the task at a user-specified 1-based index as not done. */
    public static class Unmark extends Command {
        private final String args;
        public Unmark(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            assert tasks != null && ui != null && storage != null : "tasks, ui, and storage must be set";
            int idx = Parser.parseIndex(args, tasks.size());
            Task t = tasks.get(idx);
            t.unmark();
            storage.save(tasks.asList());
            ui.show("OK, I've marked this task as not done yet:\n  " + t);
        }
    }

    /** Command that lists all tasks in the current task list. */
    public static class List extends Command {
        @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.show(tasks.render());
        }
    }

    /** Command that exits the application. */
    public static class Exit extends Command {
        @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.show("Bye. Hope to see you again soon!");
        }
        @Override public boolean isExit() {
            return true;
        }
    }

    /**
     * Command that finds tasks whose description contains a keyword (case-insensitive).
     */
    public static class Find extends Command {
        private final String keyword;

        /**
         * Constructs a Find command.
         *
         * @param args Raw keyword text.
         */
        public Find(String args) {
            this.keyword = args == null ? "" : args.trim();
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            if (keyword.isEmpty()) {
                throw new PipException("Usage: find <keyword>");
            }

            String kw = keyword.toLowerCase();
            var all = tasks.asList();

            StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
            int count = 0;
            for (int i = 0; i < all.size(); i++) {
                Task t = all.get(i);
                if (t.getDescription().toLowerCase().contains(kw)) {
                    count++;
                    sb.append(count).append(". ").append(t).append("\n");
                }
            }

            if (count == 0) {
                ui.show("No matching tasks found for: " + keyword);
            } else {
                ui.show(sb.toString().trim());
            }
        }
    }

}
