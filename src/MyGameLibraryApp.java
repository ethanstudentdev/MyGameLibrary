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
    private DashboardView dashboardView;
    private CollectionsView collectionsView;
    private String currentUser;

    /**
     * Constructor for MyGameLibraryApp.
     * Initializes the application and shows the login window.
     */
    public MyGameLibraryApp() {
        currentUser = null;
        showLoginWindow();
    }

    /**
     * Shows the login window as the entry point to the application.
     * This is the first window the user sees when running the application.
     */
    private void showLoginWindow() {
        loginPopup = new LoginPopup(this);
        loginPopup.setVisible(true);
    }

    /**
     * Shows the dashboard view after successful login.
     * This window is hidden until user successfully authenticates.
     *
     * @param username the username of the logged-in user
     */
    public void showDashboardView(String username) {
        if (collectionsView != null) {
            collectionsView.dispose();
            collectionsView = null;
        }
        dashboardView = new DashboardView(username, this);
        dashboardView.setVisible(true);
    }
    
    /**
     * Shows the collections view.
     *
     * @param username the username of the logged-in user
     */
    public void showCollectionsView(String username) {
        if (dashboardView != null) {
            dashboardView.dispose();
            dashboardView = null;
        }
        collectionsView = new CollectionsView(username, this);
        collectionsView.setVisible(true);
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
     * Closes the main window and shows the login window again.
     */
    public void onLogout() {
        currentUser = null;
        if (dashboardView != null) {
            dashboardView.dispose();
            dashboardView = null;
        }
        if (collectionsView != null) {
            collectionsView.dispose();
            collectionsView = null;
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
     * Main method to run the My Game Library application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyGameLibraryApp());
    }
}
