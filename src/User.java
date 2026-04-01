import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a single User account
 */
public class User {

    //Fields
    private String username;
    private String password;
    private final List<GameCollection> personalCollections;
    private final AccountDatabase accountDatabase;

    //Constructors

    /**
     * Constructs a User object with no passed parameters and defines defaults
     */
    public User() {
        this.username = "";
        this.password = "";
        this.personalCollections = new ArrayList<>();
        this.accountDatabase = new AccountDatabase();
    }

    //Methods

    /**
     * Creates a new account for the User with the provided credentials
     * and registers it in the AccountDatabase
     *
     * @param username The desired username
     * @param password The desired password
     */
    public void createAccount(String username, String password) {
        this.username = username;
        this.password = password;
        accountDatabase.setAccountInfo(username, password);
    }

    /**
     * Logs the User in by verifying the provided credentials against the AccountDatabase
     *
     * @param username The username to log in with
     * @param password The password to log in with
     * @return true if login was successful, false otherwise
     */
    // TODO: On login UI use this instead of using accountDatabase cause it links to accountDatabase
    public boolean login(String username, String password) {
        if (accountDatabase.validateLogin(username, password)) {
            this.username = username;
            this.password = password;
            System.out.println("Login successful for: " + username);
            return true;
        }

        System.out.println("Login failed: incorrect username or password.");
        return false;
    }

    /**
     * Logs the User out of their account
     */
    public void logout() {
        this.username = "";
        this.password = "";
        this.personalCollections.clear();
        System.out.println("User logged out.");
    }

    /**
     * Loads the User's personal game collections
     */
    //TODO: Update this method to actually load collections from a file
    public void loadCollections() {
        System.out.println("Loading collections for: " + (username.isEmpty() ? "unknown" : username));
    }

    /**
     * Saves the User's personal game collections
     */
    // TODO: Update this method to actually save collections to a file or database
    public void saveCollections() {
        System.out.println("Saving collections for: " + (username.isEmpty() ? "unknown" : username));
    }

    /**
     * Gets the username of the User
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the personal collections of the User
     *
     * @return The list of GameCollections
     */
    // TODO: Update this method to return actual collections loaded from a file
    public List<GameCollection> getPersonalCollections() {
        return personalCollections;
    }

    /**
     * Adds a GameCollection to the User's personal collections
     *
     * @param collection The GameCollection to add
     */
    // TODO: Update this method to also save the updated personal collections to a file
    public void addCollection(GameCollection collection) {
        personalCollections.add(collection);
    }

    /**
     * Removes a GameCollection from the User's personal collections
     *
     * @param collection The GameCollection to remove
     */
    // TODO: Update this method to also save the updated personal collections to a file
    public void removeCollection(GameCollection collection) {
        personalCollections.remove(collection);
    }
}
