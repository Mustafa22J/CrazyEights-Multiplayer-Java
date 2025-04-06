package network;

/**
 * Protocol defines the constants and helper methods for message formats
 * exchanged between the client and server during gameplay.
 *
 * Format used: &lt;Protocol_ID&gt;&lt;Separator&gt;&lt;Data1&gt;&lt;Separator&gt;&lt;Data2&gt;...
 * Example: "3#Player1:Hello"
 */
public class Protocol {

    /** Separator used in all protocol messages */
    public static final String SEP = "#";

    // Protocol identifiers
    public static final String DISCONNECT = "1";   // Player disconnects
    public static final String IDENTIFY   = "2";   // Player sends name (JOIN)
    public static final String CHAT       = "3";   // Chat message
    public static final String MOVE       = "4";   // Player plays card / makes move
    public static final String SUIT       = "5";   // Wild card suit change
    public static final String PLAYERS    = "6";   // Player list update
    public static final String TURN       = "7";   // Turn info or wild suit change
    public static final String ERROR      = "8";   // Error message or invalid action

    /**
     * Creates a formatted protocol message.
     *
     * @param type   the protocol identifier
     * @param params any number of string parameters to include
     * @return the complete formatted message string
     *
     * Example: Protocol.format(Protocol.CHAT, "Player1:Hi") → "3#Player1:Hi"
     */
    public static String format(String type, String... params) {
        return type + SEP + String.join(SEP, params);
    }

    /**
     * Splits a message into protocol ID and its components.
     *
     * @param message the full incoming message
     * @return an array of strings split by the protocol separator
     */
    public static String[] parse(String message) {
        return message.split(SEP, -1);
    }
}
