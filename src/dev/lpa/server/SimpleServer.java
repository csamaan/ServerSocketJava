package dev.lpa.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
          Socket socket = serverSocket.accept();
          System.out.println("Connected to " + socket.getInetAddress().getHostName());
          
        } catch(IOException e){
        System.out.println("Server exception " + e.getMessage());
    }
}
