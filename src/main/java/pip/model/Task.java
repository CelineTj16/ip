package pip.model;

import pip.app.PipException;
import pip.logic.DateTimeParser;

/**
 * Base model for a user task in Pip.
 * Subclasses provide a type tag and custom serialization for persistence.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task with the given description; tasks start as not done.
     *
     * @param description User-visible description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used in list rendering.
     *
     * @return "X" if done, otherwise a single space " ".
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as uncompleted. */
    public void unmark() {
        isDone = false;
    }

    /** One-letter type tag for saving, e.g., "T", "D", "E". */
    public abstract String typeTag();

    /** Serialize to pipe-delimited format for storage. */
    public abstract String toDataString();

    protected static String esc(String s) {
        return s.replace("|", "¦");
    }

    protected static String unesc(String s) {
        return s.replace("¦", "|");
    }

    /**
     * Returns the user-visible description of this task.
     *
     * @return Description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Deserializes a task from a pipe-delimited save line.
     *
     * @param line Saved line previously produced by {@link #toDataString()}.
     * @return Concrete {@link Task} instance represented by the line.
     * @throws PipException If the line is malformed or references an unknown type.
     */
    public static Task fromDataString(String line) throws PipException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new PipException("Corrupted save line: " + line);
        }

        String type = parts[0];
        boolean done = "1".equals(parts[1]);

        switch (type) {
        case "T": {
            String desc = unesc(parts[2]);
            Todo t = new Todo(desc);
            if (done) {
                t.mark();
            }
            return t;
        }
        case "D": {
            if (parts.length < 4) {
                throw new PipException("Corrupted deadline line: " + line);
            }
            String desc = unesc(parts[2]);
            var dt = DateTimeParser.parseDateTimeFlexible(parts[3]);
            Deadline d = new Deadline(desc, dt);
            if (done) {
                d.mark();
            }
            return d;
        }
        case "E": {
            if (parts.length < 5) {
                throw new PipException("Corrupted event line: " + line);
            }
            String desc = unesc(parts[2]);
            String from = unesc(parts[3]);
            String to = unesc(parts[4]);
            Event e = new Event(desc, from, to);
            if (done) {
                e.mark();
            }
            return e;
        }
        default:
            throw new PipException("Unknown task type: " + type);
        }
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
