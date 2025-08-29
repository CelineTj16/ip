public class AddDeadlineCommand extends Command {
    private final String args;
    public AddDeadlineCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        String rest = args.trim();
        if (rest.isEmpty() || !rest.contains("/by")) {
            throw new PipException("The description and/or time of a deadline cannot be empty :((");
        }
        String[] parts = rest.split("/by", 2);
        String desc = parts[0].trim();
        String by = parts.length > 1 ? parts[1].trim() : "";
        if (desc.isEmpty() || by.isEmpty()) {
            throw new PipException("The description and/or time of a deadline cannot be empty :((");
        }
        var dt = DateTimeParser.parseDateTimeFlexible(by);
        Task t = new Deadline(desc, dt);
        tasks.add(t);
        storage.save(tasks.asList());
        ui.show("Got it. I've added this task:\n  " + t
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
