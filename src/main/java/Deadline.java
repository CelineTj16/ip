import java.time.LocalDateTime;

public class Deadline extends Task {
    protected LocalDateTime by;

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
