package problems.task2;

public class CommandFactory {

    public static UserCommand createCommand(String type) {
        type = type.toUpperCase();
        switch (type) {
            default:
                return new UserCommand();
        }
    }
}
