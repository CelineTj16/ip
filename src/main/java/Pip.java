import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A task manager chatbot that supports todos, deadlines, and events.
 */
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

    /** Exception type for Pip command and storage errors. */
    public static class PipException extends Exception {
        public PipException(String message) {
            super(message);
        }
    }

    /** Base task with a description and completion status. */
    public abstract static class Task {
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
                if (done) {
                    t.mark();
                }
                return t;
            }
            case "D": {
                if (parts.length < 4) {
                    throw new PipException("Corrupted deadline line: " + line);
                }
                String desc = unesc(parts[2]);
                String by = unesc(parts[3]);
                Deadline d = new Deadline(desc, by);
                if (done) {
                    d.mark();
                }
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
                if (done) {
                    e.mark();
                }
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

    /** A simple todo task with only a description. */
    public static class Todo extends Task {
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

    /** A task with a deadline time. */
    public static class Deadline extends Task {
        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }

        @Override
        public String typeTag() {
            return "D";
        }

        @Override
        public String toDataString() {
            return String.format("%s | %d | %s | %s",
                    typeTag(), isDone ? 1 : 0, esc(description), esc(by));
        }
    }

    /** An event task with start and end time ranges. */
    public static class Event extends Task {
        protected String from, to;

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

    /** Storage utility for loading and saving tasks to ./data/pip.txt */
    private static class Storage {
        private static final Path DATA_DIR = Paths.get("data");
        private static final Path DATA_FILE = DATA_DIR.resolve("pip.txt");

        public static void load(ArrayList<Task> out) {
            try {
                if (Files.notExists(DATA_DIR)) {
                    Files.createDirectories(DATA_DIR);
                }
                if (Files.notExists(DATA_FILE)) {
                    Files.createFile(DATA_FILE);
                    return; // nothing to load
                }
                List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) {
                        continue;
                    }
                    try {
                        Task t = Task.fromDataString(trimmed);
                        out.add(t);
                    } catch (PipException parseErr) {
                        chunk("Warning: skipped corrupted line in save file:\n  " + trimmed);
                    }
                }
            } catch (IOException e) {
                chunk("Warning: could not read save file. Continuing without loading.");
            }
        }

        public static void save(ArrayList<Task> items) {
            try {
                if (Files.notExists(DATA_DIR)) {
                    Files.createDirectories(DATA_DIR);
                }
                List<String> lines = new ArrayList<>();
                for (Task t : items) {
                    lines.add(t.toDataString());
                }
                Files.write(
                        DATA_FILE,
                        lines,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.CREATE
                );
            } catch (IOException e) {
                chunk("Warning: could not save tasks to disk.");
            }
        }
    }

    private static void addTask(Task t) throws PipException {
        if (tasks.size() >= MAX) {
            throw new PipException("Task list is full! :((");
        }
        tasks.add(t);
        Storage.save(tasks);
        chunk("Got it. I've added this task:\n  " + t
                + "\nNow you have " + tasks.size() + " tasks in the list.");
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
        Storage.save(tasks);
        chunk("Nice! I've marked this task as done: \n  " + tasks.get(idx));
    }

    private static void handleUnmark(String input) throws PipException {
        ensureNotEmpty();
        int idx = parseIndex(sliceAfter(input, "unmark"), "unmark");
        tasks.get(idx).unmark();
        Storage.save(tasks);
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
        Storage.save(tasks);
        chunk("Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static String sliceAfter(String input, String keyword) {
        int k = keyword.length();
        return input.length() > k ? input.substring(k).trim() : "";
    }

    private static void process(String input) throws PipException {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new PipException("Please type a command!");
        }

        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
        case "list":
            handleList();
            break;
        case "mark":
            handleMark("mark " + args);
            break;
        case "unmark":
            handleUnmark("unmark " + args);
            break;
        case "todo":
            handleTodo("todo " + args);
            break;
        case "deadline":
            handleDeadline("deadline " + args);
            break;
        case "event":
            handleEvent("event " + args);
            break;
        case "delete":
            handleDelete("delete " + args);
            break;
        default:
            throw new PipException("I'm not sure what that means. Sorry!");
        }
    }

    public static void main(String[] args) {
        Storage.load(tasks);

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
