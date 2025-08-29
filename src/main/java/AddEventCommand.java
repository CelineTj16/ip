public class AddEventCommand extends Command {
    private final String args;
    public AddEventCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        String rest = args.trim();
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
        Task t = new Event(desc, from, to);
        tasks.add(t);
        storage.save(tasks.asList());
        ui.show("Got it. I've added this task:\n  " + t
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
