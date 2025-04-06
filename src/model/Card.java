package model;

import java.io.Serializable;

/**
 * Represents a single playing card in the Crazy Eights game.
 * Cards consist of a rank (e.g., "8", "K", "10") and a suit (e.g., "h", "d", "c", "s").
 *
 * This class is serializable and used across the network to represent game state.
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Suit of the card: "h" (hearts), "d" (diamonds), "c" (clubs), "s" (spades) */
    private final String suit;

    /** Rank of the card: A, 2–10, J, Q, K. "8" is a wild card. */
    private final String rank;

    /**
     * Constructs a card with the given rank and suit.
     *
     * @param rank the rank of the card (e.g., "8", "K")
     * @param suit the suit of the card (e.g., "h" for hearts)
     */
    public Card(String rank, String suit) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Gets the suit of the card.
     *
     * @return suit letter (e.g., "h")
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Gets the rank of the card.
     *
     * @return rank string (e.g., "8", "K")
     */
    public String getRank() {
        return rank;
    }

    /**
     * Returns the image file path used to render this card in the UI.
     *
     * @return path string to the image asset
     */
    public String getImagePath() {
        return "Assets/cards/" + rank + suit + ".png";
    }

    /**
     * Parses a string like "8s" or "10d" into a Card object.
     * Used in network sync messages to transfer card state.
     *
     * @param str card string (e.g., "7d")
     * @return parsed Card object or null if input is invalid
     */
    public static Card fromString(String str) {
        if (str == null || str.length() < 2) return null;
        String rank = str.substring(0, str.length() - 1);
        String suit = str.substring(str.length() - 1);
        return new Card(rank, suit);
    }

    /**
     * Converts this card to string format used in protocol messages.
     * Example: "8s", "Qh", "10d"
     *
     * @return card string in rank+suit format
     */
    @Override
    public String toString() {
        return rank + suit;
    }

    /**
     * Determines if this card can be played on top of the given card.
     * Valid matches are by rank, suit, or if this card is an "8" (wild).
     *
     * @param other the card to compare against
     * @return true if this card is a valid move on top of the other
     */
    public boolean matches(Card other) {
        return this.rank.equals(other.rank)
            || this.suit.equals(other.suit)
            || this.rank.equals("8");
    }
}
