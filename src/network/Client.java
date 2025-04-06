package network;

import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 * The Client class handles the network connection from the player's side.
 * It connects to the server, sends and receives messages using the NetworkHandler.
 */
public class Client {
    /** The server IP or hostname to connect to */
    private final String host;

    /** The port number to connect to on the server */
    private final int port;

    /** The NetworkHandler used to send/receive messages */
    private NetworkHandler handler;

    /**
     * Constructs a client with a specified server address and port.
     *
     * @param host the server IP or hostname
     * @param port the port number to connect to
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Connects to the server and starts a NetworkHandler thread.
     *
     * @param playerName the name of the player joining
     * @param listener the listener to handle incoming messages
     */
    public void connect(String playerName, NetworkHandler.MessageListener listener) {
        try {
            Socket socket = new Socket(host, port); // Establish socket connection
            handler = new NetworkHandler(socket, null);
            handler.setListener(listener);
            new Thread(handler).start(); // Run the handler on a new thread

            send("JOIN#" + playerName); // Send initial join message to server
        } catch (IOException e) {
            // Show error dialog if connection fails
            JOptionPane.showMessageDialog(
                null,
                "Connection failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Sends a message to the server via the handler.
     *
     * @param message the message to send
     */
    public void send(String message) {
        if (handler != null) {
            handler.send(message);
        }
    }

    /**
     * Disconnects from the server and closes the connection.
     */
    public void disconnect() {
        if (handler != null) {
            handler.send(Protocol.DISCONNECT + Protocol.SEP + "bye"); // Notify disconnection
            handler.close(); // Close socket
        }
    }
}
