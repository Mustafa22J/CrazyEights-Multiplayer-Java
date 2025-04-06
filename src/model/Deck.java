package model;

import java.util.*;

/**
 * Represents the deck of cards used in the Crazy Eights game.
 * Initializes a full 52-card deck and provides draw functionality.
 */
public class Deck {

    /** Stack of cards representing the draw pile */
    private final Stack<Card> cards;

    /**
     * Constructs a full, shuffled 52-card deck.
     * Includes all 13 ranks (A, 2–10, J, Q, K) for each of the 4 suits.
     */
    public Deck() {
        cards = new Stack<>();

        String[] suits = {"h", "d", "c", "s"}; // Hearts, Diamonds, Clubs, Spades
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "1", "J", "Q", "K"}; 
        // Note: "1" represents "10" due to display filename convention

        // Create one card for each rank and suit (52 total)
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }

        // Shuffle the deck to randomize card order
        Collections.shuffle(cards);
    }

    /**
     * Draws the top card from the deck.
     *
     * @return the top Card, or null if the deck is empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.pop(); // Removes and returns the top card
    }

    /**
     * Checks whether the deck is empty.
     *
     * @return true if no cards remain in the deck
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
