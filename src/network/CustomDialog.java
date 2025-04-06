package network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * CustomDialog handles the pre-game connection dialog.
 * Allows the user to host or join a game by entering relevant connection info.
 * Provides connection status updates through a status label.
 */
public class CustomDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /** Host-side name input field */
    private JTextField nameFieldHost;

    /** Host-side port input field */
    private JTextField portFieldHost;

    /** Client-side name input field */
    private JTextField nameFieldClient;

    /** Client-side port input field */
    private JTextField portFieldClient;

    /** Client-side address input field */
    private JTextField addressFieldClient;

    /** Label used to display connection status messages */
    private JLabel statusLabel;

    /** True if user selected "Host" */
    private boolean isHost;

    /** True if user clicked Host or Connect (submitted form) */
    private boolean submitted;

    /**
     * Constructs the dialog window with Host and Client tabs.
     *
     * @param parent the parent frame to position relative to
     */
    public CustomDialog(Frame parent) {
        super(parent, "Connect to Game", true);
        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Host", createHostPanel());
        tabbedPane.add("Client", createClientPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Shared status label at the bottom
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Creates the panel for hosting the game.
     */
    private JPanel createHostPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        nameFieldHost = new JTextField("Player");
        portFieldHost = new JTextField("10000");

        panel.add(new JLabel("Name:"));
        panel.add(nameFieldHost);

        panel.add(new JLabel("Port (10000-65535):"));
        panel.add(portFieldHost);

        JButton hostButton = new JButton("Host");
        hostButton.addActionListener(this::onHost);
        getRootPane().setDefaultButton(hostButton); // Allow Enter to trigger

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        panel.add(hostButton);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Creates the panel for connecting to a hosted game.
     */
    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        nameFieldClient = new JTextField("Player");
        addressFieldClient = new JTextField("localhost");
        portFieldClient = new JTextField("10000");

        panel.add(new JLabel("Name:"));
        panel.add(nameFieldClient);

        panel.add(new JLabel("Address:"));
        panel.add(addressFieldClient);

        panel.add(new JLabel("Port (10000-65535):"));
        panel.add(portFieldClient);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(this::onConnect);
        getRootPane().setDefaultButton(connectButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        panel.add(connectButton);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Host button handler.
     */
    private void onHost(ActionEvent e) {
        isHost = true;
        submitted = true;
        dispose();
    }

    /**
     * Connect button handler.
     */
    private void onConnect(ActionEvent e) {
        isHost = false;
        submitted = true;
        dispose();
    }

    /**
     * Gets the entered player name.
     */
    public String getPlayerName() {
        return isHost ? nameFieldHost.getText() : nameFieldClient.getText();
    }

    /**
     * Gets the host address entered by the user (Client only).
     */
    public String getHostAddress() {
        return addressFieldClient != null ? addressFieldClient.getText() : "localhost";
    }

    /**
     * Gets the selected port number.
     * Defaults to 5000 if invalid.
     */
    public int getPort() {
        try {
            return Integer.parseInt(isHost ? portFieldHost.getText() : portFieldClient.getText());
        } catch (NumberFormatException e) {
            return 5000;
        }
    }

    /**
     * Returns true if the user is hosting.
     */
    public boolean isHost() {
        return isHost;
    }

    /**
     * Returns true if the user clicked Host or Connect.
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Updates the connection status message in the dialog.
     *
     * @param message the status text to display
     */
    public void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}
