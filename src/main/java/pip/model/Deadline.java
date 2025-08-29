package pip.model;

import pip.logic.DateTimeParser;

import java.time.LocalDateTime;

/**
 * Task that must be completed by a specific date/time.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Constructs a Deadline with the given description and due date/time.
     *
     * @param description User-visible description of the task.
     * @param by          Date/time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatDateTimeSmart(by) + ")";
    }

    @Override
    public String typeTag() {
        return "D";
    }

    @Override
    public String toDataString() {
        return String.format("%s | %d | %s | %s",
                typeTag(), isDone ? 1 : 0, esc(description), by.toString());
    }
}
