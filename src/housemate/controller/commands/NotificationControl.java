package housemate.src.housemate.controller.commands;

/**
 * This class will be initialized when the Housemate Controller needs a Command
 * to send message/alert to recipients of interest.
 */
public class NotificationControl extends Command {
    private String receiver;
    private String msg;

    public NotificationControl(String receiver, String msg) {
        this.receiver = receiver;
        this.msg = msg;
    }

    /**
     * Notifies the specified receiver with a message.
     */
    @Override
    public void execute() {
        System.out.print(msg + " -> ");
        System.out.println(this);
    }

    @Override
    public String toString() {
        return receiver + " notified";
    }
}
