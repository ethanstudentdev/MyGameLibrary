import java.util.ArrayList;
/**
 * A class that represents a single collection of BoardGame objects
 *
 * @author Ethan Johnson
 */
public class GameCollection {
    //Fields

    private String title;
    final ArrayList<BoardGame> games;

    //Constructors

    /**
     * Constructs a GameCollection object that can
     *
     * @param title The title of the GameCollection
     */
    public GameCollection(String title)
    {
        this.title = title;
        this.games = new ArrayList<BoardGame>();
    }

    /**
     *  Constructs a GameCollection object with no passed parameters and defines defaults
     */
    public GameCollection()
    {
        this.title = "default";
        this.games = new ArrayList<BoardGame>();
    }

    //Methods

    /**
     * Sets the title of the gameCollection object from a single passed parameter
     *
     * @param aTitle The title of the Game Collection
     */
    public void setTitle(String aTitle){title = aTitle;}

    /**
     * Adds a single game to an arrayList of all the GameCollections games from a single passed parameter
     *
     * @param aGame The BoardGame that is being added to the gameCollection object
     */
    public void addGame(BoardGame aGame) {games.add(aGame);}

    /**
     * Removes a single game from an arrayList based on BoardGame object name
     *
     * @param aGame The BoardGame that is being removed from the gameCollection object
     */
    public void removeGame(BoardGame aGame) {games.remove(aGame);}

    /**
     * Gets the title of a gameCollection
     *
     * @return The title of the gameCollection
     */
    public String getTitle() {return title;}

    /**
     * Debug method to print all games in collection and their info to the console
     */
    public void printAllGames()
    {
        int i = 0;
        for(BoardGame game : games)
        {
            i++;
            System.out.println("Game # : " + i);
            System.out.println("Title");
            System.out.println(game.getTitle());
            System.out.println("Image Link");
            System.out.println(game.getImage());
            System.out.println("Description");
            System.out.println(game.getDescription());
            System.out.println("Genres");
            for(String genre : game.getGenre())
            {
                System.out.println(genre);
            }
            System.out.println("Average Rating");
            System.out.println(game.getAvgRating());
            System.out.println();
        }
    }

}
