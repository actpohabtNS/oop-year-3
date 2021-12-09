import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientMain {
    private static Person createDevice() throws IOException {
        System.out.println("Create your device");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter name: ");
        String name = reader.readLine();
        System.out.println("Enter age: ");
        int age = Integer.parseInt(reader.readLine());
        System.out.println("Enter weight: ");
        int weight = Integer.parseInt(reader.readLine());
        return new Person(name, age, weight);
    }

    public static void main(String[] args) {
        Person person;
        try {
            person = createDevice();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't create a person.");
            return;
        }
        try {
            Client client = new Client(SocketChannel.open(new InetSocketAddress("localhost", 7272)), person);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot connect to server.");
        }
    }
}
