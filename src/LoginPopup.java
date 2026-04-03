import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginPopup class for login UI, authentication logic, and event handling.
 *
 * This is a monolithic login popup window that handles:
 * - User interface for login and account creation
 * - Authentication logic and validation
 * - Event callbacks for login/logout
 *
 * TODO: AccountDatabase stuff
 * Currently, user data is stored in-memory only.
 *
 * @author Nathaniel Chan
 */
public class LoginPopup extends JFrame {

    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JLabel messageLabel;

    // Authentication listener callback
    private LoginListener listener;

    // In-memory user storage (TODO: Replace with AccountDatabase)
    private Map<String, String> userDatabase;

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
     * @param listener the LoginListener to notify on login events
     */
    public LoginPopup(LoginListener listener) {
        this.listener = listener;
        this.userDatabase = new HashMap<>();

        // Initialize with demo users
        userDatabase.put("user1", "password1");

        setTitle("My Game Library - Login");
        initializeUI();
        addEventHandlers();
    }

    /**
     * Initializes the user interface components.
     * Creates and layouts all GUI elements for the login window.
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with BorderLayout for simple, stable layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // ===== TOP SECTION: Title =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("Login to My Game Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        // ===== MIDDLE SECTION: Form =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        // Username row
        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        usernameRow.setBackground(new Color(240, 240, 240));
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameLabel.setPreferredSize(new Dimension(90, 25));
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameField.setPreferredSize(new Dimension(200, 25));
        usernameRow.add(usernameLabel);
        usernameRow.add(usernameField);

        // Password row
        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordRow.setBackground(new Color(240, 240, 240));
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordLabel.setPreferredSize(new Dimension(90, 25));
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setPreferredSize(new Dimension(200, 25));
        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);

        // Message label - fixed size
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        messageLabel.setForeground(new Color(200, 0, 0));
        messageLabel.setVerticalAlignment(JLabel.TOP);
        messageLabel.setHorizontalAlignment(JLabel.LEFT);
        messageLabel.setPreferredSize(new Dimension(290, 60));
        messageLabel.setMaximumSize(new Dimension(290, 60));
        messageLabel.setMinimumSize(new Dimension(290, 60));

        formPanel.add(usernameRow);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordRow);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(messageLabel);

        // ===== BOTTOM SECTION: Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(loginButton);

        createAccountButton = new JButton("Create Account");
        createAccountButton.setPreferredSize(new Dimension(130, 35));
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 12));
        createAccountButton.setBackground(new Color(100, 150, 100));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * <p>
     * TODO: Replace in-memory user lookup with AccountDatabase.authenticateUser()
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

        // TODO: Replace this with: AccountDatabase.authenticateUser(username, password)
        if (!userDatabase.containsKey(username)) {
            String message = "Username '" + username + "' does not exist.\nPlease create an account or try another username.";
            displayMessage(message, false);
            usernameField.selectAll();
            usernameField.requestFocus();
            return;
        }

        String storedPassword = userDatabase.get(username);
        if (!storedPassword.equals(password)) {
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
     * <p>
     * TODO: Replace in-memory storage with AccountDatabase.createAccount()
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

        // TODO: Replace this with: if (AccountDatabase.userExists(username))
        if (userDatabase.containsKey(username)) {
            String message = "Username '" + username + "' is already taken.\nPlease choose a different username.";
            displayMessage(message, false);
            usernameField.selectAll();
            usernameField.requestFocus();
            return;
        }

        // TODO: Replace this with: AccountDatabase.createAccount(username, password)
        userDatabase.put(username, password);

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