package problems.task1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private InetSocketAddress address;
    private SocketChannel socket;
    private ByteBuffer buffer;

    Client(String hostname, int port) throws IOException {
        address = new InetSocketAddress(hostname, port);
        socket = SocketChannel.open(address);

        log("[Client] Connecting to Server on port " + port + "...");
    }

    public String sendMessage(String message) throws IOException {
        byte[] bytes = message.getBytes();
        buffer = ByteBuffer.wrap(bytes);
        socket.write(buffer);

        log("[Client] Sending: " + message);
        buffer.clear();

        return "";
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length != 2) {
            System.out.println("Usage: <HOSTNAME> <PORT>");
            System.exit(1);
        }

        Client client = new Client(args[0], Integer.parseInt(args[1]));

        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            command = scanner.nextLine().trim();
            client.sendMessage(command);
        } while (!command.equals("logout") && !command.equals("shutdown"));

        client.disconnect();
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
