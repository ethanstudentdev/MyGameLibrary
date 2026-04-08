import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * DashboardView is the view the user reaches after login,
 * it allows the user to search (through text or genre button)
 * in addition to the standard My Collections/Dashboard/Logout
 *
 * @author Levi Snellgrove
 */
public class DashboardView extends JFrame{
    //Private ui elements, needed for initializing then adding behavior in different scopes
    private RoundedCornerButton collections;
    private RoundedCornerButton dashboard;
    private RoundedCornerButton logout;

    private RoundedCornerButton coop;
    private RoundedCornerButton competitive;
    private RoundedCornerButton strategy;
    private RoundedCornerButton fantasy;

    private JTextField searchBar;

    /**
     * The constructor that initializes the UI and sets up its behavior
     */
    public DashboardView()
    {
        initializeUI();
        initializeBehavior();
    }

    /**
     * UI initialization.
     * Makes a frame with the buttons and search bar needed for the required functions.
     */
    public void initializeUI(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame frame = new JFrame("MyGameLibrary - Dashboard");
        frame.setLayout(new BorderLayout());
        //setting frame size
        frame.setSize(screenSize.width, screenSize.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setting up panels/subpanels for specific ui layout
        JPanel top = new JPanel(new BorderLayout());
        JPanel topSubpanel = new JPanel(new GridLayout(1,3,5,0));
        JPanel middle = new JPanel();
        JPanel middleSubpanel = new JPanel(new GridLayout(2,1,0,10));
        JPanel bottom = new JPanel(new GridLayout(1, 4, 15, 10));

        // setting up panel sizing
        top.setPreferredSize(new Dimension(screenSize.width, ((2 * screenSize.height )/ 21) ));
        middle.setPreferredSize(new Dimension(screenSize.width, ((9 * screenSize.height) / 21)));
        bottom.setPreferredSize(new Dimension(screenSize.width, ((10 * screenSize.height) / 21)));

        topSubpanel.setPreferredSize(new Dimension((9 * screenSize.width )/ 21, ((2 * screenSize.height )/ 21) ));
        middleSubpanel.setPreferredSize(new Dimension(((11 * screenSize.width) / 21), ((9 * screenSize.height) / 21) ));

        //adding panels to frame
        frame.add(top, BorderLayout.NORTH);
        frame.add(middle, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        //Creating top right buttons
        collections = new RoundedCornerButton("My Collections");
        dashboard = new RoundedCornerButton("Dashboard");
        logout = new RoundedCornerButton("Logout");

        //Creating search bar and label
        searchBar = new JTextField("Search");
        searchBar.setBorder(new LineBorder(Color.GRAY, 15, true));
        searchBar.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel genreSearchLabel = new JLabel("Browse by game type");
        genreSearchLabel.setHorizontalAlignment(SwingConstants.CENTER);

        genreSearchLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        //creating genre search buttons
        coop = new RoundedCornerButton("Co-op");
        competitive = new RoundedCornerButton("Competitive");
        strategy = new RoundedCornerButton("Strategy");
        fantasy = new RoundedCornerButton("Fantasy");

        //adding/filling subpanels
        top.add(topSubpanel, BorderLayout.EAST);
        topSubpanel.add(collections);
        topSubpanel.add(dashboard);
        topSubpanel.add(logout);

        middle.add(middleSubpanel);
        middleSubpanel.add(searchBar);
        middleSubpanel.add(genreSearchLabel);

        bottom.add(coop);
        bottom.add(competitive);
        bottom.add(strategy);
        bottom.add(fantasy);

        // Make the frame visible
        frame.setVisible(true);
    }

    /**
     * Adds behavior to the UI elements.
     */
    public void initializeBehavior(){
        collections.addActionListener(collections);
        dashboard.addActionListener(dashboard);
        logout.addActionListener(logout);

        coop.addActionListener(coop);
        competitive.addActionListener(competitive);
        strategy.addActionListener(strategy);
        fantasy.addActionListener(fantasy);
    }
}
