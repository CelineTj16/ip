package pip.model;

import pip.app.PipException;
import pip.logic.DateTimeParser;

/**
 * Base model for a user task in Pip.
 * Subclasses provide a type tag and custom serialization for persistence.
 */
public abstract class Task {
    public static final String TODO_TAG = "T";
    public static final String DEADLINE_TAG = "D";
    public static final String EVENT_TAG = "E";
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

    /** One-letter type tag for saving, "T", "D", "E". */
    public abstract String typeTag();

    /** Serialize to pipe-delimited format for storage. */
    public abstract String toDataString();

    /** Common numeric flag to categorise whether done or not. */
    protected int doneFlag() {
        return isDone ? 1 : 0;
    }

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
        requireMinParts(parts, 3, "Corrupted save line: " + line);

        String type = parts[0];
        boolean done = "1".equals(parts[1]);

        switch (type) {
        case TODO_TAG:
            return parseTodo(parts, done);
        case DEADLINE_TAG:
            return parseDeadline(parts, done, line);
        case EVENT_TAG:
            return parseEvent(parts, done, line);
        default:
            throw new PipException("Unknown task type: " + type);
        }
    }

    private static Todo parseTodo(String[] parts, boolean done) {
        String desc = unesc(parts[2]);
        Todo t = new Todo(desc);
        markIfDone(t, done);
        return t;
    }

    private static Deadline parseDeadline(String[] parts, boolean done, String line) throws PipException {
        requireMinParts(parts, 4, "Corrupted deadline line: " + line);
        String desc = unesc(parts[2]);
        var dt = DateTimeParser.parseDateTimeFlexible(parts[3]);
        Deadline d = new Deadline(desc, dt);
        markIfDone(d, done);
        return d;
    }

    private static Event parseEvent(String[] parts, boolean done, String line) throws PipException {
        requireMinParts(parts, 5, "Corrupted event line: " + line);
        String desc = unesc(parts[2]);
        String from = unesc(parts[3]);
        String to = unesc(parts[4]);
        Event e = new Event(desc, from, to);
        markIfDone(e, done);
        return e;
    }

    private static void requireMinParts(String[] parts, int min, String errorMsg) throws PipException {
        if (parts.length < min) {
            throw new PipException(errorMsg);
        }
    }

    private static void markIfDone(Task t, boolean done) {
        if (done) {
            t.mark();
        }
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
