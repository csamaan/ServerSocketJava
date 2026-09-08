package dev.lpa.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5001)) {
            System.out.println("Server listening on port 5001...");
            Socket socket = serverSocket.accept();
            System.out.println("Connected to " + socket.getInetAddress().getHostName());

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String echoString = input.readLine();

                // Safe check against null or "exit"
                if (echoString == null || "exit".equalsIgnoreCase(echoString)) {
                    System.out.println("Client disconnected or sent exit. Server closing.");
                    break;
                }

                System.out.println("Server got request data: " + echoString);
                output.println("Echo from server: " + echoString);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}