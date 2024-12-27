import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        synchronized (clientHandlers) {
            clientHandlers.add(this);
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Get the client's name
            out.println("Enter your name:");
            clientName = in.readLine();
            broadcastMessage(clientName + " has joined the chat!");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from " + clientName + ": " + message);
                broadcastMessage(clientName + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (clientHandlers) {
                clientHandlers.remove(this);
            }
            broadcastMessage(clientName + " has left the chat.");
        }
    }

    private void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != this) { // Don't send to the sender
                    clientHandler.out.println(message);
                }
            }
        }
    }
}