package dev.lpa.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SimpleServerChannel {
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(5000));

            while(true){
                SocketChannel clientChannel = serverSocketChannel.accept();
                System.out.printf("Client %s connected%n",
                        clientChannel.socket().getRemoteSocketAddress());

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel channel = clientChannel;
                int readBytes = channel.read(buffer);
                if(readBytes > 0){
                    buffer.flip();
                    channel.write(ByteBuffer.wrap("Echo from server: ".getBytes()));
                    while(buffer.hasRemaining()){
                        channel.write(buffer);
                    }
                    buffer.clear();
                } else if(readBytes == -1){
                    System.out.printf("Connection to %s lost %n",
                            clientChannel.socket().getRemoteSocketAddress());
                    channel.close();
                }
            }
        } catch (IOException e) {
            System.err.println("problem: "+ e);
        }
    }
}
