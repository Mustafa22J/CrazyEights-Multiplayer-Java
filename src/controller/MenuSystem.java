package controller;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * MenuSystem creates the application menu bar with game and language options.
 * Supports English and French localization using ResourceBundle.
 */
public class MenuSystem {

    /** The current language locale in use (default is English). */
    private static Locale currentLocale = Locale.ENGLISH;

    /** Resource bundle used to fetch translated UI strings based on locale. */
    private static ResourceBundle messages = ResourceBundle.getBundle("Messages", currentLocale);

    /**
     * Creates and returns a menu bar with Game and Language options.
     * The menu includes options for restarting, quitting, and changing language.
     *
     * @param frame the parent JFrame to which the menu bar is attached
     * @return the fully constructed JMenuBar
     */
    public static JMenuBar createMenu(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu(messages.getString("menu.game"));
        JMenuItem restart = new JMenuItem(messages.getString("menu.restart"));
        JMenuItem quit = new JMenuItem(messages.getString("menu.quit"));

        // Restart handler
        restart.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(frame,
                messages.getString("msg.confirm_restart"),
                messages.getString("menu.restart"),
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                frame.dispose(); // Close current window
                // Could be extended to relaunch game or notify others
            }
        });

        quit.addActionListener(e -> System.exit(0));

        gameMenu.add(restart);
        gameMenu.add(quit);

        JMenu langMenu = new JMenu(messages.getString("menu.language"));
        JMenuItem english = new JMenuItem(messages.getString("menu.english"));
        JMenuItem french = new JMenuItem(messages.getString("menu.french"));

        english.addActionListener(e -> switchLanguage(frame, Locale.ENGLISH));
        french.addActionListener(e -> switchLanguage(frame, Locale.FRENCH));

        langMenu.add(english);
        langMenu.add(french);

        menuBar.add(gameMenu);
        menuBar.add(langMenu);

        return menuBar;
    }

    /**
     * Switches the application's language and prompts user to restart the app.
     *
     * @param frame the frame to display the dialog in
     * @param locale the new Locale to apply (Locale.ENGLISH or Locale.FRENCH)
     */
    private static void switchLanguage(JFrame frame, Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("Messages", currentLocale);

        JOptionPane.showMessageDialog(frame,
                messages.getString("msg.restart_required"),
                messages.getString("menu.language"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Returns the current ResourceBundle used for translation.
     * Other UI classes use this to retrieve localized text.
     *
     * @return the currently active ResourceBundle
     */
    public static ResourceBundle getMessages() {
        return messages;
    }

    /**
     * Returns the currently selected language locale.
     *
     * @return the active Locale (ENGLISH or FRENCH)
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
