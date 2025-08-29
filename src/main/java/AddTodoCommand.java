public class AddTodoCommand extends Command {
    private final String args;
    public AddTodoCommand(String args) { this.args = args; }

    @Override public void execute(TaskList tasks, Ui ui, Storage storage) throws PipException {
        String desc = args.trim();
        if (desc.isEmpty()) throw new PipException("The description of a todo cannot be empty :((");
        Task t = new Todo(desc);
        tasks.add(t);
        storage.save(tasks.asList());
        ui.show("Got it. I've added this task:\n  " + t
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
