import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Once complete will be a functional searchbar that prompts Search() with the query entered.
 *
 * @author Levi Snellgrove
 */
public class SearchBar extends JTextField {
    public JTextField searchBar;

    final String placeholderText = "Search for game...";
    final Color placeholderColor = new Color(150, 150, 150);
    final Color textColor = Color.BLACK;

    private GameCollection allGamesCollection;

    private MyGameLibraryApp app;
    private String username;

    SearchBar(MyGameLibraryApp app, String username) {
        Admin admin = new Admin();
        GameParser parser = new GameParser();

        File allGamesFile = new File("assets",admin.getGamesFile());
        allGamesCollection = parser.parse(allGamesFile);

        this.app = app;
        this.username = username;

        initializeUI();
        initializeBehavior();
    }

    private void initializeUI() {
        searchBar = new JTextField();
        searchBar.setText(placeholderText);
        searchBar.setForeground(placeholderColor);
    }

    private void initializeBehavior(){
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

        searchBar.addActionListener(e -> {
            String input = searchBar.getText().strip();

            System.out.println(input);

            if(!input.isEmpty()) {
                Search search = new Search(input, allGamesCollection);

                app.showBrowseView(username, search.searchByTitle());
            }


        });

    }

    public JTextField getSearchBar(){
        return searchBar;
    }

}