import javax.swing.*;

/**
 * MyGameLibraryApp class is the main entry point for My Game Library application.
 *
 * This application demonstrates:
 * - Login window appearing first (as the only window on startup)
 * - Main dashboard window appearing only after successful login
 * - Proper window lifecycle management
 *
 * @author Nathaniel Chan
 */
public class MyGameLibraryApp implements LoginPopup.LoginListener {

    private LoginPopup loginPopup;
    private JFrame currentFrame;
    private String currentUser;
    private GameParser parser;
    private final Admin admin;
    private final AccountDatabase accountDatabase;

    /**
     * Constructor for MyGameLibraryApp.
     * Loads config.xml first to get file paths, then initializes the application.
     */
    public MyGameLibraryApp() {
        // Load config.xml as this gives us the correct paths for accounts and games
        admin = new Admin();
        //Creates the parser to parse games
        parser = new GameParser();
        // Create the shared AccountDatabase using the path from config
        accountDatabase = new AccountDatabase(admin.getAccountsFile());
        currentUser = null;
        showLoginWindow();
    }

    /**
     * Shows the login window as the entry point to the application.
     * This is the first window the user sees when running the application.
     */
    private void showLoginWindow() {
        loginPopup = new LoginPopup(this, accountDatabase);
        loginPopup.setVisible(true);
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame("My Game Library");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        return frame;
    }

    private void switchTo(JPanel view) {
        JFrame newFrame = createFrame();
        newFrame.add(view);
        newFrame.setVisible(true);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = newFrame;
    }

    /**
     * Shows the dashboard view after successful login.
     *
     * @param username the username of the logged-in user
     */
    public void showDashboardView(String username) {
        switchTo(new DashboardView(username, this,admin));
    }

    /**
     * Shows the collections view.
     *
     * @param username the username of the logged-in user
     */
    public void showCollectionsView(String username) {
        switchTo(new CollectionsView(username, this, admin,parser));
    }

    /**
     * Shows the game screen view.
     *
     * @param game the game to display
     * @param username the username of the logged-in user
     */
    public void showGameScreenView(BoardGame game, String username) {
        switchTo(new GameScreenView(game, username, this));
    }

    /**
     * Called when a user successfully logs in.
     * Shows the dashboard view and hides the login window.
     *
     * @param username the username of the authenticated user
     */
    @Override
    public void onLoginSuccess(String username) {
        currentUser = username;
        showDashboardView(username);
    }

    /**
     * Called when the user cancels the login dialog.
     * The application exits (handled by LoginPopup).
     */
    @Override
    public void onLoginCancelled() {
        System.out.println("Login was cancelled. Application will exit.");
    }

    /**
     * Called when the user logs out from the main window.
     * Closes the current window and shows the login window again.
     */
    public void onLogout() {
        currentUser = null;
        if (currentFrame != null) {
            currentFrame.dispose();
            currentFrame = null;
        }
        showLoginWindow();
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return the current username, or null if not logged in
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the Admin instance (holds config paths, can be updated by admin users).
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * Gets the shared AccountDatabase loaded from the config path.
     */
    public AccountDatabase getAccountDatabase() {
        return accountDatabase;
    }

    /**
     * Main method to run the My Game Library application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyGameLibraryApp());
    }
}
