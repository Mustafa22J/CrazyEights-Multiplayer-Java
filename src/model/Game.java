package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the overall game state for Crazy Eights.
 * Stores players, deck, top card, and whose turn it is.
 * Used on both server and client side.
 */
public class Game {

    /** The draw deck used in the game */
    private Deck deck;

    /** The list of players in the game */
    private List<Player> players;

    /** The current top card on the discard pile */
    private Card topCard;

    /** Index of the current player's turn (0-3) */
    private int currentTurn;

    /**
     * No-args constructor (used by clients during SYNC/START).
     * Initializes an empty player list and fallback top card.
     */
    public Game() {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.topCard = new Card("8", "s"); // fallback default
        this.currentTurn = 0;
    }

    /**
     * Main game constructor (used by host/server).
     * Creates and deals cards to players and sets the starting discard pile.
     *
     * @param playerNames list of player names in joining order
     */
    public Game(List<String> playerNames) {
        this(); // call no-arg constructor

        for (String name : playerNames) {
            Player p = new Player(name);
            for (int i = 0; i < 12; i++) {
                p.addCard(deck.drawCard()); // deal 12 cards to each player
            }
            players.add(p);
        }

        topCard = deck.drawCard(); // set the starting discard pile
    }

    /**
     * Gets a player by name.
     * Used when syncing or applying a move.
     *
     * @param name player name
     * @return the matching Player object, or null if not found
     */
    public Player getPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return current Player
     */
    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }

    /**
     * Advances to the next player in turn order.
     * Wraps around to the beginning when needed.
     */
    public void nextTurn() {
        currentTurn = (currentTurn + 1) % players.size();
    }

    /**
     * Returns the index of the current player's turn.
     *
     * @return index between 0 and players.size() - 1
     */
    public int getCurrentTurnIndex() {
        return currentTurn;
    }

    /**
     * Sets the current turn index.
     * Used when syncing from server state.
     *
     * @param index turn index to set (0–3)
     */
    public void setCurrentTurnIndex(int index) {
        if (index >= 0 && index < players.size()) {
            currentTurn = index;
        }
    }

    /**
     * Sets the current player by name.
     * Used for syncing or recovery scenarios.
     *
     * @param name player name to make current turn
     */
    public void setCurrentPlayer(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                currentTurn = i;
                return;
            }
        }
    }

    /**
     * Gets the top card from the discard pile.
     *
     * @return top card
     */
    public Card getTopCard() {
        return topCard;
    }

    /**
     * Sets the top card of the discard pile.
     *
     * @param card the new top card
     */
    public void setTopCard(Card card) {
        this.topCard = card;
    }

    /**
     * Gets the list of players in the game.
     *
     * @return list of Player objects
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the current draw deck.
     *
     * @return deck instance
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Adds a new player only if they aren't already present.
     * Used for dynamic joining or syncing.
     *
     * @param name name of the player to add
     */
    public void addPlayerIfAbsent(String name) {
        if (getPlayer(name) == null) {
            players.add(new Player(name));
        }
    }
}
