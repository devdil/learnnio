import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockingServerExample {

    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){

            int hasToBeReadv = selector.select();
            Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();

            while (selectionKeys.hasNext()){
                SelectionKey selectionKey = selectionKeys.next();
                selectionKeys.remove();

                if (selectionKey.isAcceptable()){
                    System.out.println("got an accept");
                    ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                else if (selectionKey.isReadable()){
                    System.out.println("Something came into socket, lets read");
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    socketChannel.read(buffer);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
                else if (selectionKey.isWritable()){
                    System.out.println("Something came into socket, lets write");
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer  byteBuffer = (ByteBuffer) selectionKey.attachment();
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);

                    if (byteBuffer.hasRemaining()){
                        byteBuffer.compact();
                    }
                    else{
                        byteBuffer.clear();
                    }
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
