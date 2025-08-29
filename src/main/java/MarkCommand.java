public class MarkCommand extends Command {
    private final String args;
    public MarkCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        if (tasks.size() == 0) throw new PipException("Your list is empty! Add some tasks first :))");
        int idx = Parser.parseIndex(args, tasks.size());
        tasks.get(idx).mark();
        storage.save(tasks.asList());
        ui.show("Nice! I've marked this task as done: \n  " + tasks.get(idx));
    }
}
