package pip.model;

public class Todo extends Task {
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
