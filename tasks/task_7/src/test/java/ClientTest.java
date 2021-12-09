import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientTest {
    private final Person person = new Person("Nikita", 19, 85);
    private final SocketChannel clientChannel = Mockito.mock(SocketChannel.class);
    private Client client;

    @BeforeEach
    void init() {
        client = new Client(clientChannel, person);
    }
    @Test
    void getResponseTest() throws IOException {
        doAnswer(invocation -> {
            client.buffer = ByteBuffer.wrap("Person successfully received".getBytes());
            return null;
        }).when(clientChannel).read(client.buffer);
        String response = client.getResponse();
        verify(clientChannel).read(ByteBuffer.allocate(1024));
        assertEquals(response, "Person successfully received");
    }
    @Test
    void sendDeviceObjectTest() throws IOException, ClassNotFoundException {
        client.send();
        client.buffer.rewind();
        ObjectInputStream reader = new ObjectInputStream(new ByteArrayInputStream(client.buffer.array()));
        Person personFromBuffer =  (Person) reader.readObject();
        assertEquals(personFromBuffer.name, person.name);
    }
}
