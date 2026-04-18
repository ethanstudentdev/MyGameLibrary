import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.imageio.ImageIO;

/**
 * A full-screen game detail view for a single board game.
 *
 * Displays game metadata, rating, description, reviews, and collection actions.
 *
 * @author Nathaniel Chan
 */
public class GameScreenView extends JPanel {
    private final BoardGame game;
    private final String username;
    private final MyGameLibraryApp app;
    private final AccountDatabase accountDatabase = new AccountDatabase();

    private JLabel avgRatingLabel;
    private JTextArea reviewsArea;
    private JLabel starRatingLabel;
    private JLabel userRatingLabel;
    private JLabel typeLabel;
    private JTextArea descriptionArea;
    private JButton[] starButtons;
    private JTextField searchBar;

    public GameScreenView(BoardGame game, String username, MyGameLibraryApp app) {
        this.game = game;
        this.username = username;
        this.app = app;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color buttonColor = new Color(108, 117, 125);
        Color buttonHoverColor = new Color(90, 98, 104);

        /** Top navigation bar. */
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(backgroundColor);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel2 = new JLabel("Game Details - " + username);
        titleLabel2.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel2.setForeground(textColor);
        titleLabel2.setPreferredSize(new Dimension(300, 50));
        topBar.add(titleLabel2, BorderLayout.WEST);

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
        topBar.add(centerPanel, BorderLayout.CENTER);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        navButtons.setBackground(backgroundColor);
        navButtons.setPreferredSize(new Dimension(600, 50));

        RoundedCornerButton collectionsBtn = createStyledButton("My Collections", buttonColor, Color.WHITE, buttonHoverColor);
        RoundedCornerButton dashboardBtn = createStyledButton("Dashboard", buttonColor, Color.WHITE, buttonHoverColor);
        RoundedCornerButton logoutBtn = createStyledButton("Logout", new Color(220, 53, 69), Color.WHITE, new Color(200, 35, 51));

        collectionsBtn.addActionListener(e -> app.showCollectionsView(username));
        dashboardBtn.addActionListener(e -> app.showDashboardView(username));
        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                app.onLogout();
            }
        });

        navButtons.add(collectionsBtn);
        navButtons.add(dashboardBtn);
        navButtons.add(logoutBtn);
        topBar.add(navButtons, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        /** Main content. */
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);

        /** Left side card. */
        JPanel leftCard = new JPanel();
        leftCard.setBackground(Color.WHITE);
        leftCard.setLayout(new BorderLayout(0, 18));
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel coverLabel = new JLabel("", SwingConstants.CENTER);
        coverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        coverLabel.setOpaque(true);
        coverLabel.setBackground(new Color(235, 240, 250));
        coverLabel.setForeground(accentColor);
        coverLabel.setPreferredSize(new Dimension(560, 360));
        leftCard.add(coverLabel, BorderLayout.NORTH);

        String imageUrl = game.getImage();
        if (imageUrl != null && !imageUrl.isBlank()) {
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    BufferedImage img = ImageIO.read(new URL(imageUrl));
                    if (img == null) return null;
                    Image scaled = img.getScaledInstance(560, 360, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (icon != null) {
                            coverLabel.setIcon(icon);
                            coverLabel.setText("");
                        }
                    } catch (Exception ignored) {}
                }
            }.execute();
        }

        JPanel leftButtons = new JPanel();
        leftButtons.setBackground(Color.WHITE);
        leftButtons.setLayout(new BoxLayout(leftButtons, BoxLayout.Y_AXIS));

        /** Star rating panel. */
        JPanel starPanel = new JPanel();
        starPanel.setBackground(Color.WHITE);
        starPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        starButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            starButtons[i] = new JButton("☆");
            starButtons[i].setFont(new Font("Arial", Font.PLAIN, 30));
            starButtons[i].setBackground(Color.WHITE);
            starButtons[i].setBorderPainted(false);
            starButtons[i].setFocusPainted(false);
            starButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int rating = i + 1;
            starButtons[i].addActionListener(e -> setUserRating(rating));
            starPanel.add(starButtons[i]);
        }
        leftButtons.add(starPanel);

        leftButtons.add(Box.createVerticalStrut(14));
        leftButtons.add(createActionButton("Write a review", buttonColor, Color.WHITE, buttonHoverColor, e -> promptReview()));
        leftButtons.add(Box.createVerticalStrut(14));
        leftButtons.add(createActionButton("Add to Collection", buttonColor, Color.WHITE, buttonHoverColor, e -> showAddToCollectionDialog()));

        leftCard.add(leftButtons, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        contentPanel.add(leftCard, gbc);

        /** Right side detail panel. */
        JPanel rightCard = new JPanel();
        rightCard.setBackground(Color.WHITE);
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JLabel titleLabel = new JLabel(game.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(textColor);
        rightCard.add(titleLabel);

        JLabel publisherLabel = new JLabel(game.getPublisher());
        publisherLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        publisherLabel.setForeground(new Color(90, 98, 104));
        rightCard.add(publisherLabel);

        rightCard.add(Box.createVerticalStrut(16));

        starRatingLabel = new JLabel(buildStarText(game.getAvgRating()));
        starRatingLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        starRatingLabel.setForeground(new Color(220, 160, 40));
        rightCard.add(starRatingLabel);

        avgRatingLabel = new JLabel(String.format("%.1f out of 5", game.getAvgRating()));
        avgRatingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        avgRatingLabel.setForeground(textColor);
        rightCard.add(avgRatingLabel);

        rightCard.add(Box.createVerticalStrut(24));

        JLabel typeHeading = new JLabel("Game Type");
        typeHeading.setFont(new Font("Arial", Font.BOLD, 16));
        typeHeading.setForeground(textColor);
        rightCard.add(typeHeading);

        typeLabel = new JLabel(buildGenreText(game.getGenre()));
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        typeLabel.setForeground(new Color(0, 122, 204));
        typeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        rightCard.add(typeLabel);

        rightCard.add(Box.createVerticalStrut(24));

        JLabel descriptionHeading = new JLabel("Description");
        descriptionHeading.setFont(new Font("Arial", Font.BOLD, 16));
        descriptionHeading.setForeground(textColor);
        rightCard.add(descriptionHeading);

        descriptionArea = new JTextArea(game.getDescription());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 15));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(new Color(245, 245, 245));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(0, 220));
        descriptionScroll.setBorder(BorderFactory.createEmptyBorder());
        rightCard.add(descriptionScroll);

        rightCard.add(Box.createVerticalStrut(24));

        JLabel reviewHeading = new JLabel("Your Review");
        reviewHeading.setFont(new Font("Arial", Font.BOLD, 16));
        reviewHeading.setForeground(textColor);
        rightCard.add(reviewHeading);

        userRatingLabel = new JLabel("Your Rating: 0/5");
        userRatingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userRatingLabel.setForeground(new Color(100, 100, 100));
        rightCard.add(userRatingLabel);

        reviewsArea = new JTextArea();
        reviewsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewsArea.setLineWrap(true);
        reviewsArea.setWrapStyleWord(true);
        reviewsArea.setEditable(false);
        reviewsArea.setBackground(new Color(245, 245, 245));
        reviewsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane reviewsScroll = new JScrollPane(reviewsArea);
        reviewsScroll.setPreferredSize(new Dimension(0, 280));
        reviewsScroll.setBorder(BorderFactory.createEmptyBorder());
        rightCard.add(reviewsScroll);

        refreshRatingDisplay();
        refreshReviewsDisplay();
        refreshUserRatingDisplay();

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.55;
        gbc.weighty = 1.0;
        contentPanel.add(rightCard, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private RoundedCornerButton createStyledButton(String text, Color bg, Color fg, Color hover) {
        RoundedCornerButton button = new RoundedCornerButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 55));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                        Math.max(0, bg.getRed() - 20),
                        Math.max(0, bg.getGreen() - 20),
                        Math.max(0, bg.getBlue() - 20)));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
        return button;
    }

    private void setupSearchBarPlaceholder() {
        final String placeholderText = "Search for game...";
        final Color placeholderColor = new Color(150, 150, 150);
        final Color textColor = Color.BLACK;

        searchBar.setText(placeholderText);
        searchBar.setForeground(placeholderColor);

        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals(placeholderText)) {
                    searchBar.setText("");
                    searchBar.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().trim().isEmpty()) {
                    searchBar.setText(placeholderText);
                    searchBar.setForeground(placeholderColor);
                }
            }
        });

        searchBar.addActionListener(e -> {
            String query = searchBar.getText();
            if (!query.equals(placeholderText) && !query.trim().isEmpty()) {
                app.showDashboardView(username);
            }
        });
    }

    private JButton createActionButton(String text, Color bg, Color fg, Color hover, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
        return button;
    }

    private String buildGenreText(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "Unknown";
        }
        return String.join(", ", genres);
    }

    private String buildStarText(float avgRating) {
        int stars = Math.round(avgRating);
        stars = Math.max(0, Math.min(stars, 5));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append(i < stars ? "★" : "☆");
            if (i < 4) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private void refreshRatingDisplay() {
        float average = game.getAvgRating();
        starRatingLabel.setText(buildStarText(average));
        avgRatingLabel.setText(String.format("%.1f out of 5", average));
    }

    /** Refresh the UI to show the current review list for the game. */
    private void refreshUserRatingDisplay() {
        int userRating = accountDatabase.getUserRating(username, game.getTitle());
        for (int i = 0; i < 5; i++) {
            if (i < userRating) {
                starButtons[i].setText("★");
                /** Gold color for filled stars. */
                starButtons[i].setForeground(new Color(255, 215, 0));
            } else {
                starButtons[i].setText("☆");
                starButtons[i].setForeground(Color.GRAY);
            }
        }
        if (userRatingLabel != null) {
            userRatingLabel.setText("Your Rating: " + userRating + "/5");
        }
    }

    private void setUserRating(int rating) {
        accountDatabase.setUserRating(username, game.getTitle(), rating);
        refreshUserRatingDisplay();
        JOptionPane.showMessageDialog(this, "Rating saved!", "Thank you", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Refresh the UI to show the current review for the user on this game. */
    private void refreshReviewsDisplay() {
        String userReview = accountDatabase.getUserReview(username, game.getTitle());
        if (userReview == null || userReview.trim().isEmpty()) {
            reviewsArea.setText("No review yet.");
        } else {
            reviewsArea.setText(userReview);
        }
    }

    /** Prompt the user to submit a text review for this game. */
    private void promptReview() {
        String existingReview = accountDatabase.getUserReview(username, game.getTitle());
        JTextArea reviewInput = new JTextArea(existingReview != null ? existingReview : "");
        reviewInput.setFont(new Font("Arial", Font.PLAIN, 14));
        reviewInput.setLineWrap(true);
        reviewInput.setWrapStyleWord(true);
        reviewInput.setPreferredSize(new Dimension(400, 250));
        JScrollPane scrollPane = new JScrollPane(reviewInput);

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Write your review:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String review = reviewInput.getText().trim();
            if (review.isEmpty()) {
                accountDatabase.setUserReview(username, game.getTitle(), "");
            } else {
                accountDatabase.setUserReview(username, game.getTitle(), review);
            }
            refreshReviewsDisplay();
            refreshUserRatingDisplay();
            JOptionPane.showMessageDialog(this, "Review saved!", "Thank you", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Show a dialog that lets the user choose an existing collection or create a new one. */
    private void showAddToCollectionDialog() {
        JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Manage Collections", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);

        DefaultListModel<String> collectionModel = new DefaultListModel<>();
        for (String collectionName : accountDatabase.getCollectionNames(username)) {
            collectionModel.addElement(collectionName);
        }

        JList<String> collectionList = new JList<>(collectionModel);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        collectionList.setFont(new Font("Arial", Font.PLAIN, 14));
        collectionList.setFixedCellHeight(32);

        /** Renderer that shows a checkmark next to collections containing this game. */
        collectionList.setCellRenderer(new javax.swing.ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                List<String> gamesInCollection = accountDatabase.getCollectionGameIds(username, value);
                boolean hasGame = gamesInCollection.contains(game.getTitle());
                JLabel label = new JLabel((hasGame ? "✔  " : "     ") + value);
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(new Color(0, 122, 204));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(hasGame ? new Color(0, 140, 60) : new Color(33, 37, 41));
                }
                return label;
            }
        });

        JScrollPane listScroll = new JScrollPane(collectionList);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        /** Buttons stacked vertically on the right. */
        JButton addButton = new JButton("Add to Collection");
        JButton removeButton = new JButton("Remove from Collection");
        JButton newCollectionButton = new JButton("Create New Collection");
        JButton closeButton = new JButton("Close");

        Dimension btnSize = new Dimension(200, 36);
        addButton.setPreferredSize(btnSize);
        removeButton.setPreferredSize(btnSize);
        newCollectionButton.setPreferredSize(btnSize);
        closeButton.setPreferredSize(btnSize);

        addButton.setBackground(new Color(0, 122, 204));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);

        newCollectionButton.setBackground(new Color(40, 167, 69));
        newCollectionButton.setForeground(Color.WHITE);
        newCollectionButton.setFocusPainted(false);

        closeButton.setFocusPainted(false);

        addButton.addActionListener(e -> {
            String selectedCollection = collectionList.getSelectedValue();
            if (selectedCollection == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a collection first.", "Select a Collection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> existingGameIds = accountDatabase.getCollectionGameIds(username, selectedCollection);
            if (existingGameIds.contains(game.getTitle())) {
                JOptionPane.showMessageDialog(dialog, "This game is already in the selected collection.", "Already Added", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            accountDatabase.addGameToCollection(username, selectedCollection, game.getTitle());
            collectionList.repaint();
        });

        removeButton.addActionListener(e -> {
            String selectedCollection = collectionList.getSelectedValue();
            if (selectedCollection == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a collection first.", "Select a Collection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedCollection.equals("All Games")) {
                JOptionPane.showMessageDialog(dialog, "Cannot remove from 'All Games' collection.", "Cannot Remove", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> existingGameIds = accountDatabase.getCollectionGameIds(username, selectedCollection);
            if (!existingGameIds.contains(game.getTitle())) {
                JOptionPane.showMessageDialog(dialog, "This game is not in the selected collection.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            accountDatabase.removeGameFromCollection(username, selectedCollection, game.getTitle());
            collectionList.repaint();
        });

        newCollectionButton.addActionListener(e -> {
            String collectionName = JOptionPane.showInputDialog(dialog, "Enter a new collection name:", "New Collection", JOptionPane.PLAIN_MESSAGE);
            if (collectionName == null) {
                return;
            }
            collectionName = collectionName.trim();
            if (collectionName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Collection name cannot be empty.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (collectionModel.contains(collectionName)) {
                JOptionPane.showMessageDialog(dialog, "A collection with that name already exists.", "Duplicate Collection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            accountDatabase.createCollection(username, collectionName);
            collectionModel.addElement(collectionName);
            collectionList.setSelectedValue(collectionName, true);
        });

        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(newCollectionButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(closeButton);

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel heading = new JLabel("Select a collection — ✔ means this game is already added:");
        heading.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(heading, BorderLayout.NORTH);
        contentPanel.add(listScroll, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);

        dialog.setContentPane(contentPanel);
        dialog.setVisible(true);
    }
}
