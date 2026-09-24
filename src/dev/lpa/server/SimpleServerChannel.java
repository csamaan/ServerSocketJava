package dev.lpa.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class SimpleServerChannel {
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(5000));
            serverSocketChannel.configureBlocking(false);
            List<SocketChannel> clientChannels = new ArrayList<>();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while(true){
                SocketChannel clientChannel = serverSocketChannel.accept();
                if(clientChannel != null) {
                    clientChannel.configureBlocking(false);
                    clientChannels.add(clientChannel);
                    System.out.printf("Client %s connected%n",
                            clientChannel.socket().getRemoteSocketAddress());
                }
                for(int i = 0; i < clientChannels.size(); i++){
                SocketChannel channel = clientChannels.get(i);
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
                            channel.socket().getRemoteSocketAddress());
                    channel.close();
                    clientChannels.remove(i);
                    i--;
                }
                }
            }
        } catch (IOException e) {
            System.err.println("Problem: "+ e);
        }
    }
}
