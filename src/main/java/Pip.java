import java.util.Scanner;

public class Pip {
    private static final String LINE = "    ____________________________________________________________";

    private static void chunk(String text) {
        System.out.println(LINE);
        System.out.println("    " + text);
        System.out.println(LINE);
        System.out.println();
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

            chunk(input);
        }

    }
}
