package pip.model;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() { this.tasks = new ArrayList<>(); }
    public TaskList(List<Task> loaded) { this.tasks = new ArrayList<>(loaded); }

    public int size() { return tasks.size(); }
    public Task get(int i) { return tasks.get(i); }
    public void add(Task t) { tasks.add(t); }
    public Task remove(int i) { return tasks.remove(i); }

    public List<Task> asList() { return java.util.Collections.unmodifiableList(tasks); }

    public String render() {
        if (tasks.isEmpty()) return "Your list is empty! Add some tasks first :))";
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
