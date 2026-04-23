import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * BrowseView is the frame that shows up when a search is made.
 * It has the search results in a table as well as genre search buttons and the
 * standard top row (search bar, collections button, dashboard button, and logout button).
 *
 * @author Levi Snellgrove
 */
public class BrowseView extends JFrame {
    GameCollection results;

    //Lifely to change in the future, 9 placeholder genre search buttons
    RoundedCornerButton genre1;
    RoundedCornerButton genre2;
    RoundedCornerButton genre3;
    RoundedCornerButton genre4;
    RoundedCornerButton genre5;
    RoundedCornerButton genre6;
    RoundedCornerButton genre7;
    RoundedCornerButton genre8;
    RoundedCornerButton genre9;

    //Table and model used to display search results
    JTable searchResultTable;
    DefaultTableModel model;

    SearchBar searchBar;

    //Booleans for sorting
    private boolean titleSortReverse = false;
    private boolean publisherSortReverse = false;
    private boolean ratingSortReverse = false;

    //This view will be constructed only by the search function so
    //no need to worry about other constructors
    public BrowseView(GameCollection results){
        this.results = results;

        initializeUI();
        //initializeBehavior();
    }

    /**
     * Initializes UI for browse view
     */
    private void initializeUI(){
        //Setting up frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame = new JFrame("MyGameLibrary - Search Results");
        frame.setLayout(new BorderLayout());
        frame.setSize(screenSize.width, screenSize.height);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setting up subpanels
        JPanel top = new JPanel(new BorderLayout());
        top.setPreferredSize(new Dimension(screenSize.width, ((3 * screenSize.height) / 21) ));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setPreferredSize(new Dimension(screenSize.width, ((17 * screenSize.height) / 21) ));

        //empty panel just for padding
        JPanel bottomPadding = new JPanel();
        bottomPadding.setPreferredSize(new Dimension(screenSize.width, (screenSize.height) / 42) );

        //subpanel with displayed search results
        JPanel resultsSubpanel = new JPanel();
        resultsSubpanel.setPreferredSize(new Dimension(((11 * screenSize.width) / 21), ((16 * screenSize.height) / 21) ));
        resultsSubpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        //subpanel with genre search buttons
        JPanel bottomRightSubpanel = new JPanel(new BorderLayout());
        bottomRightSubpanel.setPreferredSize(new Dimension(((9 * screenSize.width) / 21), ((15 * screenSize.height) / 21) ));

        JPanel genreSearch = new JPanel(new GridLayout(3,3,10,10));
        genreSearch.setPreferredSize(new Dimension(((8 * screenSize.width) / 21), ((15 * screenSize.height) / 21) ));
        genreSearch.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel resultsLabel = new JLabel("Search Results");
        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel genreSearchLabel = new JLabel("Search by Genre");
        genreSearchLabel.setHorizontalAlignment(SwingConstants.CENTER);

        searchBar = new SearchBar();
        searchBar.setPreferredSize(new Dimension(((11 * screenSize.width) / 21), ((16 * screenSize.height) / 21) ));

        //adding to the main frame
        frame.add(bottomPadding, BorderLayout.SOUTH);
        frame.add(bottom, BorderLayout.CENTER);
        frame.add(top, BorderLayout.NORTH);

        top.add(resultsLabel, BorderLayout.SOUTH);
        top.add(searchBar.getSearchBar(), BorderLayout.WEST);

        bottom.add(resultsSubpanel, BorderLayout.WEST);
        bottom.add(bottomRightSubpanel, BorderLayout.EAST);
        bottomRightSubpanel.add(genreSearchLabel, BorderLayout.NORTH);
        bottomRightSubpanel.add(genreSearch, BorderLayout.CENTER);

        genre1 = new RoundedCornerButton("1");
        genre2 = new RoundedCornerButton("2");
        genre3 = new RoundedCornerButton("3");
        genre4 = new RoundedCornerButton("4");
        genre5 = new RoundedCornerButton("5");
        genre6 = new RoundedCornerButton("6");
        genre7 = new RoundedCornerButton("7");
        genre8 = new RoundedCornerButton("8");
        genre9 = new RoundedCornerButton("9");

        genreSearch.add(genre1);
        genreSearch.add(genre2);
        genreSearch.add(genre3);
        genreSearch.add(genre4);
        genreSearch.add(genre5);
        genreSearch.add(genre6);
        genreSearch.add(genre7);
        genreSearch.add(genre8);
        genreSearch.add(genre9);

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
