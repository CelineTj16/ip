public class DeleteCommand extends Command {
    private final String args;
    public DeleteCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        if (tasks.size() == 0) throw new PipException("Your list is empty! Add some tasks first :))");
        int idx = Parser.parseIndex(args, tasks.size());
        Task removed = tasks.remove(idx);
        storage.save(tasks.asList());
        ui.show("Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
