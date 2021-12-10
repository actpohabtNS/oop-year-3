package problems.task2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class FtpServer {

    private int port;

    private String ip;

    //The ServerSocketChannel channel belonging to the server
    private ServerSocketChannel serverSocketChannel;

    //Multiplex io scheduler
    private Selector selector;

    //For ftp control connection, the amount of task2.data transferred is small,
    // so the setting here is relatively small
    private ByteBuffer inputBuffer = ByteBuffer.allocate(100);

    public FtpServer(int port, String ip) throws IOException {

        selector = Selector.open();

        //Used for connection establishment
        serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(21));
        serverSocketChannel.configureBlocking(false);

        //Register the channel with the selector
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server on.....");
    }

    public static void main(String args[]) throws IOException {
        FtpServer ftpServer = new FtpServer(21, "127.0.0.1");
        ftpServer.listen();
    }

    public void listen() throws IOException {
        while (!Thread.interrupted()) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                handleKey(key);
            }
        }
    }

    public void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            accept(key);
        }
        //The completion of the read event is the arrival of the client command
        else if (key.isReadable()) {
            read(key);
        }
        //Tests whether this key's channel is ready for writing,
        // test whether the channel corresponding to this event can already write task2.data
        //Here is a unified return to the task2.data, what is needed,
        // and the corresponding key, which contains all the task2.data we need.
        else if (key.isWritable()) {
            write(key);
        }
    }

    public void accept(SelectionKey key) throws IOException {
        System.out.println("Establish connection...");
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        //Register a tcp connection channel to the scheduler,
        // and at the same time be interested in the write event
        // (it will be executed soon, because the write operation
        // is ready immediately after registration)
        SelectionKey socketKey = socketChannel.register(selector, SelectionKey.OP_WRITE);
        String response = "220 \r\n";
        socketKey.attach(response);
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        inputBuffer.clear();
        //Write task2.data to inputBuffer
        int count = socketChannel.read(inputBuffer);
        String command = new String(inputBuffer.array(), 0, count);
        //Clear the buffer task2.data to prepare for the next write
        inputBuffer.clear();
        if (command != null) {
            String[] datas = command.split("#");
            UserCommand commandSolver = CommandFactory.createCommand(datas[0]);
            String data = "";
            if (datas.length >= 2) {
                data = datas[1];
            }
            //Obtain the result of command processing
            String response = commandSolver.getResult(data);

            //The event of interest for switching the channel is write

            key.interestOps(SelectionKey.OP_WRITE);
            //The return will be bound to the key, where the key:
            // represents a certain tcp connection
            key.attach(response);
        }
    }

    public void write(SelectionKey key) throws IOException {

        //Get the task2.data that needs to be sent
        SocketChannel socketChannel = (SocketChannel) key.channel();
        String response = ((String) key.attachment());
        ByteBuffer block = ByteBuffer.wrap(response.getBytes());
        block.clear();
        block.put(response.getBytes());
        //Output to channel
        block.flip();//Switch to the read mode, because when writing task2.data
        // to the channel, it actually reads the task2.data from the buffer,
        // starting from the position, and then to the limit position.
        int i = socketChannel.write(block);
        System.out.println("Sent: " + i + " bytes of task2.data");
        System.out.println("The server sends task2.data to the client--：" + response);
        key.interestOps(SelectionKey.OP_READ);//Switch to the channel to be interested in reading events
    }
}
