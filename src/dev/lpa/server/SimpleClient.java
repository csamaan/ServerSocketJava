package dev.lpa.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5001);
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            String requestString;
            String responseString;

            do {
                System.out.println("Enter a string to be echoed (type \"exit\" to quit): ");
                requestString = scanner.nextLine();

                // Send request to server
                output.println(requestString);

                // Read server response ONLY if we aren't exiting
                if (!requestString.equalsIgnoreCase("exit")) {
                    responseString = input.readLine();
                    System.out.println("Echo from server: " + responseString);
                }
            } while (!requestString.equalsIgnoreCase("exit"));

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } finally {
            System.out.println("Client Disconnected");
        }
    }
}