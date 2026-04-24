import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BrowseView is the frame that shows up when a search is made.
 * It has the search results in a table as well as genre search buttons and the
 * standard top row (search bar, collections button, dashboard button, and logout button).
 *
 * @author Levi Snellgrove
 */
public class BrowseView extends JPanel {
    public JPanel frame; //main frame, convert

    //all variables for constructor
    private String username;
    private MyGameLibraryApp app;
    private GameCollection results;
    private Admin admin;

    //Variables for genre selection
    private ArrayList<String> categoryNames;
    private List<RoundedCornerButton> categoryButtons;
    private RoundedCornerButton leftArrow;
    private RoundedCornerButton rightArrow;

    //Table and model used to display search results
    JTable searchResultTable;
    DefaultTableModel model;

    SearchBar searchBar;

    RoundedCornerButton collections;
    RoundedCornerButton dashboard;
    RoundedCornerButton logout;

    //Booleans for sorting
    private boolean titleSortReverse = false;
    private boolean publisherSortReverse = false;
    private boolean ratingSortReverse = false;

    //This view will be constructed only by the search function so
    //no need to worry about other constructors

    /**
     * Basic constructor for BrowseView.
     *
     * @param username username of the user accessing the browse view.
     * @param results the gameCollection to be displayed that is the search results.
     * @param app the app instance to manage view switching.
     * @param admin
     */
    public BrowseView(String username, GameCollection results, MyGameLibraryApp app, Admin admin){
        this.results = results;
        this.username = username;
        this.app = app;
        this.admin = admin;

        this.categoryNames = results.getGenres();

        initializeUI();
        initializeBehavior();
    }

    /**
     * Initializes UI for browse view
     */
    private void initializeUI(){
        //Colors for ui, taken from nathaniels code
        //thanks nathaniel
        Color backgroundColor = new Color(250, 250, 252);
        Color accentColor = new Color(0, 122, 204);
        Color textColor = new Color(33, 37, 41);
        Color buttonColor = new Color(108, 117, 125);
        Color buttonHoverColor = new Color(90, 98, 104);
        Color logoutButtonColor = new Color(220, 53, 69);
        Color logoutButtonHoverColor = new Color(200, 35, 51);

        //Setting up frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame = new JPanel();
        frame.setLayout(new BorderLayout());
        frame.setSize(screenSize.width, screenSize.height);
        frame.setBackground(backgroundColor);

        //setting up subpanels
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(screenSize.width, ((4 * screenSize.height) / 21) ));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setPreferredSize(new Dimension(screenSize.width, ((15 * screenSize.height) / 21) ));

        //subpanel with displayed search results
        JPanel resultsSubpanel = new JPanel();
        resultsSubpanel.setPreferredSize(new Dimension(((11 * screenSize.width) / 21), ((14 * screenSize.height) / 21) ));

        //subpanel with genre search buttons
        JPanel bottomRightSubpanel = new JPanel(new BorderLayout());
        bottomRightSubpanel.setPreferredSize(new Dimension(((9 * screenSize.width) / 21), ((13 * screenSize.height) / 21) ));

        JPanel genreSearch = new JPanel(new GridLayout(3,3,10,10));
        genreSearch.setPreferredSize(new Dimension(((8 * screenSize.width) / 21), ((13 * screenSize.height) / 21) ));

        JLabel resultsLabel = new JLabel("Search Results");
        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel genreSearchLabel = new JLabel("Search by Genre");
        genreSearchLabel.setHorizontalAlignment(SwingConstants.CENTER);
        genreSearchLabel.setFont(new Font("Arial", Font.BOLD, 14));

        //subpanel in top left with search bar
        JPanel searchSubpanel = new JPanel();
        searchSubpanel.setPreferredSize(new Dimension(((9 * screenSize.width) / 21), ((2 * screenSize.height) / 21) ));
        searchBar = new SearchBar(app, username);
        searchBar.getSearchBar().setPreferredSize(new Dimension(((9 * screenSize.width) / 21), ((2 * screenSize.height) / 21) ));
        searchBar.getSearchBar().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        searchBar.getSearchBar().setHorizontalAlignment(SwingConstants.CENTER);
        searchBar.getSearchBar().setFont(new Font("Arial", Font.PLAIN, 18));

        //subpanel with three standard navigation buttons
        JPanel headButtonSubpanel = new JPanel(new GridLayout(1,3,10,1));
        headButtonSubpanel.setPreferredSize(new Dimension(((9 * screenSize.width) / 21), ((2 * screenSize.height) / 21) ));

        collections = createStyledButton("My Collections", buttonColor, Color.WHITE, buttonHoverColor);
        dashboard = createStyledButton("Dashboard", buttonColor, Color.WHITE, buttonHoverColor);
        logout = createStyledButton("Logout", logoutButtonColor, Color.WHITE, logoutButtonHoverColor);

        //adding to the main frame
        frame.add(bottom, BorderLayout.CENTER);
        frame.add(top, BorderLayout.NORTH);

        top.add(resultsLabel, BorderLayout.SOUTH);
        top.add(searchSubpanel, BorderLayout.WEST);
        top.add(headButtonSubpanel, BorderLayout.EAST);
        searchSubpanel.add(searchBar.getSearchBar());
        headButtonSubpanel.add(collections);
        headButtonSubpanel.add(dashboard);
        headButtonSubpanel.add(logout);

        bottom.add(resultsSubpanel, BorderLayout.WEST);
        bottom.add(bottomRightSubpanel, BorderLayout.EAST);
        bottomRightSubpanel.add(genreSearchLabel, BorderLayout.NORTH);
        bottomRightSubpanel.add(genreSearch, BorderLayout.CENTER);

        //setting up genre search buttons
        //Taken from dasshboardview
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
            JPanel column = new JPanel(new GridLayout(5, 1, 0, 20));
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
                        GameParser parser = new GameParser();

                        File allGamesFile = new File("assets",admin.getGamesFile());
                        GameCollection allGames = parser.parse(allGamesFile);

                        Search search = new Search(categoryName, allGames);
                        GameCollection searchCol = search.searchByGenre();

                        app.showBrowseView(username, searchCol);
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
        categoryNavigationPanel.setPreferredSize(new Dimension(((8 * screenSize.width) / 21), ((13 * screenSize.height) / 21) ));

        genreSearch.add(categoryNavigationPanel);

        //done with genre search

        //Initializing the results table
        searchResultTable = new JTable();
        searchResultTable.setRowHeight(80);
        initializeTable(searchResultTable, results, model);
        JScrollPane scrollPane = new JScrollPane(searchResultTable);
        scrollPane.setPreferredSize(new Dimension(((11 * screenSize.width) / 21), ((17 * screenSize.height) / 21) ));
        resultsSubpanel.add(scrollPane);

        frame.setVisible(true);
    }

    /**
     * Initializes the behavior of necessary UI elements.
     */
    private void initializeBehavior(){
        collections.addActionListener(e -> {
            app.showCollectionsView(username);
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
    }

    //Taken from dashboard view
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

    /**
     * The method that fills a table with the search results to be displayed.
     *
     * @param table The table to be filled
     * @param contents The search results
     * @param model The table model to assign to the table for cosmetics and functionality of the table.
     */
    private void initializeTable(JTable table, GameCollection contents, DefaultTableModel model){
        model = new DefaultTableModel(new Object[]{"Cover", "Name", "Publisher", "Average Rating"}, 0) {
            //this lets the table display images in column 0
            @Override
            public Class<?> getColumnClass(int column) {
                // Column 0 is the Image Column
                return column == 0 ? ImageIcon.class : Object.class;
            }
            //make it uneditable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);

        //for all the games in our search results
        for(BoardGame game : contents.getGames()){
            ImageIcon imageIcon = null;
            try {
                //get the image url
                URL url = new URL(game.getImage());
                //and make imageicon that image
                imageIcon = new ImageIcon(url);

                //Resizing image
                Image img = imageIcon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(img);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Default image would go here
            }

            model.addRow(new Object[]{
                    imageIcon,
                    game.getTitle(),
                    game.getPublisher(),
                    game.getAvgRating()
            });
        }

        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int viewColumn = table.columnAtPoint(e.getPoint());

                if (viewColumn != -1)
                {
                    //Converts the current columns index to whatever it is when pressed
                    int modelColumn = table.convertColumnIndexToModel(viewColumn);
                    //call to sort header
                    sortHeader(modelColumn, results);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.convertRowIndexToModel(table.getSelectedRow());
                    if (results.getGames() != null && row >= 0 && row < results.getGames().size()) {
                        app.showGameScreenView(results.getGames().get(row), username);
                    }
                }
            }
        });
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

    private void sortHeader(int column, GameCollection collection)
    {
        switch (column) {
            case 0:
                System.out.println("Cover header clicked");
                break;
            case 1:
                System.out.println("Name header clicked");
                if (!titleSortReverse) {
                    collection.getGames().sort(BoardGame.byTitle);
                    titleSortReverse = true;
                } else if (titleSortReverse) {
                    collection.getGames().sort(BoardGame.byTitle.reversed());
                    titleSortReverse = false;
                }
                initializeTable(searchResultTable, collection, model);
                break;
            case 2:
                System.out.println("Publisher header clicked");
                if (!publisherSortReverse) {
                    collection.getGames().sort(BoardGame.byPublisher);
                    publisherSortReverse = true;
                } else if (publisherSortReverse) {
                    collection.getGames().sort(BoardGame.byPublisher.reversed());
                    publisherSortReverse = false;
                }
                initializeTable(searchResultTable, collection, model);
                break;
            case 3:
                //TODO: Doesnt work - fix
                System.out.println("Average Rating header clicked");
                if (!ratingSortReverse) {
                    collection.getGames().sort(BoardGame.byRating);
                    ratingSortReverse = true;
                } else if (ratingSortReverse) {
                    collection.getGames().sort(BoardGame.byRating.reversed());
                    ratingSortReverse = false;
                }
                initializeTable(searchResultTable, collection, model);
                break;
            }
        }
}
