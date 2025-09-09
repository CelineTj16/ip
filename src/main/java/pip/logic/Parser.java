package pip.logic;

import pip.app.PipException;

/**
 * Parses raw user input into executable Command objects.
 * Throws PipException for empty input or unknown commands.
 */
public class Parser {
    /**
     * Parses a raw command line into a concrete Command.
     *
     * @param fullCommand Raw line from the user.
     * @return Executable command corresponding to the input.
     * @throws PipException If the input is empty or the command is unknown.
     */
    public static Command parse(String fullCommand) throws PipException {
        String trimmed = fullCommand == null ? "" : fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new PipException("Please type a command!");
        }

        String[] parts = trimmed.split("\\s+", 2);
        String cmd = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        return switch (cmd) {
        case "bye" -> new Command.Exit();
        case "list" -> new Command.List();
        case "mark" -> new Command.Mark(args);
        case "unmark" -> new Command.Unmark(args);
        case "delete" -> new Command.Delete(args);
        case "todo" -> new Command.AddTodo(args);
        case "deadline" -> new Command.AddDeadline(args);
        case "event" -> new Command.AddEvent(args);
        case "find" -> new Command.Find(args);
        default -> throw new PipException("I'm not sure what that means. Sorry!");
        };


    }

    static int parseIndex(String s, int size) throws PipException {
        String t = s == null ? "" : s.trim();
        if (t.isEmpty()) {
            throw new PipException("Please provide a valid task number between 1 and " + size + ".");
        }
        try {
            int idx = Integer.parseInt(t);
            if (idx < 1 || idx > size) {
                throw new PipException("Please provide a valid task number between 1 and " + size + ".");
            }
            return idx - 1;
        } catch (NumberFormatException e) {
            throw new PipException("Please provide a valid task number between 1 and " + size + ".");
        }
    }
}
