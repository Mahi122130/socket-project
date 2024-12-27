import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1834;
    private static String clientName;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;gfcg
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Connect to the server
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read server welcome message
            System.out.println(in.readLine());  // "Enter your name:"

            // Ask user for their name
            System.out.print("Enter your name: ");
            clientName = scanner.nextLine();
            out.println(clientName);  // Send name to the server

            // Start a thread to listen for incoming messages
            new Thread(new IncomingMessageListener()).start();

            // Allow the user to send messages
            sendMessageToServer();

        } catch (IOException e) {
            System.err.println("Error: Unable to connect to server.");
            e.printStackTrace();
        }
    }

    // Method to send messages to the server
    private static void sendMessageToServer() {
        String message;
        while (true) {
            System.out.print("You: ");
            message = scanner.nextLine();

            // Exit condition: if message is "bye", disconnect from server
            if (message.equalsIgnoreCase("bye")) {
                System.out.println("Exiting the chat. Goodbye!");
                break;
            }

            // Send the message to the server
            out.println(message);

            // Wait for acknowledgment from server
            try {
                String serverAck = in.readLine();  // Server acknowledgment
                System.out.println("Server: " + serverAck);
            } catch (IOException e) {
                System.err.println("Error while receiving acknowledgment.");
            }
        }

        // Close socket and streams after exiting the chat
        closeResources();
    }

    // Close socket and streams
    private static void closeResources() {
        try {
            socket.close();
            out.close();
            in.close();
            scanner.close();
        } catch (IOException e) {
            System.err.println("Error while closing resources.");
        }
    }

    // Class to listen for incoming messages from the server
    private static class IncomingMessageListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.err.println("Error: Unable to read message from server.");
            }
        }
    }
}
