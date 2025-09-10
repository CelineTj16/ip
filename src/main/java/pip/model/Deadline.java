package pip.model;

import java.time.LocalDateTime;

import pip.logic.DateTimeParser;

/** Task that must be completed by a specific date/time. */
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
                typeTag(), doneFlag(), esc(description), by.toString());
    }
}
