import java.util.ArrayList;
import java.util.List;

//TODO: Add parsing of accounts from the file and saving new accounts to the file for persistence

/**
 * AccountDatabase manages and holds all accounts,
 * and returns account data for validation.
 */
public class AccountDatabase {

    // Fields
    private final List<String[]> accounts; // each entry: [username, password, IsAdmin, PersonalCollectionID]

    // Constructors
    /**
     * Constructs an AccountDatabase and initializes the accounts list.
     */
    public AccountDatabase() {
        this.accounts = new ArrayList<>();
    }

    // Methods

    /**
     * Sets account info by storing credentials.
     * @param username the username for the new account
     * @param password the password for the new account
     */
    public void setAccountInfo(String username, String password) {
        accounts.add(new String[]{username, password});
        System.out.println("Account created for: " + username);
    }

    /**
     * Validates login credentials against stored accounts.
     * @param username the username to validate
     * @param password the password to validate
     * @return true if credentials match a stored account, false otherwise
     */
    public boolean validateLogin(String username, String password) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered.");
            return false;
        }

        for (String[] account : accounts) {
            if (account[0].equals(username) && account[1].equals(password)) {
                System.out.println("Login successful for: " + username);
                return true;
            }
        }

        System.out.println("Validation failed: no matching account found.");
        return false;
    }
}
