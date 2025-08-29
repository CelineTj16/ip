package pip.model;

/**
 * Basic task with a textual description and no date/time.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo with the given description.
     *
     * @param description User-visible description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String typeTag() {
        return "T";
    }

    @Override
    public String toDataString() {
        return String.format("%s | %d | %s", typeTag(), isDone ? 1 : 0, esc(description));
    }
}
