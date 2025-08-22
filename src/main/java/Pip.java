import java.util.ArrayList;
import java.util.Scanner;

public class Pip {
    private static final String LINE = "    ____________________________________________________________";
    private static final int MAX = 100;

    private static final ArrayList<Task> tasks = new ArrayList<>();

    private static void chunk(String text) {
        System.out.println(LINE);
        for (String line : text.split("\n")) {
            System.out.println("     " + line);
        }
        System.out.println(LINE);
    }

    public static class PipException extends Exception {
        public PipException(String message) {
            super(message);
        }
    }

    public static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "X" : " ");
        }

        public void mark() { isDone = true; }

        public void unmark() { isDone = false; }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    public static class Todo extends Task {
        public Todo(String description) { super(description); }
        @Override public String toString() { return "[T]" + super.toString(); }
    }

    public static class Deadline extends Task {
        protected String by;
        public Deadline(String description, String by) { super(description); this.by = by; }
        @Override public String toString() { return "[D]" + super.toString() + " (by: " + by + ")"; }
    }

    public static class Event extends Task {
        protected String from, to;
        public Event(String description, String from, String to) { super(description); this.from = from; this.to = to; }
        @Override public String toString() { return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")"; }
    }

    private static void addTask(Task t) throws PipException {
        if (tasks.size() >= MAX) {
            throw new PipException("Task list is full! :((");
        }
        tasks.add(t);
        chunk("Got it. I've added this task:\n  " + t + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void ensureNotEmpty() throws PipException {
        if (tasks.isEmpty()) {
            throw new PipException("Your list is empty! Add some tasks first :))");
        }
    }

    private static int parseIndex(String rest, String cmd) throws PipException {
        String trimmed = rest == null ? "" : rest.trim();
        if (trimmed.isEmpty()) {
            throw new PipException("Please provide a valid task number between 1 and " + tasks.size() + ".");
        }
        try {
            int idx = Integer.parseInt(trimmed);
            if (idx < 1 || idx > tasks.size()) {
                throw new PipException("Please provide a valid task number between 1 and " + tasks.size() + ".");
            }
            return idx - 1;
        } catch (NumberFormatException e) {
            throw new PipException("Please provide a valid task number between 1 and " + tasks.size() + ".");
        }
    }

    private static void handleList() throws PipException {
        ensureNotEmpty();
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        chunk(sb.toString().trim());
    }

    private static void handleMark(String input) throws PipException {
        ensureNotEmpty();
        int idx = parseIndex(sliceAfter(input, "mark"), "mark");
        tasks.get(idx).mark();
        chunk("Nice! I've marked this task as done: \n  " + tasks.get(idx));
    }

    private static void handleUnmark(String input) throws PipException {
        ensureNotEmpty();
        int idx = parseIndex(sliceAfter(input, "unmark"), "unmark");
        tasks.get(idx).unmark();
        chunk("OK, I've marked this task as not done yet:\n  " + tasks.get(idx));
    }

    private static void handleTodo(String input) throws PipException {
        String rest = sliceAfter(input, "todo").trim();
        if (rest.isEmpty()) {
            throw new PipException("The description of a todo cannot be empty :((");
        }
        addTask(new Todo(rest));
    }

    private static void handleDeadline(String input) throws PipException {
        String rest = sliceAfter(input, "deadline").trim();
        if (rest.isEmpty() || !rest.contains("/by")) {
            throw new PipException("The description and/or time of a deadline cannot be empty :((");
        }
        String[] parts = rest.split("/by", 2);
        String desc = parts[0].trim();
        String by = parts.length > 1 ? parts[1].trim() : "";
        if (desc.isEmpty() || by.isEmpty()) {
            throw new PipException("The description and/or time of a deadline cannot be empty :((");
        }
        addTask(new Deadline(desc, by));
    }

    private static void handleEvent(String input) throws PipException {
        String rest = sliceAfter(input, "event").trim();
        if (rest.isEmpty() || !rest.contains("/from") || !rest.contains("/to")) {
            throw new PipException("The description and/or times of an event cannot be empty :((");
        }
        int pFrom = rest.indexOf("/from");
        int pTo = rest.indexOf("/to");
        String desc = rest.substring(0, pFrom).trim();
        String from = rest.substring(pFrom + 5, pTo).trim();
        String to = rest.substring(pTo + 3).trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new PipException("The description and/or times of an event cannot be empty :((");
        }
        addTask(new Event(desc, from, to));
    }

    private static void handleDelete(String input) throws PipException {
        ensureNotEmpty();
        int idx = parseIndex(sliceAfter(input, "delete"), "delete");
        Task removed = tasks.remove(idx);
        chunk("Noted. I've removed this task:\n  " + removed + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static String sliceAfter(String input, String keyword) {
        int k = keyword.length();
        return input.length() > k ? input.substring(k).trim() : "";
    }

    private static void process(String input) throws PipException {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new PipException("Please type a command!");
        } else if (trimmed.equals("list")) {
            handleList();
        } else if (trimmed.startsWith("mark")) {
            handleMark(trimmed);
        } else if (trimmed.startsWith("unmark")) {
            handleUnmark(trimmed);
        } else if (trimmed.startsWith("todo")) {
            handleTodo(trimmed);
        } else if (trimmed.startsWith("deadline")) {
            handleDeadline(trimmed);
        } else if (trimmed.startsWith("event")) {
            handleEvent(trimmed);
        } else if (trimmed.startsWith("delete")) {
            handleDelete(trimmed);
        } else {
            throw new PipException("I'm not sure what that means. Sorry!");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        chunk("Hi! I'm Pip :)) \n    What can I do for you?");
        while (true) {
            String input = sc.nextLine();
            if (input.equals("bye")) {
                chunk("Bye. Hope to see you again soon!");
                break;
            }
            try {
                process(input);
            } catch (PipException e) {
                chunk(e.getMessage());
            }
        }
        sc.close();
    }
}
