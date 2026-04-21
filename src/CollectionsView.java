import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * CollectionsView displays the user's game collections with a table showing game details.
 * Users can browse collections, rate games, and manage their collections.
 *
 * @author Nathaniel Chan
 */
public class CollectionsView extends JPanel {
    private final String username;
    private final MyGameLibraryApp app;
    private final AccountDatabase accountDatabase;
    private final Admin admin;
    private ArrayList<GameCollection> userCollections;
    private GameCollection currentCollection;
    private GameCollection allGamesCollection;
    private List<BoardGame> displayedGames;
    private final GameParser parser;

    private RoundedCornerButton collections;
    private RoundedCornerButton dashboard;
    private RoundedCornerButton logout;

    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    private JPanel collectionsListPanel;
    private JTable gamesTable;
    private DefaultTableModel tableModel;
    private JLabel collectionNameLabel;
    private JTextField searchBar;
    private RoundedCornerButton createCollectionBtn;
    private RoundedCornerButton removeCollectionBtn;

    public CollectionsView(String username, MyGameLibraryApp app, Admin admin, GameParser parser) {
        this.username = username;
        this.app = app;
        this.accountDatabase = app.getAccountDatabase();
        this.parser = parser;
        this.userCollections = new ArrayList<>();
        this.admin = admin;
        initializeCollections();
        initializeUI();
        initializeBehavior();
    }

    /** Load the global game list and then load saved user collections from disk. */
    private void initializeCollections() {
        File allGamesFile = new File("assets",admin.getGamesFile());
        allGamesCollection = parser.parse(allGamesFile);
        userCollections.add(allGamesCollection);

        List<String> collectionNames = accountDatabase.getCollectionNames(username);
        if (!collectionNames.contains("Favorites")) {
            accountDatabase.createCollection(username, "Favorites");
            collectionNames = accountDatabase.getCollectionNames(username);
        }

        for (String collectionName : collectionNames) {
            if (collectionName.equals("All Games")) {
                continue;
            }

            GameCollection savedCollection = new GameCollection(collectionName);
            for (String gameId : accountDatabase.getCollectionGameIds(username, collectionName)) {
                BoardGame matchingGame = findGameByTitle(gameId);
                if (matchingGame != null) {
                    savedCollection.addGame(matchingGame);
                }
            }
            userCollections.add(savedCollection);
        }

        currentCollection = allGamesCollection;
    }

    private BoardGame findGameByTitle(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        for (BoardGame game : allGamesCollection.getGames()) {
            if (title.equalsIgnoreCase(game.getTitle())) {
                return game;
            }
        }
        return null;
    }


    public void initializeUI() {
        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color buttonColor = new Color(108, 117, 125);
        Color buttonHoverColor = new Color(90, 98, 104);
        Color logoutButtonColor = new Color(220, 53, 69);
        Color logoutButtonHoverColor = new Color(200, 35, 51);

        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        /** Top panel header with navigation. */
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(backgroundColor);
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /** Left side title. */
        JLabel titleLabel = new JLabel("My Collections - " + username);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setPreferredSize(new Dimension(300, 50));
        top.add(titleLabel, BorderLayout.WEST);

        /** Center search bar. */
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

        /** Right side navigation buttons. */
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

        /** Main content panel. */
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);

        /** Left side collections list. */
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

        /** Bottom-left create/remove collection buttons. */
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

        /** Right side games table. */
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(backgroundColor);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        collectionNameLabel = new JLabel(currentCollection.getTitle());
        collectionNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        collectionNameLabel.setForeground(textColor);
        rightPanel.add(collectionNameLabel, BorderLayout.NORTH);

        /** Games table configuration. */
        String[] columnNames = {"Cover", "Name", "Publisher", "Average Rating", "Rate it!", "Leave/View a Review"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                /** Allow editing reviews in the review column. */
                return column == 5;
            }
        };

        gamesTable = new JTable(tableModel);
        gamesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        gamesTable.setRowHeight(80);
        gamesTable.setBackground(Color.WHITE);
        gamesTable.setGridColor(accentColor);
        gamesTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        gamesTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setBackground(Color.WHITE);
                }
                String url = value instanceof String ? (String) value : null;
                if (url != null && !url.isBlank()) {
                    if (imageCache.containsKey(url)) {
                        label.setIcon(imageCache.get(url));
                    } else {
                        new SwingWorker<ImageIcon, Void>() {
                            @Override
                            protected ImageIcon doInBackground() throws Exception {
                                BufferedImage img = ImageIO.read(new URL(url));
                                if (img == null) return null;
                                Image scaled = img.getScaledInstance(100, 70, Image.SCALE_SMOOTH);
                                return new ImageIcon(scaled);
                            }
                            @Override
                            protected void done() {
                                try {
                                    ImageIcon icon = get();
                                    if (icon != null) {
                                        imageCache.put(url, icon);
                                        table.repaint();
                                    }
                                } catch (Exception ignored) {}
                            }
                        }.execute();
                    }
                }
                return label;
            }
        });

        gamesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && gamesTable.getSelectedRow() != -1) {
                    int row = gamesTable.convertRowIndexToModel(gamesTable.getSelectedRow());
                    if (displayedGames != null && row >= 0 && row < displayedGames.size()) {
                        openGameScreen(displayedGames.get(row));
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(gamesTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(accentColor, 2));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);

        populateCollectionsList();
        updateGamesTable();
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

    /** Bind the currently selected collection to the table view. */
    private void updateGamesTable() {
        tableModel.setRowCount(0);

        if (currentCollection == null) {
            displayedGames = new ArrayList<>();
        } else if (currentCollection.getTitle().equals("All Games")) {
            displayedGames = allGamesCollection.getGames();
        } else {
            displayedGames = currentCollection.getGames();
        }

        if (displayedGames == null || displayedGames.isEmpty()) {
            tableModel.addRow(new Object[]{"", "No games available", "", "N/A", "", ""});
            return;
        }

        for (BoardGame game : displayedGames) {
            float average = accountDatabase.getAverageRatingForGame(game.getTitle());
            if (average <= 0) {
                average = game.getAvgRating();
            }
            String avgRating = String.format("%.1f", average);

            int userRating = accountDatabase.getUserRating(username, game.getTitle());
            String rateDisplay = userRating > 0 ? userRating + "/5" : "—";

            String userReview = accountDatabase.getUserReview(username, game.getTitle());
            String reviewDisplay = (userReview != null && !userReview.trim().isEmpty()) ? userReview : "—";

            tableModel.addRow(new Object[]{game.getImage(), game.getTitle(), game.getPublisher(), avgRating, rateDisplay, reviewDisplay});
        }
    }

    private void openGameScreen(BoardGame game) {
        app.showGameScreenView(game, username);
    }

    private RoundedCornerButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        return createStyledButton(text, bgColor, fgColor, hoverColor, 180, 55);
    }

    private void setupSearchBarPlaceholder() {
        final String placeholderText = "Search for game...";
        final Color placeholderColor = new Color(150, 150, 150);
        final Color textColor = Color.BLACK;

        /** Set initial placeholder state. */
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
            /** Stay on collections view. */
        });

        dashboard.addActionListener(e -> {
            app.showDashboardView(username);
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

    /** Prompt the user to create a new custom collection and persist it to disk. */
    private void createCollection() {
        String name = JOptionPane.showInputDialog(this, "Enter a name for the new collection:", "Create Collection", JOptionPane.PLAIN_MESSAGE);
        if (name == null) {
            /** Cancelled by user. */
            return;
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
        accountDatabase.createCollection(username, name);
        currentCollection = newCollection;
        collectionNameLabel.setText(newCollection.getTitle());
        populateCollectionsList();
        updateGamesTable();
    }

    /** Remove the currently selected custom collection from the UI and saved account data. */
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
            accountDatabase.removeCollection(username, title);
            userCollections.remove(currentCollection);
            currentCollection = userCollections.get(0);
            collectionNameLabel.setText(currentCollection.getTitle());
            populateCollectionsList();
            updateGamesTable();
        }
    }

    /** Search function placeholder for collection text filtering. */
    private void searchCollections(String query) {
        /** TODO: implement search logic for collections. */
        System.out.println("Searching for: " + query);
    }
}
