package dev.lpa.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
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
            try (
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                            audioFile
                    )
            ) {
                System.out.println(
                        "Total Audio Frame Length: " +
                                audioInputStream.getFrameLength()
                );
            } catch (UnsupportedAudioFileException e) {
                System.err.println(
                        "Audio format not supported: " + e.getMessage()
                );
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}