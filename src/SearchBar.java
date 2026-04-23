import javax.swing.*;
import java.awt.*;

/**
 * Not complete.
 * Once complete will be a functional searchbar that prompts Search() with the query entered.
 *
 * @author Levi Snellgrove
 */
public class SearchBar extends JTextField {
    JTextField searchBar;

    SearchBar(){
        searchBar = new JTextField();

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

    public JTextField getSearchBar(){
        return searchBar;
    }

}