import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CollectionsView displays the user's game collections with a table showing game details.
 * Users can browse collections, rate games, and manage their collections.
 *
 * @author Nathaniel Chan
 */
public class CollectionsView extends JFrame {
    private final String username;
    private final MyGameLibraryApp app;
    private ArrayList<GameCollection> userCollections;
    private GameCollection currentCollection;
    private List<BoardGame> allGames;

    private RoundedCornerButton collections;
    private RoundedCornerButton dashboard;
    private RoundedCornerButton logout;

    private JPanel collectionsListPanel;
    private JTable gamesTable;
    private DefaultTableModel tableModel;
    private JLabel collectionNameLabel;
    private JTextField searchBar;
    private RoundedCornerButton createCollectionBtn;
    private RoundedCornerButton removeCollectionBtn;

    public CollectionsView(String username, MyGameLibraryApp app) {
        this.username = username;
        this.app = app;
        this.userCollections = new ArrayList<>();
        initializeCollections();
        initializeUI();
        initializeBehavior();
    }

    private void initializeCollections() {
        // Load all games from XML
        allGames = GameParser.parseAllGames("assets/bgg90Games.xml");

        GameCollection allGamesCollection = new GameCollection("All Games");
        for (BoardGame game : allGames) {
            allGamesCollection.addGame(game);
        }
        userCollections.add(allGamesCollection);

        GameCollection favorites = new GameCollection("Favorites");
        userCollections.add(favorites);

        currentCollection = allGamesCollection;
    }

    public void initializeUI() {
        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color buttonColor = new Color(108, 117, 125);
        Color buttonHoverColor = new Color(90, 98, 104);
        Color logoutButtonColor = new Color(220, 53, 69);
        Color logoutButtonHoverColor = new Color(200, 35, 51);

        setTitle("My Game Library - My Collections");
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel - Header with navigation
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(backgroundColor);
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left side - Title
        JLabel titleLabel = new JLabel("My Collections - " + username);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setPreferredSize(new Dimension(300, 50));
        top.add(titleLabel, BorderLayout.WEST);

        // Center - Search bar
        searchBar = new JTextField();
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        searchBar.setHorizontalAlignment(SwingConstants.CENTER);
        searchBar.setFont(new Font("Arial", Font.PLAIN, 18));
        searchBar.setPreferredSize(new Dimension(500, 50));
        searchBar.setMaximumSize(new Dimension(500, 50));
        setupSearchBarPlaceholder();

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(searchBar);
        top.add(centerPanel, BorderLayout.CENTER);

        // Right side - Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navPanel.setBackground(backgroundColor);
        navPanel.setPreferredSize(new Dimension(600, 50));

        collections = createStyledButton("My Collections", buttonColor, Color.WHITE, buttonHoverColor);
        dashboard = createStyledButton("Dashboard", buttonColor, Color.WHITE, buttonHoverColor);
        logout = createStyledButton("Logout", logoutButtonColor, Color.WHITE, logoutButtonHoverColor);

        navPanel.add(collections);
        navPanel.add(dashboard);
        navPanel.add(logout);

        top.add(navPanel, BorderLayout.EAST);

        // Main content panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);

        // Left side - Collections list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(backgroundColor);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        JLabel collectionsLabel = new JLabel("Collections");
        collectionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        collectionsLabel.setForeground(textColor);
        leftPanel.add(collectionsLabel, BorderLayout.NORTH);

        collectionsListPanel = new JPanel();
        collectionsListPanel.setLayout(new BoxLayout(collectionsListPanel, BoxLayout.Y_AXIS));
        collectionsListPanel.setBackground(backgroundColor);
        JScrollPane collectionsScroll = new JScrollPane(collectionsListPanel);
        collectionsScroll.setBorder(BorderFactory.createEmptyBorder());
        collectionsScroll.getViewport().setBackground(backgroundColor);
        leftPanel.add(collectionsScroll, BorderLayout.CENTER);

        // Bottom left - Create/Remove collection buttons
        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
        leftButtonPanel.setBackground(backgroundColor);
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        createCollectionBtn = createStyledButton("Create Collection", buttonColor, Color.WHITE, buttonHoverColor);
        removeCollectionBtn = createStyledButton("Remove Collection", buttonColor, Color.WHITE, buttonHoverColor);

        leftButtonPanel.add(createCollectionBtn);
        leftButtonPanel.add(Box.createVerticalStrut(10));
        leftButtonPanel.add(removeCollectionBtn);

        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);

        // Right side - Games table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(backgroundColor);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        collectionNameLabel = new JLabel(currentCollection.getTitle());
        collectionNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        collectionNameLabel.setForeground(textColor);
        rightPanel.add(collectionNameLabel, BorderLayout.NORTH);

        // Games table
        String[] columnNames = {"Cover", "Name", "Publisher", "Average Rating", "Rate it!", "Leave/View a Review"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Allow editing ratings and reviews
            }
        };

        gamesTable = new JTable(tableModel);
        gamesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        gamesTable.setRowHeight(80);
        gamesTable.setBackground(Color.WHITE);
        gamesTable.setGridColor(accentColor);

        JScrollPane tableScroll = new JScrollPane(gamesTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(accentColor, 2));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        populateCollectionsList();
        updateGamesTable();

        getContentPane().setBackground(backgroundColor);
    }

    private void populateCollectionsList() {
        collectionsListPanel.removeAll();
        Color accentColor = new Color(0, 122, 204);
        Color selectedColor = new Color(0, 102, 180);
        Color textColor = new Color(33, 37, 41);

        for (GameCollection collection : userCollections) {
            RoundedCornerButton collectionBtn = new RoundedCornerButton(collection.getTitle());
            boolean selected = collection == currentCollection;
            collectionBtn.setBackground(selected ? selectedColor : accentColor);
            collectionBtn.setForeground(Color.WHITE);
            collectionBtn.setFont(new Font("Arial", Font.PLAIN, 14));
            collectionBtn.setFocusPainted(false);
            collectionBtn.setBorderPainted(false);
            collectionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            collectionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            collectionBtn.addActionListener(e -> {
                currentCollection = collection;
                collectionNameLabel.setText(collection.getTitle());
                updateGamesTable();
                populateCollectionsList();
            });

            collectionsListPanel.add(collectionBtn);
            collectionsListPanel.add(Box.createVerticalStrut(5));
        }

        collectionsListPanel.revalidate();
        collectionsListPanel.repaint();
    }

    private void updateGamesTable() {
        tableModel.setRowCount(0);

        if (currentCollection == null || currentCollection.getTitle().equals("All Games")) {
            // Load games from XML for "All Games" collection
            loadGamesFromXML();
        }
    }

    private void loadGamesFromXML() {
        if (allGames == null || allGames.isEmpty()) {
            tableModel.addRow(new Object[]{"[Cover]", "No games available", "", "N/A", "", ""});
            return;
        }

        for (BoardGame game : allGames) {
            tableModel.addRow(new Object[]{"[Cover]", game.getTitle(), game.getPublisher(), game.getRatings(), "[Rate]", "[Review]"});
        }
    }

    private RoundedCornerButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        return createStyledButton(text, bgColor, fgColor, hoverColor, 180, 55);
    }

    private void setupSearchBarPlaceholder() {
        final String placeholderText = "Search for game...";
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

    private RoundedCornerButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor, int width, int height) {
        RoundedCornerButton button = new RoundedCornerButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                        Math.max(0, bgColor.getRed() - 20),
                        Math.max(0, bgColor.getGreen() - 20),
                        Math.max(0, bgColor.getBlue() - 20)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public void initializeBehavior() {
        collections.addActionListener(e -> {
            // Stay on collections view
        });

        dashboard.addActionListener(e -> {
            app.showDashboardView(username);
            dispose();
        });

        logout.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                app.onLogout();
            }
        });

        createCollectionBtn.addActionListener(e -> createCollection());
        removeCollectionBtn.addActionListener(e -> removeCollection());

        searchBar.addActionListener(e -> {
            String query = searchBar.getText();
            if (!query.equals("Search for game...") && !query.trim().isEmpty()) {
                searchCollections(query);
            }
        });
    }

    private void createCollection() {
        String name = JOptionPane.showInputDialog(this, "Enter a name for the new collection:", "Create Collection", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {
            return; // Cancelled
        }

        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Collection name cannot be empty.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (GameCollection collection : userCollections) {
            if (collection.getTitle().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "A collection with that name already exists.", "Duplicate Collection", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        GameCollection newCollection = new GameCollection(name);
        userCollections.add(newCollection);
        currentCollection = newCollection;
        collectionNameLabel.setText(newCollection.getTitle());
        populateCollectionsList();
        updateGamesTable();
    }

    private void removeCollection() {
        if (currentCollection == null) {
            return;
        }

        String title = currentCollection.getTitle();
        if (title.equals("All Games") || title.equals("Favorites")) {
            JOptionPane.showMessageDialog(this, "This default collection cannot be removed.", "Cannot Remove", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Remove collection '" + title + "'?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            userCollections.remove(currentCollection);
            currentCollection = userCollections.get(0);
            collectionNameLabel.setText(currentCollection.getTitle());
            populateCollectionsList();
            updateGamesTable();
        }
    }

    private void searchCollections(String query) {
        // TODO: implement search logic for collections
        System.out.println("Searching for: " + query);
    }
}
