public class UnmarkCommand extends Command {
    private final String args;
    public UnmarkCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        if (tasks.size() == 0) throw new PipException("Your list is empty! Add some tasks first :))");
        int idx = Parser.parseIndex(args, tasks.size());
        tasks.get(idx).unmark();
        storage.save(tasks.asList());
        ui.show("OK, I've marked this task as not done yet:\n  " + tasks.get(idx));
    }
}
