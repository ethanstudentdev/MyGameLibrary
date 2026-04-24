import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

/**
 * DashboardView is the view the user reaches after login.
 * It allows the user to search games by text or genre and provides
 * quick access to My Collections, Dashboard, and Logout.
 *
 * @author Levi Snellgrove
 * @author Nathaniel Chan
 * @author Ethan Johnson
 */
public class DashboardView extends JPanel {
    private final String username;
    private final MyGameLibraryApp app;
    private GameCollection masterCollection;
    private GameParser parser;
    private File allGamesFile;
    private ArrayList<String> categoryNames;
    private Search searchEngine;
    private Admin admin;
    private boolean isAdmin;
    private AccountDatabase accountDatabase;
    private BrowseView browser;

    private boolean searchByTitle;
    private boolean searchByGenre;

    private RoundedCornerButton collections;
    private RoundedCornerButton dashboard;
    private RoundedCornerButton logout;
    private RoundedCornerButton adminButton;

    private List<RoundedCornerButton> categoryButtons;
    private JTextField searchBar;
    private RoundedCornerButton leftArrow;
    private RoundedCornerButton rightArrow;

    /**
     * The dashboardView constructor
     *
     * @param username The username of the current user
     * @param app The app being user
     * @param admin admin to load files from
     */
    public DashboardView(String username, MyGameLibraryApp app, Admin admin, boolean isAdmin, AccountDatabase accountDatabase) {
        this.username = username;
        this.app = app;
        this.admin = admin;
        this.isAdmin = isAdmin;
        this.accountDatabase = accountDatabase;
        this.allGamesFile = new File("assets","bgg90Games.xml");
        this.parser = new GameParser();
        this.masterCollection = parser.parse(allGamesFile);
        this.categoryNames = masterCollection.getGenres();
        this.searchByTitle = true;
        this.searchByGenre = false;
        initializeUI();
        initializeBehavior();
    }

    public void initializeUI() {
        // Color Scheme
        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color buttonColor = new Color(108, 117, 125);
        Color buttonHoverColor = new Color(90, 98, 104);
        Color logoutButtonColor = new Color(220, 53, 69);
        Color logoutButtonHoverColor = new Color(200, 35, 51);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Top panel - Header with navigation
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(backgroundColor);
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Dashboard - " + username);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setPreferredSize(new Dimension(300, 50));
        top.add(titleLabel, BorderLayout.WEST);

        // Navigation buttons panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setBackground(backgroundColor);

        collections = createStyledButton("My Collections", buttonColor, Color.WHITE, buttonHoverColor);
        dashboard = createStyledButton("Dashboard", buttonColor, Color.WHITE, buttonHoverColor);
        logout = createStyledButton("Logout", logoutButtonColor, Color.WHITE, logoutButtonHoverColor);
        adminButton = createStyledButton("Admin",buttonColor,Color.WHITE,buttonHoverColor);

        navPanel.add(adminButton);
        navPanel.add(collections);
        navPanel.add(dashboard);
        navPanel.add(logout);

        top.add(navPanel, BorderLayout.EAST);

        // Middle panel - Search functionality
        JPanel middle = new JPanel(new GridBagLayout()); // Use GridBagLayout for perfect centering
        middle.setBackground(backgroundColor);
        middle.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(backgroundColor);

        // Create search bar with proper placeholder behavior
        searchBar = new JTextField();
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        searchBar.setHorizontalAlignment(SwingConstants.CENTER);
            /** Search text for readability. */
            searchBar.setFont(new Font("Arial", Font.PLAIN, 18));
            /** Search bar for easier input. */
            searchBar.setPreferredSize(new Dimension(500, 50));
        searchBar.setMaximumSize(new Dimension(500, 50));
        searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        /** Initialize placeholder text behavior. */
        setupSearchBarPlaceholder();

        JLabel genreSearchLabel = new JLabel("Browse by game type");
        genreSearchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        /** Genre label font for readability. */
        genreSearchLabel.setFont(new Font("Arial", Font.BOLD, 18));
        genreSearchLabel.setForeground(textColor);
        genreSearchLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        genreSearchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchPanel.add(searchBar);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(genreSearchLabel);

        // Category button grid
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.X_AXIS));
        gridPanel.setBackground(backgroundColor);

        Collections.sort(categoryNames);
        int maxLabelLength = 0;

        for (String genre : categoryNames)
        {
            maxLabelLength = Math.max(maxLabelLength, genre.length());
        }
        int buttonWidth = Math.max(180, maxLabelLength * 11 + 40);
        int columnWidth = buttonWidth + 20;

        categoryButtons = new ArrayList<>();
        for (int i = 0; i < categoryNames.size(); i += 3)
        {
            JPanel column = new JPanel(new GridLayout(3, 1, 0, 20));
            column.setBackground(backgroundColor);
            for (int row = 0; row < 3; row++) {
                int index = i + row;
                if (index < categoryNames.size())
                {
                    String categoryName = categoryNames.get(index);
                    RoundedCornerButton categoryButton = createStyledButton(categoryName, accentColor, Color.WHITE, new Color(0, 140, 230), buttonWidth, 55);
                    categoryButton.putClientProperty("categoryName", categoryName);
                    //Searches by the genre clicked if a category button is clicked
                    categoryButton.addActionListener(e -> {
                        searchByTitle = false;
                        searchByGenre = true;
                        searchGames(categoryName);
                    });
                    categoryButtons.add(categoryButton);
                    column.add(categoryButton);
                } else
                {
                    column.add(Box.createVerticalGlue());
                }
            }
            gridPanel.add(column);
            if (i + 3 < categoryNames.size())
            {
                gridPanel.add(Box.createHorizontalStrut(20));
            }
        }

        JScrollPane categoryScrollPane = new JScrollPane(gridPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        categoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        categoryScrollPane.setPreferredSize(new Dimension(3 * columnWidth - 20, 240));
        categoryScrollPane.getViewport().setBackground(backgroundColor);
        categoryScrollPane.setOpaque(false);
        categoryScrollPane.getViewport().setOpaque(false);

        leftArrow = createStyledButton("<", buttonColor, Color.WHITE, buttonHoverColor, 60, 55);
        rightArrow = createStyledButton(">", buttonColor, Color.WHITE, buttonHoverColor, 60, 55);

        leftArrow.addActionListener(e -> scrollCategoryPanel(categoryScrollPane, -columnWidth));
        rightArrow.addActionListener(e -> scrollCategoryPanel(categoryScrollPane, columnWidth));

        updateArrowStates(categoryScrollPane);

        JPanel categoryNavigationPanel = new JPanel(new BorderLayout(25, 0));
        categoryNavigationPanel.setBackground(backgroundColor);
        categoryNavigationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryNavigationPanel.add(leftArrow, BorderLayout.WEST);
        categoryNavigationPanel.add(categoryScrollPane, BorderLayout.CENTER);
        categoryNavigationPanel.add(rightArrow, BorderLayout.EAST);

        searchPanel.add(Box.createVerticalStrut(20));
        searchPanel.add(categoryNavigationPanel);

        // Add search panel to center of middle panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        middle.add(searchPanel, gbc);

        // Add panels to main frame
        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
    }

    private RoundedCornerButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        return createStyledButton(text, bgColor, fgColor, hoverColor, 180, 55);
    }

    private RoundedCornerButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor, int width, int height) {
        RoundedCornerButton button = new RoundedCornerButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void scrollCategoryPanel(JScrollPane scrollPane, int offset) {
        JViewport viewport = scrollPane.getViewport();
        Point position = viewport.getViewPosition();
        int newX = position.x + offset;
        newX = Math.max(0, Math.min(newX, scrollPane.getViewport().getView().getWidth() - viewport.getWidth()));
        viewport.setViewPosition(new Point(newX, position.y));
        updateArrowStates(scrollPane);
    }

    private void updateArrowStates(JScrollPane scrollPane) {
        // Arrows are always enabled for scrolling
    }

    private void setupSearchBarPlaceholder() {
        final String placeholderText = "Search games...";
        final Color placeholderColor = new Color(150, 150, 150);
        final Color textColor = Color.BLACK;

        // Set initial placeholder state
        searchBar.setText(placeholderText);
        searchBar.setForeground(placeholderColor);

        searchBar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchBar.getText().equals(placeholderText)) {
                    searchBar.setText("");
                    searchBar.setForeground(textColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchBar.getText().trim().isEmpty()) {
                    searchBar.setText(placeholderText);
                    searchBar.setForeground(placeholderColor);
                }
            }
        });
    }

    public void initializeBehavior() {
        collections.addActionListener(e -> openCollections());
        dashboard.addActionListener(e -> openDashboard());
        logout.addActionListener(e -> performLogout());
       adminButton.addActionListener(e -> performAdmin());

        if (categoryButtons != null) {
            for (RoundedCornerButton categoryButton : categoryButtons) {
                categoryButton.addActionListener(e -> {
                    String categoryName = (String) categoryButton.getClientProperty("categoryName");
                    if (categoryName != null) {
                        filterByGameType(categoryName);
                    } else {
                        filterByGameType(categoryButton.getText());
                    }
                });
            }
        }

        searchBar.addActionListener(e -> {
            String query = searchBar.getText();
            if (!query.equals("Search games...") && !query.trim().isEmpty()) {
                searchGames(query);
            }
        });
    }

    /**
     * Creates an admin panel that the user can make themselves an admin, change the gamefile path, accountdatabase path
     *
     */
    private void performAdmin() {
        boolean currentlyAdmin = accountDatabase.isAdmin(username);

        JDialog dialog = new JDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Choose an Option",
                true
        );

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridLayout(4, 1, 0, 10));

        JButton button1 = new JButton("Make Current User Admin");
        JButton button2 = new JButton("Change game path");
        JButton button3 = new JButton("Change account database path");
        JButton button4 = new JButton("Close");

        button1.addActionListener(d -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Make " + username + " an admin?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                isAdmin = true;
                accountDatabase.setAdmin(username, true);
                JOptionPane.showMessageDialog(dialog, username + " is now an admin.");
            }
        });

        button2.addActionListener(d -> {
            if (!currentlyAdmin) {
                JOptionPane.showMessageDialog(dialog, "You must be an admin to change the game path.");
                return;
            }
            String defaultPath = "bgg90Games.xml";
            JTextField pathField = new JTextField(admin.getGamesFile());
            Object[] message = {"Game file path:", pathField};
            int option = JOptionPane.showOptionDialog(dialog, message, "Change Game Path",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Save", "Reset to Default", "Cancel"}, "Save");
            if (option == 0) {
                String newPath = pathField.getText().trim();
                if (!newPath.isEmpty()) {
                    admin.setGamesFile(newPath);
                    allGamesFile = new File(newPath);
                    masterCollection = parser.parse(allGamesFile);
                    categoryNames = masterCollection.getGenres();
                    JOptionPane.showMessageDialog(dialog, "Game path updated to:\n" + newPath);
                }
            } else if (option == 1) {
                admin.setGamesFile(defaultPath);
                allGamesFile = new File(defaultPath);
                masterCollection = parser.parse(allGamesFile);
                categoryNames = masterCollection.getGenres();
                JOptionPane.showMessageDialog(dialog, "Game path reset to default.");
            }
        });

        button3.addActionListener(d -> {
            if (!currentlyAdmin) {
                JOptionPane.showMessageDialog(dialog, "You must be an admin to change the account database path.");
                return;
            }

            String defaultPath = "assets/accounts.xml";
            JTextField pathField = new JTextField(admin.getAccountsFile());
            Object[] message = {"Account database path:", pathField};
            int option = JOptionPane.showOptionDialog(dialog, message, "Change Account Database Path",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Save", "Reset to Default", "Cancel"}, "Save");
            if (option == 0) {
                String newPath = pathField.getText().trim();
                if (!newPath.isEmpty()) {
                    admin.setAccountsFile(newPath);
                    JOptionPane.showMessageDialog(dialog, "Account database path updated to:\n" + newPath);
                }
            } else if (option == 1) {
                admin.setAccountsFile(defaultPath);
                JOptionPane.showMessageDialog(dialog, "Account database path reset to default.");
            }
        });

        button4.addActionListener(d -> dialog.dispose());

        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void openCollections() {
        app.showCollectionsView(username);
    }

    private void openDashboard() {
        // TODO: refresh or show the dashboard overview
    }

    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            app.onLogout();
        }
    }

    private void searchGames(String query) {
        if (query == null || query.trim().isEmpty() || query.equals("Search games...")) {
            return; // Don't search if it's placeholder text or empty
        }
        //search logic for the dashboard search bar
        //Creates a new Search class object for searching
        searchEngine = new Search(query,masterCollection);

        //Creates a collection to store games in and "searches"
        GameCollection searchCol = new GameCollection();

        //This logic means search by title is always the default search way since the else if resets it
        if(searchByTitle)
        {
            System.out.println("Searching by title for: " + query);
            searchCol = searchEngine.searchByTitle();
        }
        else if (searchByGenre)
        {
            System.out.println("Searching by genre for: " + query);
            searchCol = searchEngine.searchByGenre();
            searchByTitle = true;
            searchByGenre = false;
        }

        //debug section
        searchCol.printAllGames();
        searchCol.printAllGames();

        searchCol.getGenres();
        searchCol.printGenres();

        //Creates a new Browse view window with search criteria
        app.showBrowseView(username, searchCol);
    }

    private void filterByGameType(String type) {
        // TODO: implement genre filtering for game type buttons
    }
}
