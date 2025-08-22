import java.util.Scanner;

public class Pip {
    private static final String LINE = "    ____________________________________________________________";
    private static final int MAX = 100;

    private static final Task[] tasks = new Task[MAX];
    private static int size = 0;

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

        public void mark() {
            isDone = true;
        }

        public void unmark() {
            isDone = false;
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
    }

    public static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

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
    }

    public static class Event extends Task {
        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    public static void addTask(Task t) {
        tasks[size] = t;
        size++;
        chunk(
                "Got it. I've added this task:\n  " + t.toString()
                        + "\nNow you have " + size + " tasks in the list."
        );
    }

    public static void main(String[] args) throws PipException {
        Scanner sc = new Scanner(System.in);

        chunk("Hi! I'm Pip :)) \n    What can I do for you?");

        while (true) {
            String input = sc.nextLine();

            try {
                if (input.equals("bye")) {
                    chunk("Bye. Hope to see you again soon!");
                    break;
                } else if (input.equals("list")) {
                    if (size == 0) {
                        throw new PipException("Your list is empty! Add some tasks first :))");
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        sb.append(i + 1)
                                .append(". ")
                                .append(tasks[i].toString())
                                .append("\n");
                    }
                    chunk(sb.toString());
                } else if (input.startsWith("mark")) {
                    if (input.trim().equals("mark")) {
                        throw new PipException("Please provide a task number, like: mark 2 :))");
                    }
                    int taskNumber;
                    try {
                        taskNumber = Integer.parseInt(input.substring(5).trim());
                    } catch (NumberFormatException e) {
                        throw new PipException("Please provide a valid task number, like: mark 2 :))");
                    }
                    if (taskNumber < 1 || taskNumber > size) {
                        if (size == 1) {
                            throw new PipException("Task number must be 1!");
                        } else {
                            throw new PipException("Task number must be between 1 and " + size + "!");
                        }
                    }
                    Task task = tasks[taskNumber - 1];
                    task.mark();
                    chunk("Nice! I've marked this task as done: \n  " + task.toString());
                } else if (input.startsWith("unmark")) {
                    if (input.trim().equals("unmark")) {
                        throw new PipException("Please provide a task number, like: unmark 3 :))");
                    }
                    int taskNumber;
                    try {
                        taskNumber = Integer.parseInt(input.substring(7).trim());
                    } catch (NumberFormatException e) {
                        throw new PipException("Please provide a valid task number, like: unmark 3 :))");
                    }
                    if (taskNumber < 1 || taskNumber > size) {
                        if (size == 1) {
                            throw new PipException("Task number must be 1!");
                        } else {
                            throw new PipException("Task number must be between 1 and " + size + "!");
                        }
                    }
                    Task task = tasks[taskNumber - 1];
                    task.unmark();
                    chunk("OK, I've marked this task as not done yet:\n  " + task.toString());
                } else if (input.startsWith("todo")) {
                    String rest = input.length() > 4 ? input.substring(4).trim() : "";
                    if (rest.isEmpty()) {
                        throw new PipException("The description of a todo cannot be empty :((");
                    }
                    if (size >= MAX) {
                        throw new PipException("Task list is full! :((");
                    }
                    addTask(new Todo(rest));
                } else if (input.startsWith("deadline")) {
                    String rest = input.length() > 8 ? input.substring(8).trim() : "";
                    if (rest.isEmpty() || !rest.contains("/by")) {
                        throw new PipException("The description and/or time of a deadline cannot be empty :((");
                    }
                    String[] parts = rest.split("/by", 2);
                    String desc = parts[0].trim();
                    String by = parts.length > 1 ? parts[1].trim() : "";
                    if (desc.isEmpty() || by.isEmpty()) {
                        throw new PipException("The description and/or time of a deadline cannot be empty :((");
                    }
                    if (size >= MAX) {
                        throw new PipException("Task list is full! :((");
                    }
                    addTask(new Deadline(desc, by));
                } else if (input.startsWith("event")) {
                    String rest = input.length() > 5 ? input.substring(5).trim() : "";
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
                    if (size >= MAX) {
                        throw new PipException("Task list is full! :((");
                    }
                    addTask(new Event(desc, from, to));
                } else if (input.trim().isEmpty()) {
                    throw new PipException("Please type a command!");
                } else {
                    throw new PipException("I'm not sure what that means. Sorry!");
                }
            } catch (PipException e) {
                chunk(e.getMessage());
            }
        }

        sc.close();
    }
}
