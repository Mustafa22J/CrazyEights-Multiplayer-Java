package network;

import java.io.*;
import java.net.Socket;

/**
 * NetworkHandler manages input/output for a single client connection.
 * It handles reading from the socket, notifying listeners, or forwarding messages to the server.
 * It can act either on the client side (with listener) or server side (with server ref).
 */
public class NetworkHandler implements Runnable {

	/** The client socket connection */
    private final Socket socket;
    
    /** Buffered reader for incoming messages */
    private final BufferedReader reader;
    
    /** Writer for sending messages to the client */
    private final PrintWriter writer;
    
    /** Reference to the server (only used on server side) */
    private final Server server;
    
    /** Player's name, if already joined */
    private String playerName;
    
    /** Message listener callback (only used on client side) */
    private MessageListener listener;

    /**
     * Functional interface for receiving messages from the socket (client-side).
     */
    public interface MessageListener {
        void onMessageReceived(String message);
    }

    /**
     * Constructs a handler for a client-server socket connection.
     *
     * @param socket the connected socket
     * @param server the server instance (null if client-side)
     * @throws IOException if I/O streams fail to initialize
     */
    public NetworkHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true); // auto-flush
    }

    /**
     * Sets a message listener for client-side message handling.
     *
     * @param listener the listener to notify
     */
    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    /**
     * Sends a message to the connected client.
     *
     * @param message the message string
     */
    public void send(String message) {
        writer.println(message);
    }

    /**
     * Sets the player's name (usually from JOIN# message).
     *
     * @param name the player's name
     */
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    /**
     * Returns the player's name associated with this handler.
     *
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gracefully closes the socket connection.
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Socket close error: " + e.getMessage());
        }
    }

    /**
     * Main socket listener loop (run on separate thread).
     * Processes incoming messages and dispatches them to server or client listener.
     */
    @Override
    public void run() {
        String input;
        try {
            while ((input = reader.readLine()) != null) {
                // Handle player joining
                if (input.startsWith("JOIN#")) {
                    String name = input.substring(5).trim();
                    setPlayerName(name);
                    continue; // Don't broadcast JOIN message
                }

                // If on server side, route to server logic
                if (server != null) {
                    server.handleMessage(this, input);
                } 
                // If client-side, notify the local listener (e.g., GameController)
                else if (listener != null) {
                    listener.onMessageReceived(input);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection dropped.");
        } finally {
            close(); // Cleanup on disconnect
            if (server != null) {
                server.removeClient(this); // Notify server to remove this handler
            }
        }
    }
}
