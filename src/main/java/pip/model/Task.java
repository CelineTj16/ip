package pip.model;

import pip.app.PipException;
import pip.logic.DateTimeParser;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public void mark() {
        isDone = true;
    }

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

    /** Parse a saved line into a concrete Task. */
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
            if (done) t.mark();
            return t;
        }
        case "D": {
            if (parts.length < 4) {
                throw new PipException("Corrupted deadline line: " + line);
            }
            String desc = unesc(parts[2]);
            var dt = DateTimeParser.parseDateTimeFlexible(parts[3]);
            Deadline d = new Deadline(desc, dt);
            if (done) d.mark();
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
            if (done) e.mark();
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
