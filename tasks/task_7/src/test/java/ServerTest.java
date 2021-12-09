import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class ServerTest {
    private final ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    private final Person person = new Person("Nikita", 19, 85);
    private final Set<SelectionKey> setSelectionKeys = new HashSet<>();
    private final SelectionKey acceptableSelectionKey = Mockito.mock(SelectionKey.class);
    private final SelectionKey readableSelectionKey = Mockito.mock(SelectionKey.class);
    private Server server;


    @BeforeEach
    void init() throws IOException {
        SocketChannel client = Mockito.mock(SocketChannel.class);
        when(serverSocket.accept()).thenReturn(client);
        this.server = Mockito.spy(Server.class);
        when(acceptableSelectionKey.readyOps()).thenReturn(SelectionKey.OP_ACCEPT);
        when(readableSelectionKey.readyOps()).thenReturn(SelectionKey.OP_READ);
    }

    @Test
    void successfulReceivingTest() throws IOException {
        SocketChannel client = Mockito.mock(SocketChannel.class);
        server.sendResponse(true, client);
        verify(client).write(ByteBuffer.wrap("Person successfully received".getBytes()));
    }

    @Test
    void acceptClientTest() {
        setSelectionKeys.add(acceptableSelectionKey);
        doNothing().when(server).acceptClient();
        server.checkSelectionKeys(setSelectionKeys);
        verify(server, times(1)).acceptClient();
    }

    @Test
    void receiveObjectTest() {
        setSelectionKeys.add(readableSelectionKey);
        doNothing().when(server).receiveObject(readableSelectionKey);
        server.checkSelectionKeys(setSelectionKeys);
        verify(server).receiveObject(readableSelectionKey);
    }

    @Test
    void readObjectTest() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream writer = new ObjectOutputStream(byteArrayOutputStream);
        writer.writeObject(person);
        writer.close();
        ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

        Person receivedPerson = (Person) server.readObject(buffer);
        assertEquals(receivedPerson.name, person.name);
    }

}
