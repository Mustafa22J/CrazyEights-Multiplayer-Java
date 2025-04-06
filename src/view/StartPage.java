package view;

import controller.GameController;
import controller.MenuSystem;
import model.Game;
import network.Client;
import network.CustomDialog;
import network.Server;

import javax.swing.*;
import java.awt.*;

/**
 * The StartPage class is the main entry point for the Crazy Eights game.
 * It shows a splash screen, prompts the user to host or join a game,
 * then sets up networking and initializes the game UI.
 */
public class StartPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private CustomDialog dialog;
    private Game game;
    private Client client;
    private GameView gameView;
    private GameController controller;

    /**
     * Constructs the main window, sets initial UI and starts the splash screen timer.
     */
    public StartPage() {
        setTitle("Crazy Eights - Multiplayer");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Splash screen
        ImageIcon splashIcon = new ImageIcon(getClass().getResource("/Assets/StartPage.jpg"));
        Image splashImg = splashIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        JLabel splashLabel = new JLabel(new ImageIcon(splashImg));
        splashLabel.setSize(getSize());
        add(splashLabel, BorderLayout.CENTER);
        setVisible(true);

        // Launch network setup after splash
        Timer timer = new Timer(2000, e -> {
            getContentPane().removeAll();
            revalidate();
            repaint();
            setupNetworkDialog();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Prompts the player to host or join a game, sets up the network connection,
     * initializes the game view and connects the message handler.
     */
    private void setupNetworkDialog() {
        dialog = new CustomDialog(this);
        dialog.setVisible(true);
        if (!dialog.isSubmitted()) return;

        final String name = dialog.getPlayerName();
        final int port = dialog.getPort();
        final boolean isHost = dialog.isHost();

        // Start server if host
        if (isHost) {
            Server server = new Server(port, dialog);
            server.start();
        }

        // Initialize view and menu
        gameView = new GameView();
        SwingUtilities.invokeLater(() -> {
            setJMenuBar(MenuSystem.createMenu(this));
            setContentPane(gameView);
            revalidate();
            repaint();
        });

        // Connect client and set message listener
        client = new Client(dialog.getHostAddress(), port);
        client.connect(name, message -> {
            // Initialize game on first START message
            if (message.startsWith("START#") && controller == null) {
                game = new Game();
                controller = new GameController(game, gameView, client, name);
            }

            // Update waiting status (ex: "2/4 players connected")
            if (message.matches("^\\d/4 players connected$") || message.startsWith("All players connected")) {
                gameView.updateStatus(message);
            }

            // Forward message to controller once it's created
            if (controller != null) {
                controller.handleIncomingMessage(message);
            }
        });
    }

    /**
     * Main method to launch the game.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartPage::new);
    }
}
