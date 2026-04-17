import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a single collection of BoardGame objects
 */
public class GameCollection {
    // Fields

    private String title;
    private final ArrayList<BoardGame> games;

    // Constructors

    /**
     * Constructs a GameCollection object that can
     *
     * @param title The title of the GameCollection
     */
    public GameCollection(String title)
    {
        this.title = title;
        this.games = new ArrayList<>();
    }

    /**
     * Constructs a GameCollection object with no passed parameters and defines defaults
     */
    public GameCollection()
    {
        this.title = "default";
        this.games = new ArrayList<>();
    }

    // Methods

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
     * Gets the list of games in this collection.
     *
     * @return List of BoardGame objects in the collection
     */
    public List<BoardGame> getGames() {
        return games;
    }
    public void printAllNames() {
        for(BoardGame game : games) {
            System.out.println(game.getTitle());
        }
    }
}
