package problems.task1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: <HOSTNAME> <PORT>");
            System.exit(1);
        }

        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        socketChannel.bind(socketAddress);
        socketChannel.configureBlocking(false);

        int ops = socketChannel.validOps();
        SelectionKey selectKy = socketChannel.register(selector, ops, null);

        boolean isWorking = true;
        while (isWorking) {
            log("[Server] Waiting for new connection and buffer select...");
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();

                if (selectionKey.isAcceptable()) {
                    SocketChannel client = socketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);

                    log("[Server] Connection Accepted: " + client.getLocalAddress() + "\n");
                } else if (selectionKey.isReadable()) {

                    SocketChannel client = (SocketChannel) selectionKey.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    client.read(buffer);
                    String result = new String(buffer.array()).trim();

                    log("[Server] Message received: " + result);

                    if (result.equals("logout")) {
                        client.close();
                        log("\n[Server] Keeping running. Try using another client to connect again.");
                    }

                    if (result.equals("shutdown")) {
                        client.close();
                        log("\n[Server] Shutting down");
                        isWorking = false;
                    }
                }
                selectionKeyIterator.remove();
            }
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}
