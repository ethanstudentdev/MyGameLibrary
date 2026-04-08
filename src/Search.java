/**
 * The processing that will inform the displayed result of a search.
 * Should be able to filter games that are not a part of the desired type
 * (When searching by genre nothing from another genre should appear).
 * Should also be able to sort games by different priority (Name, publisher
 * rating, and ascending/descending). Lastly, it should be able to search
 * for games with the typed query in its title.
 *
 * @author Levi Snellgrove
 */
public class Search {
    private String query;
    private GameCollection source;

    /**
     * The constructor that will take in the search criteria
     * and eventually prompt a browse view frame to display the search
     * results computed.
     *
     * @param query the string that the user inputs which will be compared
     *              to game titles.
     * @param genre the genre by which we will filter games. There may be
     *              more filters in the future.
     * @param source the collection of games we are searching. Could be all
     *               games in the database, a user made collection, etc
     * @param priority the order that the games will be sorted once filtered.
     */
    public Search(String query, String genre, GameCollection source, GameComparator priority){
        this.query = query;
        this.source = source;
    }

    private GameCollection filterByGenre(){ return null;}
}
