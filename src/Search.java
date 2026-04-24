import java.util.ArrayList;

/**
 * The processing that will inform the displayed result of a search.
 * Should be able to filter games that are not a part of the desired type
 * (When searching by genre nothing from another genre should appear).
 * Should also be able to sort games by different priority (Name, publisher
 * rating, and ascending/descending). Lastly, it should be able to search
 * for games with the typed query in its title.
 *
 * @author Levi Snellgrove
 * @author Ethan Johnson
 */
public class Search {
    private String query;
    private String genre;
    private GameCollection source;

    /**
     * The constructor that will take in the search criteria
     * and eventually prompt a browse view frame to display the search
     * results computed.
     *
     * @param query the string that the user inputs which will be compared
     *              to game titles.
     * @param source the collection of games we are searching. Could be all
     *               games in the database, a user made collection, etc
     */
    public Search(String query, GameCollection source)
    {
        this.query = query;
        this.source = source;
    }


    /**
     * Searches a collection by title
     *
     * @return
     */
    public GameCollection searchByTitle()
    {
        //A game collection to be displayed as search result
        GameCollection searchCol = new GameCollection("searchCollection");

      //Creates a search collection BY TITLE
        for(BoardGame game : source.getGames())
        {
            String q = query.toLowerCase();
            String title = game.getTitle().toLowerCase();
            if(q.equals(title))
            {
                searchCol.addGame(game);
            } else if (title.contains(q)) {
                searchCol.addGame(game);
            }
        }
        return searchCol;
    }

    /**
     * Searches a collection by genre
     *
     * @return
     */
    public GameCollection searchByGenre()
    {
        //A game collection to be displayed as search result
        GameCollection searchCol = new GameCollection("searchCollection");

        //Creates a search collection BY GENRE
        for(BoardGame game : source.getGames())
        {
            String q = query.toLowerCase();
            ArrayList<String> genres = game.getGenre();
            for(String g : genres)
            {
                g = g.toLowerCase();
                if(q.equals(g))
                {
                    searchCol.addGame(game);
                } else if (g.contains(q)) {
                    searchCol.addGame(game);
                }
            }
        }
        return searchCol;
    }
}
