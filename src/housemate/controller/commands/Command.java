package housemate.src.housemate.controller.commands;

/**
 * Command is an abstract class that has an `execute()` method. It support
 * queueing and logging using queue container and toString() respectively.
 */
public abstract class Command {
    /**
     * Executes self.
     */
    public abstract void execute();

    /**
     * Returns a String representing the activities initialized by the Command for
     * logging purpose.
     */
    @Override
    public abstract String toString();
}
