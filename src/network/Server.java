package network;

import model.*;
import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class for hosting a Crazy Eights multiplayer game.
 * Accepts incoming player connections, manages game state and
 * broadcasts messages to all connected clients.
 */
public class Server {
	/** Port number the server listens on. */
    private final int port;
    
    /** List of currently connected clients. */
    private final List<NetworkHandler> clients = new ArrayList<>();
    
    /** Reference to the host UI dialog for status updates. */
    private final CustomDialog dialog;
    
    /** The active game instance (logic and state). */
    private Game game;

    /**
     * Constructs a Server instance with specified port and dialog for UI updates.
     *
     * @param port   Port to host the server on (10000-65535).
     * @param dialog UI dialog for status updates (host side).
     */
    public Server(int port, CustomDialog dialog) {
        this.port = port;
        this.dialog = dialog;
    }

    /**
     * Starts the server on a separate thread, accepting up to 4 players.
     * Once all players are connected and named, the game is initialized and started.
     */
    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                updateStatus("Waiting for players...");

                // Accept up to 4 players
                while (clients.size() < 4) {
                    Socket clientSocket = serverSocket.accept();
                    NetworkHandler handler = new NetworkHandler(clientSocket, this);
                    clients.add(handler);
                    new Thread(handler).start();

                    String status = clients.size() + "/4 players connected";
                    updateStatus(status);
                    broadcast(status);  // Show connection info to all clients
                }

                String waitingMsg = "All players connected. Waiting for player names...";
                updateStatus(waitingMsg);
                broadcast(waitingMsg);  // Inform players on client UI

                // Wait for all players to send their names via JOIN#
                while (clients.stream().anyMatch(c -> c.getPlayerName() == null)) {
                    Thread.sleep(100);
                }

                updateStatus("All players identified. Starting game...");
                Thread.sleep(500);
                dialog.dispose();  // Close the host's waiting dialog

                initializeGame();  // Create game state with player names
                broadcast(buildStartMessage());  // Sync game to all clients

            } catch (IOException | InterruptedException e) {
                updateStatus("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Initializes the game with player names collected from handlers.
     */
    private void initializeGame() {
        List<String> names = new ArrayList<>();
        for (NetworkHandler handler : clients) {
            if (handler.getPlayerName() != null) {
                names.add(handler.getPlayerName());
            }
        }
        game = new Game(names);
    }

    /**
     * Builds the full START message to send game state to all clients.
     *
     * @return formatted game state sync string
     */
    private String buildStartMessage() {
        StringBuilder builder = new StringBuilder("START#");
        builder.append(game.getTopCard().toString()).append("|");
        builder.append("TURN:").append(game.getCurrentTurnIndex()).append("|");

        for (Player p : game.getPlayers()) {
            builder.append(p.getName()).append(":");
            for (Card c : p.getHand()) {
                builder.append(c.toString()).append(",");
            }
            builder.append(";");
        }

        return builder.toString();
    }

    /**
     * Updates the host dialog status label (thread-safe).
     *
     * @param msg status text
     */
    private void updateStatus(String msg) {
        if (dialog != null) {
            SwingUtilities.invokeLater(() -> dialog.setStatus(msg));
        }
    }

    /**
     * Sends a message to all connected clients.
     *
     * @param message the message to send
     */
    public synchronized void broadcast(String message) {
        for (NetworkHandler handler : clients) {
            handler.send(message);
        }
    }

    /**
     * Removes a disconnected client from the list.
     *
     * @param handler the disconnected client
     */
    public synchronized void removeClient(NetworkHandler handler) {
        clients.remove(handler);
        updateStatus(clients.size() + "/4 players connected");
    }

    /**
     * Handles incoming messages from any client.
     *
     * @param from    the client sending the message
     * @param message the message received
     */
    public synchronized void handleMessage(NetworkHandler from, String message) {
        if (message.startsWith("JOIN#")) {
            from.setPlayerName(message.substring(5));
            return;
        }

        // Relay the message to all players (chat, moves, sync, etc.)
        broadcast(message);
    }
}
