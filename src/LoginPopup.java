import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginPopup class for login UI, authentication logic, and event handling.
 *
 * This is a monolithic login popup window that handles:
 * - User interface for login and account creation
 * - Authentication logic and validation
 * - Event callbacks for login/logout
 *
 * @author Nathaniel Chan
 */
public class LoginPopup extends JFrame {

    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedCornerButton loginButton;
    private RoundedCornerButton createAccountButton;
    private JLabel messageLabel;

    // Authentication listener callback
    private LoginListener listener;

    // User for login and account creation
    private User user;

    /**
     * Interface for login event callbacks.
     * Implement this to handle login success and cancellation events.
     */
    public interface LoginListener {
        /**
         * Called when a user successfully logs in.
         *
         * @param username the username of the authenticated user
         */
        void onLoginSuccess(String username);

        /**
         * Called when the user cancels the login dialog.
         */
        void onLoginCancelled();
    }

    /**
     * Constructor for LoginPopup.
     * Creates and initializes the login window.
     *
     * @param listener        the LoginListener to notify on login events
     * @param accountDatabase the shared account database loaded from config path
     */
    public LoginPopup(LoginListener listener, AccountDatabase accountDatabase) {
        this.listener = listener;
        this.user = new User(accountDatabase);

        setTitle("My Game Library - Login");
        initializeUI();
        addEventHandlers();
    }

    /**
     * Initializes the user interface components.
     * Creates and layouts all GUI elements for the login window.
     */
    private void initializeUI() {
        // Modern color scheme
        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color inputBgColor = new Color(255, 255, 255);
        Color buttonColor = accentColor;
        Color buttonHoverColor = new Color(0, 140, 230);
        Color createButtonColor = new Color(40, 167, 69);
        Color createButtonHoverColor = new Color(34, 139, 57);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 370);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with BorderLayout for simple, stable layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TOP SECTION: Title =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("Login to My Game Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(accentColor);
        titlePanel.add(titleLabel);

        // ===== MIDDLE SECTION: Form =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Username row
        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        usernameRow.setBackground(backgroundColor);
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setForeground(textColor);
        usernameLabel.setPreferredSize(new Dimension(90, 25));
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(250, 30));
        usernameField.setBackground(inputBgColor);
        usernameField.setForeground(textColor);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameRow.add(usernameLabel);
        usernameRow.add(usernameField);

        // Password row
        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordRow.setBackground(backgroundColor);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(textColor);
        passwordLabel.setPreferredSize(new Dimension(90, 25));
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 30));
        passwordField.setBackground(inputBgColor);
        passwordField.setForeground(textColor);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);

        // Message label - fixed size
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        messageLabel.setForeground(new Color(220, 53, 69));
        messageLabel.setVerticalAlignment(JLabel.TOP);
        messageLabel.setHorizontalAlignment(JLabel.LEFT);
        messageLabel.setPreferredSize(new Dimension(340, 50));
        messageLabel.setMaximumSize(new Dimension(340, 50));
        messageLabel.setMinimumSize(new Dimension(340, 50));

        formPanel.add(usernameRow);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordRow);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(messageLabel);

        // ===== BOTTOM SECTION: Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        loginButton = new RoundedCornerButton("Login");
        loginButton.setPreferredSize(new Dimension(140, 40));
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for login button
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(buttonHoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(buttonColor);
            }
        });

        createAccountButton = new RoundedCornerButton("Create Account");
        createAccountButton.setPreferredSize(new Dimension(140, 40));
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 12));
        createAccountButton.setBackground(createButtonColor);
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setBorderPainted(false);
        createAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for create account button
        createAccountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createAccountButton.setBackground(createButtonHoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createAccountButton.setBackground(createButtonColor);
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);

        // ===== ADD ALL PANELS TO MAIN PANEL =====
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Adds event handlers to all interactive components.
     */
    private void addEventHandlers() {
        loginButton.addActionListener(e -> handleLogin());
        createAccountButton.addActionListener(e -> handleCreateAccount());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    /**
     * Handles the login button action.
     * Validates input and attempts to authenticate the user.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validation
        if (username == null || username.trim().isEmpty()) {
            displayMessage("Please enter both username and password.", false);
            return;
        }

        if (password == null || password.isEmpty()) {
            displayMessage("Please enter both username and password.", false);
            return;
        }

        username = username.trim();

        // Check if username exists
        if (!user.userExists(username)) {
            String message = "Username '" + username + "' does not exist.\nPlease create an account or try another username.";
            displayMessage(message, false);
            usernameField.selectAll();
            usernameField.requestFocus();
            return;
        }

        // Validate credentials
        if (!user.login(username, password)) {
            String message = "Incorrect password for username '" + username + "'.\nPlease try again.";
            displayMessage(message, false);
            passwordField.setText("");
            usernameField.requestFocus();
            return;
        }

        // Login successful
        String successMessage = "Login successful! Welcome, " + username + "!";
        displayMessage(successMessage, true);

        JOptionPane.showMessageDialog(this,
                "Welcome, " + username + "!\n",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        listener.onLoginSuccess(username);
    }

    /**
     * Handles the create account button action.
     * Validates input and creates a new account.
     */
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Validation
        if (username == null || username.trim().isEmpty()) {
            displayMessage("\nUsername cannot be empty or contain only spaces.", false);
            usernameField.requestFocus();
            return;
        }

        if (password == null || password.isEmpty()) {
            displayMessage("\nPassword cannot be empty.", false);
            passwordField.requestFocus();
            return;
        }

        username = username.trim();

        if (username.length() < 3) {
            String message = "Username must be at least 3 characters long.\nCurrent length: " + username.length() + ".";
            displayMessage(message, false);
            usernameField.selectAll();
            usernameField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            String message = "Password must be at least 6 characters long.\nCurrent length: " + password.length() + ".";
            displayMessage(message, false);
            passwordField.setText("");
            passwordField.requestFocus();
            return;
        }

        // Check if username is already taken
        if (user.userExists(username)) {
            String message = "Username '" + username + "' is already taken.\nPlease choose a different username.";
            displayMessage(message, false);
            usernameField.selectAll();
            usernameField.requestFocus();
            return;
        }

        // Create the account
        user.createAccount(username, password);

        String successMessage = "Account created successfully!\n\nUsername: " + username +
                "\n\nYou can now login with your new credentials.";
        displayMessage(successMessage, true);

        JOptionPane.showMessageDialog(this,
                successMessage,
                "Account Created Successfully",
                JOptionPane.INFORMATION_MESSAGE);


        usernameField.requestFocus();
    }

    /**
     * Displays a message to the user.
     *
     * @param message   the message to display
     * @param isSuccess true for success (green), false for error (red)
     */
    private void displayMessage(String message, boolean isSuccess) {
        // Convert newlines to HTML line breaks and wrap in HTML
        String htmlMessage = "<html><body style='width: 280px;'>" +
                message.replace("\n", "<br>") +
                "</body></html>";
        messageLabel.setText(htmlMessage);

        if (isSuccess) {
            messageLabel.setForeground(new Color(0, 120, 0));
        } else {
            messageLabel.setForeground(new Color(180, 0, 0));
        }
    }
}