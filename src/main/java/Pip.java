import java.util.Scanner;

public class Pip {
    private static final String LINE = "    ____________________________________________________________";
    private static final int MAX = 100;
    private static final Task[] tasks = new Task[MAX];
    private static int size = 0;

    private static void chunk(String text) {
        System.out.println(LINE);
        for (String line : text.split("\n")) {
            System.out.println("     " + line);  // always 5 spaces before text
        }
        System.out.println(LINE);
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
        chunk("Got it. I've added this task:\n  " + t.toString() +
                "\nNow you have " + size + " tasks in the list.");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        chunk("Hi! I'm Pip :)) \n    What can I do for you?");

        while (true) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                chunk("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    sb.append(i + 1).append(". ").append(tasks[i].toString()).append("\n");
                }
                chunk(sb.toString());
            } else if (input.startsWith("mark ")) {
                int task_number = Integer.parseInt(input.substring(5).trim());
                Task task = tasks[task_number - 1];
                task.mark();
                chunk("Nice! I've marked this task as done: \n  " + task.toString());
            } else if (input.startsWith("unmark ")) {
                int task_number = Integer.parseInt(input.substring(7).trim());
                Task task = tasks[task_number - 1];
                task.unmark();
                chunk("OK, I've marked this task as not done yet: \n  " + task.toString());
            } else if (input.startsWith("todo ")) {
                String desc = input.substring(5).trim();
                addTask(new Todo(desc));
            } else if (input.startsWith("deadline ")) {
                String rest = input.substring(9).trim();
                int p = rest.indexOf("/by");
                String desc = rest.substring(0, p).trim();
                String by = rest.substring(p + 3).trim();
                addTask(new Deadline(desc, by));
            } else if (input.startsWith("event ")) {
                String rest = input.substring(6).trim();
                int pFrom = rest.indexOf("/from");
                int pTo = (pFrom >= 0) ? rest.indexOf("/to", pFrom + 5) : -1;
                String desc = rest.substring(0, pFrom).trim();
                String from = rest.substring(pFrom + 5, pTo).trim(); // after "/from"
                String to = rest.substring(pTo + 3).trim();          // after "/to"
                addTask(new Event(desc, from, to));
            } else {
                chunk("Sorry, I didn't understand that.");
            }
        }

    }
}
