package view;

import model.Card;
import model.Game;
import model.Player;

import javax.swing.*;

import controller.MenuSystem;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * GameView handles all UI rendering and interactions
 * This class is the View in the MVC pattern.
 * for the multiplayer Crazy Eights game.
 * It displays players, cards, chat panel, and game state updates.
 */
public class GameView extends JPanel {

    private static final long serialVersionUID = 1L;
    
 // Chat UI components
    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;
    private JLabel statusLabel;
    
 // Game interaction components
    private JButton drawButton;
    
 // Event listeners for game actions
    private Consumer<Card> cardClickListener;
    private Consumer<String> chatListener;
    private Runnable drawListener;
    
 // Panels for layout positioning
    private JPanel centerPanel, bottomPanel, topPanel, leftPanel, rightPanel;

    /**
     * Constructor initializes layout and chat panel.
     */
    public GameView() {
        setLayout(new BorderLayout());
        setupChatPanel();
    }

    /**
     * Sets up the chat area on the right side of the UI.
     */
    private void setupChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        chatInput = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        statusLabel = new JLabel("Waiting for players...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        chatPanel.add(statusLabel, BorderLayout.NORTH);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(200, getHeight()));

        sendButton.addActionListener(e -> {
            if (chatListener != null) {
                chatListener.accept(chatInput.getText());
                chatInput.setText(""); // Clear after sending
            }
        });

        add(chatPanel, BorderLayout.EAST);
    }

    /**
     * Updates the status label above the chat panel.
     *
     * @param message The message to show (e.g. "Your turn", "Waiting...")
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Renders the entire game state on screen based on model data.
     *
     * @param game             The game model
     * @param currentPlayerName The name of the player viewing the screen
     */
    public void render(Game game, String currentPlayerName) {
        removeAll();
        setupChatPanel(); // Chat panel must be re-added

        BackgroundPanel gamePanel = new BackgroundPanel();
        gamePanel.setLayout(new BorderLayout());
        add(gamePanel, BorderLayout.CENTER);

        // Layout containers for each position
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setOpaque(false);

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);

        leftPanel = new JPanel(new GridLayout(1, 1));
        leftPanel.setOpaque(false);

        rightPanel = new JPanel(new GridLayout(1, 1));
        rightPanel.setOpaque(false);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Center piles: draw and discard
        JPanel pilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pilePanel.setOpaque(false);

        JLabel drawPile = new JLabel(loadImage("Assets/cards/back.png"));
        JLabel discardPile = new JLabel(loadImage("Assets/cards/" + game.getTopCard().toString() + ".png"));
        pilePanel.add(drawPile);
        pilePanel.add(discardPile);

        drawButton = new JButton(MenuSystem.getMessages().getString("label.draw_card")); // Localized "Draw Card"
        drawButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        drawButton.addActionListener(e -> {
            if (drawListener != null) drawListener.run();
        });

        centerPanel.add(pilePanel);
        centerPanel.add(drawButton);

        // Draw players
        List<Player> players = game.getPlayers();
        Player you = players.stream()
                .filter(p -> p.getName().equals(currentPlayerName))
                .findFirst().orElse(null);

        if (you == null) {
            System.err.println("You not found in player list! UI will still render.");
            revalidate();
            repaint();
            return;
        }

        int yourIndex = players.indexOf(you);
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int pos = (i - yourIndex + 4) % 4; // rotate layout: bottom/right/top/left

            switch (pos) {
            case 0:
                renderBottomPlayer(p);
                break;
            case 1:
                renderRightPlayer(p);
                break;
            case 2:
                renderTopPlayer(p);
                break;
            case 3:
                renderLeftPlayer(p);
                break;
            }
        }

        // Add all player panels
        gamePanel.add(topPanel, BorderLayout.NORTH);
        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
        gamePanel.add(leftPanel, BorderLayout.WEST);
        gamePanel.add(rightPanel, BorderLayout.EAST);
        gamePanel.add(centerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Renders the player's own hand at the bottom of the screen.
     */
    private void renderBottomPlayer(Player p) {
        JPanel handPanel = new JPanel(null); // Absolute layout
        handPanel.setOpaque(false);

        int x = 0;
        int y = 0;

        List<Card> hand = p.getHand();
        for (int i = 0; i < hand.size(); i++) {
            boolean isLast = (i == hand.size() - 1);
            String imgPath = isLast
                    ? "Assets/cards/" + hand.get(i) + ".png"
                    : "Assets/cards/l" + hand.get(i) + ".png";

            JButton btn = new JButton(loadImage(imgPath));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);

            int cardWidth = btn.getPreferredSize().width;
            int cardHeight = btn.getPreferredSize().height;

            btn.setBounds(x, y, cardWidth, cardHeight);
            handPanel.add(btn);

            Card finalC = hand.get(i);
            btn.addActionListener(e -> {
                if (cardClickListener != null) cardClickListener.accept(finalC);
            });

            x += isLast ? cardWidth - 12 : 17; // overlap cards
        }

        handPanel.setPreferredSize(new Dimension(x + 40, 100));

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);

        JLabel label = new JLabel(p.getName() + " | Score: " + p.getScore());
        label.setForeground(Color.BLUE);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        namePanel.setOpaque(false);
        namePanel.add(label);

        bottomContainer.add(namePanel, BorderLayout.NORTH);
        bottomContainer.add(handPanel, BorderLayout.CENTER);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.add(bottomContainer);
    }
    
    /**
     * Renders a top player (relative position) with face-down cards.
     * @param p the player to render
     */
    private void renderTopPlayer(Player p) {
        JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 5));
        handPanel.setOpaque(false);

        for (int i = 0; i < p.getHand().size(); i++) {
            String path = (i == 0) ? "Assets/cards/back.png" : "Assets/cards/lback.png";
            handPanel.add(new JLabel(loadImage(path)));
        }

        JLabel label = new JLabel(p.getName() + " | Score: " + p.getScore());
        label.setForeground(Color.BLUE);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(label, BorderLayout.SOUTH);
        container.add(handPanel, BorderLayout.CENTER);
        topPanel.add(container);
    }
    
    /**
     * Renders a left player (relative position) with face-down cards.
     * @param p the player to render
     */
    private void renderLeftPlayer(Player p) {
        JPanel handPanel = new JPanel();
        handPanel.setOpaque(false);
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < p.getHand().size(); i++) {
            String path = (i == 0) ? "Assets/cards/Fback.png" : "Assets/cards/Flback.png";
            handPanel.add(new JLabel(loadImage(path)));
        }

        JLabel label = new JLabel(p.getName() + " | Score: " + p.getScore());
        label.setForeground(Color.BLUE);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(label, BorderLayout.NORTH);
        container.add(handPanel, BorderLayout.CENTER);
        leftPanel.add(container);
    }
    
    /**
     * Renders a right player (relative position) with face-down cards.
     * @param p the player to render
     */
    private void renderRightPlayer(Player p) {
        JPanel handPanel = new JPanel();
        handPanel.setOpaque(false);
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < p.getHand().size(); i++) {
            String path = (i == 0) ? "Assets/cards/Fback.png" : "Assets/cards/Flback.png";
            handPanel.add(new JLabel(loadImage(path)));
        }

        JLabel label = new JLabel(p.getName() + " | Score: " + p.getScore());
        label.setForeground(Color.BLUE);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(label, BorderLayout.NORTH);
        container.add(handPanel, BorderLayout.CENTER);
        rightPanel.add(container);
    }

    /**
     * Background image panel used behind game elements.
     */
    private static class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = new ImageIcon(getClass().getClassLoader().getResource("Assets/Background.png")).getImage();
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Loads an image from disk for use in the UI.
     *
     * @param path image file path
     * @return loaded ImageIcon
     */
    private ImageIcon loadImage(String path) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return new ImageIcon(); // fallback empty icon
        }
    }
    
    /**
     * Sets the card click event listener.
     * @param listener callback for card click events
     */
    public void setCardClickListener(Consumer<Card> listener) {
        this.cardClickListener = listener;
    }
    
    /**
     * Sets the chat input listener.
     * @param listener callback for chat message send
     */
    public void setChatListener(Consumer<String> listener) {
        this.chatListener = listener;
    }
    
    /**
     * Sets the draw button click listener.
     * @param listener callback for draw action
     */
    public void setDrawListener(Runnable listener) {
        this.drawListener = listener;
    }

    /**
     * Appends a chat message to the chat area.
     *
     * @param message message string
     */
    public void appendChat(String message) {
        chatArea.append(message + "\n");
    }
}
