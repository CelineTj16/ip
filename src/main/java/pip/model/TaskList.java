package pip.model;

import java.util.ArrayList;
import java.util.List;

/** Mutable container for {@link Task} objects with convenience render methods. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Constructs an empty {@code TaskList}. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with the given tasks.
     *
     * @param loaded Initial tasks to copy into this list.
     */
    public TaskList(List<Task> loaded) {
        this.tasks = new ArrayList<>(loaded);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return Current size (0 or more).
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param i Zero-based index.
     * @return Task at the index.
     * @throws IndexOutOfBoundsException If i is out of range.
     */
    public Task get(int i) {
        return tasks.get(i);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param t Task to add.
     */
    public void add(Task t) {
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param i Zero-based index to remove.
     * @return The removed task.
     * @throws IndexOutOfBoundsException If i is out of range.
     */
    public Task remove(int i) {
        return tasks.remove(i);
    }

    /**
     * Returns an unmodifiable view of the internal list.
     *
     * @return Unmodifiable list of tasks.
     */
    public List<Task> asList() {
        return java.util.Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a human-readable rendering of all tasks, one per line.
     *
     * @return Rendered list, or a friendly message if empty.
     */
    public String render() {
        if (tasks.isEmpty()) {
            return "Your list is empty! Add some tasks first :))";
        }
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
