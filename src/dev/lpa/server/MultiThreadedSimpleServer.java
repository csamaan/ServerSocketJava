package dev.lpa.server;

import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedSimpleServer {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server listening on port 5000...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostName());

                socket.setSoTimeout(900_0000);
                executorService.submit(() -> handleClientRequest(socket) );
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    private static void handleClientRequest(Socket socket){
        try(socket;
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output =
                    new PrintWriter(socket.getOutputStream(), true)){
            while (true) {
                String echoString = input.readLine();
                System.out.println("Server got request data: " + echoString);
                if(echoString.equals("exit")){
                    break;
                }
                output.println("Echo from server: " + echoString);
            }
        } catch (Exception e){
            System.out.println("Client handler exception: " + e.getMessage());
        }
    }
}


// ClientHandler class handles individual client communication on a separate thread
class ClientHandler extends Thread {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            while (true) {
                String echoString = input.readLine();

                if (echoString == null || "exit".equalsIgnoreCase(echoString)) {
                    System.out.println("Client disconnected or sent exit.");
                    break;
                }

                System.out.println("Server got request data: " + echoString);
                output.println("Echo from server: " + echoString);
            }
        } catch (IOException e) {
            System.out.println("Client handler exception: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Failed to close socket: " + e.getMessage());
            }
        }
    }


}