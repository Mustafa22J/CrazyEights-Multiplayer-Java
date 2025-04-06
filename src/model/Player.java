package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Crazy Eights game.
 * Each player has a name, a hand of cards, and a running score.
 * This class is serializable for network synchronization.
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The player's display name */
    private final String name;

    /** The player's current hand of cards */
    private final List<Card> hand;

    /** The player's current score */
    private int score = 0;

    /**
     * Constructs a new player with a given name and an empty hand.
     *
     * @param name the player's display name
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param c the card to add
     */
    public void addCard(Card c) {
        hand.add(c);
    }

    /**
     * Removes a card from the player's hand.
     *
     * @param c the card to remove
     */
    public void removeCard(Card c) {
        hand.remove(c);
    }

    /**
     * Returns the list of cards currently in the player's hand.
     *
     * @return list of Card objects
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Clears all cards from the player's hand.
     * Used when syncing state or starting a new game.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Gets the player's name.
     *
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Determines if the player has at least one playable card.
     *
     * @param top the top card on the discard pile
     * @return true if the player can play any card
     */
    public boolean hasPlayableCard(Card top) {
        for (Card c : hand) {
            if (c.matches(top)) return true;
        }
        return false;
    }

    /**
     * Gets the player's score.
     *
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the player's score.
     *
     * @param score the new score value
     */
    public void setScore(int score) {
        this.score = score;
    }
}
