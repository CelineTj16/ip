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

        public String format() {
            return "[" + getStatusIcon() + "] " + description;
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
            } else if (input.equals("list")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    sb.append(i + 1).append(". ").append(tasks[i].format()).append("\n");
                }
                chunk(sb.toString());
            } else if (input.startsWith("mark ")) {
                int task_number = Integer.parseInt(input.substring(5).trim());
                Task task = tasks[task_number - 1];
                task.mark();
                chunk("Nice! I've marked this task as done: \n  " + task.format());
            } else if (input.startsWith("unmark ")) {
                int task_number = Integer.parseInt(input.substring(7).trim());
                Task task = tasks[task_number - 1];
                task.unmark();
                chunk("OK, I've marked this task as not done yet: \n  " + task.format());
            } else {
                tasks[size] = new Task(input);
                size++;
                chunk("added: " + input);
            }
        }

    }
}
