import java.util.Scanner;

public class Pip {
    private static final String LINE = "    ____________________________________________________________";
    private static final int MAX = 100;
    private static final String[] tasks = new String[MAX];
    private static int size = 0;

    private static void chunk(String text) {
        System.out.println(LINE);
        for (String line : text.split("\n")) {
            System.out.println("     " + line);  // always 5 spaces before text
        }
        System.out.println(LINE);
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
                    sb.append(i + 1).append(". ").append(tasks[i]).append("\n");
                }
                chunk(sb.toString());
            } else {
                tasks[size] = input;
                size++;
                chunk("added: " + input);
            }
        }

    }
}
