package controller;

import model.*;
import network.Client;
import network.Protocol;
import view.GameView;

import javax.swing.*;

/**
 * The GameController handles the interaction between the Game model,
 * GameView UI, and Client networking logic.
 * <p>
 * It listens for UI actions, processes player moves, manages synchronization
 * between clients, and updates the view accordingly.
 *
 * @author YourName
 */
public class GameController {

    /** The local game state model */
    private final Game game;

    /** The UI component that displays the game */
    private final GameView view;

    /** The network client used to send/receive messages */
    private final Client client;

    /** The name of the current local player */
    private final String playerName;

    /**
     * Constructs the GameController and initializes UI event listeners.
     *
     * @param game        the game model
     * @param view        the game view
     * @param client      the network client
     * @param playerName  the current player's name
     */
    public GameController(Game game, GameView view, Client client, String playerName) {
        this.game = game;
        this.view = view;
        this.client = client;
        this.playerName = playerName;

        initListeners(); // Setup UI callbacks
    }

    /**
     * Sets up listeners for card clicks, draw button, and chat messages.
     */
    private void initListeners() {
        // Card click listener
        view.setCardClickListener(card -> {
            if (!isYourTurn()) {
                JOptionPane.showMessageDialog(view, "Not your turn!");
                return;
            }

            if (!card.matches(game.getTopCard()) && !card.getRank().equals("8")) {
                JOptionPane.showMessageDialog(view, "Invalid card.");
                return;
            }

            game.getPlayer(playerName).removeCard(card);
            game.setTopCard(card);

            // Handle wild card (8)
            if (card.getRank().equals("8")) {
                String[] suits = {"h", "d", "c", "s"};
                String chosenSuit = (String) JOptionPane.showInputDialog(
                        view,
                        MenuSystem.getMessages().getString("label.change_suit"),
                        "Wild Card",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        suits,
                        "h"
                );
                if (chosenSuit != null) {
                    game.setTopCard(new Card("8", chosenSuit));
                    client.send(Protocol.TURN + Protocol.SEP + "SUIT:" + chosenSuit);
                }
            }

            if (game.getPlayer(playerName).getHand().isEmpty()) {
                client.send(Protocol.DISCONNECT + Protocol.SEP + playerName + " won!");
                JOptionPane.showMessageDialog(view, "You won!");
            }

            game.nextTurn();
            syncGameState();
        });

        // Draw card listener
        view.setDrawListener(() -> {
            if (!isYourTurn()) return;

            Card drawn;
            do {
                drawn = game.getDeck().drawCard();
                if (drawn == null) {
                    JOptionPane.showMessageDialog(view, "Deck is empty.");
                    return;
                }
                game.getPlayer(playerName).addCard(drawn);
            } while (!drawn.matches(game.getTopCard()));

            syncGameState();
        });

        // Chat message listener
        view.setChatListener(msg ->
                client.send(Protocol.CHAT + Protocol.SEP + playerName + ": " + msg));
    }

    /**
     * Checks if it is currently this player's turn.
     *
     * @return true if it's this player's turn
     */
    private boolean isYourTurn() {
        return game.getCurrentPlayer().getName().equals(playerName);
    }

    /**
     * Handles incoming messages from the server or other players.
     *
     * @param message the raw message received
     */
    public void handleIncomingMessage(String message) {
        System.out.println("Incoming message: " + message);

        String[] parts = message.split(Protocol.SEP, 2);
        if (parts.length < 2) return;

        switch (parts[0]) {
            case Protocol.CHAT:
                SwingUtilities.invokeLater(() -> view.appendChat(parts[1]));
                break;

            case Protocol.TURN:
                if (parts[1].startsWith("SUIT:")) {
                    String suit = parts[1].substring(5);
                    game.setTopCard(new Card("8", suit));
                    updateUI();
                }
                break;

            case Protocol.DISCONNECT:
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(view, "Game over! " + parts[1]));
                break;

            case "START":
            case "SYNC":
                parseGameState(parts[1]);
                updateUI();
                break;

            default:
                System.err.println("Unhandled protocol: " + parts[0]);
        }
    }

    /**
     * Builds and sends the full game state to the server for synchronization.
     */
    private void syncGameState() {
        StringBuilder sync = new StringBuilder("SYNC#");
        sync.append(game.getTopCard().toString());
        sync.append("|TURN:").append(game.getCurrentTurnIndex()).append("|");

        for (Player p : game.getPlayers()) {
            sync.append(p.getName()).append(":");
            for (Card c : p.getHand()) {
                sync.append(c.toString()).append(",");
            }
            sync.append(";");
        }

        client.send(sync.toString());
    }

    /**
     * Parses the incoming game state from the server.
     *
     * @param data the SYNC/START data string
     */
    private void parseGameState(String data) {
        System.out.println("Parsing state: " + data);

        if (!data.contains("|TURN:")) {
            System.err.println("Invalid sync format: " + data);
            return;
        }

        try {
            String[] split = data.split("\\|", 3);
            String topCardStr = split[0];
            int turnIndex = Integer.parseInt(split[1].replace("TURN:", ""));
            String playersBlock = split[2];

            game.setTopCard(new Card(
                    topCardStr.substring(0, topCardStr.length() - 1),
                    topCardStr.substring(topCardStr.length() - 1)
            ));
            game.setCurrentTurnIndex(turnIndex);

            String[] playersData = playersBlock.split(";");
            for (String pdata : playersData) {
                if (pdata.trim().isEmpty()) continue;

                String[] parts = pdata.split(":");
                if (parts.length < 1) continue;

                String pname = parts[0].trim();
                Player player = game.getPlayer(pname);
                if (player == null) {
                    player = new Player(pname);
                    game.getPlayers().add(player);
                }

                player.clearHand();

                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    String[] cards = parts[1].split(",");
                    for (String cStr : cards) {
                        if (!cStr.trim().isEmpty()) {
                            Card card = new Card(
                                    cStr.substring(0, cStr.length() - 1),
                                    cStr.substring(cStr.length() - 1)
                            );
                            player.addCard(card);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to parse game state: " + e.getMessage());
        }
    }

    /**
     * Updates the game view and status messages.
     * Ensures rendering is done on the Event Dispatch Thread.
     */
    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            view.render(game, playerName);

            String currentTurnName = game.getCurrentPlayer().getName();
            int playerCount = game.getPlayers().size();

            if (game.getPlayer(playerName).getHand().isEmpty()) {
                view.updateStatus("You won!");
            } else if (currentTurnName.equals(playerName)) {
                view.updateStatus("It's YOUR turn! (" + playerCount + "/4 players)");
            } else {
                view.updateStatus("Waiting for " + currentTurnName + " (" + playerCount + "/4 players)");
            }
        });
    }
}
