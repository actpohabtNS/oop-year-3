import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private final SocketChannel client;
    private final Person person;
    public ByteBuffer buffer;

    public Client(SocketChannel client, Person person) {
        this.buffer = ByteBuffer.allocate(1024);
        this.person = person;
        this.client = client;
    }

    public void send() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream writer = new ObjectOutputStream(byteArrayOutputStream);
        writer.writeObject(person);
        writer.close();
        buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        client.write(buffer);
    }

    public String sendAndGetResponse() throws IOException {
        try {
            send();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The object is not send");
            return "ERROR";
        }
        return getResponse();
    }

    public String getResponse() throws IOException {
        buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        buffer.rewind();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
    @Override
    public void run() {
        try {
            String response = sendAndGetResponse();
            if (response.equals("ERROR")) {
                System.out.println("Sending was failed. Object wasn't sent.");
            } else {
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't read server response.");
        }
    }
}