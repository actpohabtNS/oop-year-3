package problems.task2;

public class UserCommand implements ICommand {
    @Override
    public String getResult(String data) {
        int len = data.length();
        data = data.substring(0, len - 2);
        System.out.println("Username is: " + data);
        String response = "true";
        return response;
    }
}
