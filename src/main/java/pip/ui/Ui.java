package pip.ui;

public class Ui {
    private static final String LINE = "    ____________________________________________________________";

    public void showLine() { System.out.println(LINE); }

    public void showWelcome() {
        showLine();
        System.out.println("     Hi! I'm Pip :))");
        System.out.println("     What can I do for you?");
        showLine();
    }

    public String readCommand(java.util.Scanner sc) { return sc.nextLine(); }

    public void show(String text) {
        for (String line : text.split("\n")) System.out.println("     " + line);
    }

    public void showError(String msg) { System.out.println("     " + msg); }

    public void showLoadingError() {
        System.out.println("     Warning: could not load save file. Starting with an empty list.");
    }
}
