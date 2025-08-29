package pip.logic;

import pip.app.PipException;
import pip.model.TaskList;
import pip.storage.Storage;
import pip.ui.Ui;
import pip.model.Task;
import pip.model.Todo;
import pip.model.Deadline;
import pip.model.Event;

import java.time.LocalDateTime;

/**
 * Base type for all executable commands in Pip.
 */
public abstract class Command {
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
    public boolean isExit() { return false; }

    /**
     * Command that adds a new Todo task to the list.
     */
    public static class AddTodo extends Command {
        private final String args;

        /**
         * Constructs an AddTodo command with raw description arguments.
         *
         * @param args Raw description text (leading/trailing spaces allowed).
         */
        public AddTodo(String args) { this.args = args.trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            if (args.isEmpty()) throw new PipException("The description of a todo cannot be empty :((");
            Task t = new Todo(args);
            tasks.add(t);
            storage.save(tasks.asList());
            ui.show("Got it. I've added this task:\n  " + t
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        }
    }

    /**
     * Command that adds a new Deadline parsed from
     * <desc> /by <time> (supports multiple date/time formats).
     */
    public static class AddDeadline extends Command {
        private final String args;

        /**
         * Constructs an AddDeadline command with raw argument text.
         *
         * @param args Raw text containing description and /by time.
         */
        public AddDeadline(String args) { this.args = args.trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            if (!args.contains("/by")) throw new PipException("Usage: deadline <desc> /by <time>");
            String[] parts = args.split("/by", 2);
            String desc = parts[0].trim();
            String by = parts[1].trim();
            if (desc.isEmpty() || by.isEmpty()) throw new PipException("Deadline description/time cannot be empty :((");
            LocalDateTime dt = DateTimeParser.parseDateTimeFlexible(by);
            Task t = new Deadline(desc, dt);
            tasks.add(t);
            storage.save(tasks.asList());
            ui.show("Got it. I've added this task:\n  " + t
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        }
    }

    /**
     * Command that adds a new Event parsed from
     * <desc> /from <start> /to <end>.
     */
    public static class AddEvent extends Command {
        private final String args;

        /**
         * Constructs an AddEvent command with raw argument text.
         *
         * @param args Raw text containing description, /from, and /to parts.
         */
        public AddEvent(String args) { this.args = args.trim(); }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            if (!args.contains("/from") || !args.contains("/to"))
                throw new PipException("Usage: event <desc> /from <start> /to <end>");
            int pFrom = args.indexOf("/from"), pTo = args.indexOf("/to");
            String desc = args.substring(0, pFrom).trim();
            String from = args.substring(pFrom + 5, pTo).trim();
            String to   = args.substring(pTo + 3).trim();
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty())
                throw new PipException("Event description/times cannot be empty :((");
            Task t = new Event(desc, from, to);
            tasks.add(t);
            storage.save(tasks.asList());
            ui.show("Got it. I've added this task:\n  " + t
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        }
    }

    /**
     * Command that deletes the task at a user-specified 1-based index.
     */
    public static class Delete extends Command {
        private final String args;

        /**
         * Constructs a Delete command.
         *
         * @param args Raw index string provided by the user (1-based).
         */
        public Delete(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            if (tasks.size() == 0) throw new PipException("Your list is empty! Add some tasks first :))");
            int idx = Parser.parseIndex(args, tasks.size());
            Task removed = tasks.remove(idx);
            storage.save(tasks.asList());
            ui.show("Noted. I've removed this task:\n  " + removed
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        }
    }

    /**
     * Command that marks the task at a user-specified 1-based index as done.
     */
    public static class Mark extends Command {
        private final String args;

        /**
         * Constructs a Mark command.
         *
         * @param args Raw index string provided by the user (1-based).
         */
        public Mark(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            int idx = Parser.parseIndex(args, tasks.size());
            tasks.get(idx).mark();
            storage.save(tasks.asList());
            ui.show("Nice! I've marked this task as done:\n  " + tasks.get(idx));
        }
    }

    /**
     * Command that marks the task at a user-specified 1-based index as not done.
     */
    public static class Unmark extends Command {
        private final String args;

        /**
         * Constructs an Unmark command.
         *
         * @param args Raw index string provided by the user (1-based).
         */
        public Unmark(String args) { this.args = args; }

        @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
            int idx = Parser.parseIndex(args, tasks.size());
            tasks.get(idx).unmark();
            storage.save(tasks.asList());
            ui.show("OK, I've marked this task as not done yet:\n  " + tasks.get(idx));
        }
    }

    /**
     * Command that lists all tasks in the current task list.
     */
    public static class List extends Command {
        @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.show(tasks.render());
        }
    }

    /**
     * Command that exits the application.
     */
    public static class Exit extends Command {
        @Override public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.show("Bye. Hope to see you again soon!");
        }
        @Override public boolean isExit() { return true; }
    }
}
