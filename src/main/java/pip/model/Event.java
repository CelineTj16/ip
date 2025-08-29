package pip.model;

/**
 * Task that spans a time interval (start to end).
 */
public class Event extends Task {
    protected String from, to;

    /**
     * Constructs an Event with the given description and time range.
     *
     * @param description User-visible description.
     * @param from        Start time text (unparsed).
     * @param to          End time text (unparsed).
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String typeTag() {
        return "E";
    }

    @Override
    public String toDataString() {
        return String.format("%s | %d | %s | %s | %s",
                typeTag(), isDone ? 1 : 0, esc(description), esc(from), esc(to));
    }
}
