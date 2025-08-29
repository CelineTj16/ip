public class Ui {
    private static final String LINE = "    ____________________________________________________________";

    void showLine() { System.out.println(LINE); }

    void showWelcome() {
        showLine();
        System.out.println("     Hi! I'm Pip :))");
        System.out.println("     What can I do for you?");
        showLine();
    }

    String readCommand(java.util.Scanner sc) { return sc.nextLine(); }

    void show(String text) {
        for (String line : text.split("\n")) System.out.println("     " + line);
    }

    void showError(String msg) { System.out.println("     " + msg); }

    void showLoadingError() {
        System.out.println("     Warning: could not load save file. Starting with an empty list.");
    }
}
