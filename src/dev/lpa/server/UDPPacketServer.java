package dev.lpa.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class UDPPacketServer {

    private static final int PORT = 5000;
    private static final int PACKET_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[PACKET_SIZE];
            System.out.println("Awaiting client connection...");

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String audioFileName = new String(
                    packet.getData(),
                    0,
                    packet.getLength(),
                    StandardCharsets.UTF_8
            );
            System.out.println(
                    "Client requested to listen to: " + audioFileName
            );

            File audioFile = new File(audioFileName);
            if (!audioFile.exists()) {
                throw new FileNotFoundException(
                        "Audio file not found at path: " +
                                audioFile.getAbsolutePath()
                );
            }

            // Wrap AudioInputStream in try-with-resources to ensure proper stream closing
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
                System.out.println("Total Audio Frame Length: " + audioInputStream.getFrameLength());
            } catch (UnsupportedAudioFileException e) {
                System.err.println(
                        "Audio format not supported: " + e.getMessage()
                );
            }
            sendDataToClient(audioFileName, socket, packet);
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
    private static void sendDataToClient(String file, DatagramSocket serverSocket, DatagramPacket clientPacket) {
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);
        try(FileChannel fileChannel = FileChannel.open(Paths.get(file), StandardOpenOption.READ)) {
            InetAddress clientIP = clientPacket.getAddress();
            int clientPort = clientPacket.getPort();
            while(true) {
                buffer.clear();
                if(fileChannel.read(buffer)==-1) {
                    break;
                }
                buffer.flip();
                while(buffer.hasRemaining()) {
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    DatagramPacket packet = new DatagramPacket(data, data.length,
                            clientIP, clientPort);
                    serverSocket.send(packet);
                }
                try{
                    //if your playback starts skipping, please adjust the speed of the timeout to 10 or 15
                    TimeUnit.MILLISECONDS.sleep(15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}