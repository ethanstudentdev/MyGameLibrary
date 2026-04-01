import javax.swing.*;

/**
 * MainWindow class represents the main dashboard of My Game Library application.
 *
 * This window appears only after the user successfully logs in through the LoginWindow.
 * It provides access to the various features of the application including:
 * - Browsing games
 * - Searching for games
 * - Managing collections
 * - Leaving reviews and ratings
 * - Logging out
 *
 * @author Nathaniel Chan
 * @version 1.0
 */
public class MainWindow extends JFrame {

    private String username;
    private MyGameLibraryApp app;
    private JLabel userStatusLabel;

    /**
     * Constructor for MainWindow.
     * Sets up the main dashboard window.
     *
     * @param username the username of the logged-in user
     * @param app the application instance for logout handling
     */
    public MainWindow(String username, MyGameLibraryApp app) {
        this.username = username;
        this.app = app;

        setTitle("My Game Library - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        initializeUI();
    }

    /**
     * Initializes the user interface components.
     * Creates and layouts all GUI elements for the main window.
     */
    private void initializeUI() {
        // Main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new java.awt.Color(240, 240, 240));

        // Header with user status
        JPanel headerPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        headerPanel.setBackground(new java.awt.Color(240, 240, 240));

        JLabel titleLabel = new JLabel("My Game Library");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // User status
        userStatusLabel = new JLabel("Welcome, " + username + "!");
        userStatusLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        mainPanel.add(userStatusLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Logout button at the bottom
        JPanel logoutPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        logoutPanel.setBackground(new java.awt.Color(240, 240, 240));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        logoutButton.setPreferredSize(new java.awt.Dimension(100, 35));
        logoutButton.setBackground(new java.awt.Color(200, 50, 50));
        logoutButton.setForeground(java.awt.Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutButton);

        mainPanel.add(logoutPanel);

        setContentPane(mainPanel);
    }

    /**
     * Handles the Logout button action.
     * Logs out the user and returns to the login window.
     */
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            app.onLogout();
        }
    }

    /**
     * Gets the username of the logged-in user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
