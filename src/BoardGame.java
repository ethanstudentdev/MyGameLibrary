import java.util.ArrayList;
import java.util.Comparator;

/**
 * A class that represents a single BoardGame object
 *
 * @author Ethan Johnson
 */
public class BoardGame {
    //Fields

    private String title;
    private String description;
    private RatingList ratings;
    private ReviewList reviews;
    private final ArrayList<String> genre;

    //Constructors

    /**
     * Constructs a BoardGame object that can
     *
     * @param title        The title of the Board Game
     * @param description  The summary description of the Board Game
     * @param ratings      All the ratings of the board game
     * @param reviews      All the reviews of the board game
     */
    public BoardGame(String title, String description, RatingList ratings, ReviewList reviews)
    {
        this.title = title;
        this.description = description;
        this.genre = new ArrayList<String>();
        this.ratings = ratings;
        this.reviews = reviews;
    }

    /**
     *  Constructs a BoardGame object with no passed parameters and defines defaults
     */
    public BoardGame()
    {
        this.title = "no title found";
        this.description = "no description found";
        this.genre = new ArrayList<String>();
    }

    //Methods

    //Getters//
    /**
     * Gets the description of a BoardGame object
     *
     * @return The description of the BoardGame
     */
    public String getDescription(){return description;}

    /**
     * Gets the list of genres of a BoardGame object
     *
     * @return The arraylist of genres of the BoardGame
     */
    public ArrayList<String> getGenre() {return genre;}

    /**
     * Gets the title for a BoardGame object
     *
     * @return String representing title of a Board Game
     */
    public String getTitle(){return title;}

    //Adders

    /**
     * Adds a genre to the genres
     *
     * @param aGenre The genre that is being added to the genre arrayList object
     */
    public void addGenre(String aGenre) {genre.add(aGenre);}

    //Setters//
    /**
     * Sets the title for a BoardGame object
     *
     * @param title The title of the Board Game
     */
    public void setTitle(String title){this.title = title;}

    /**
     * Sets the description for a BoardGame object
     *
     * @param description The description of the Board Game
     */
    public void setDescription(String description){this.description = description;}

    //Comparators
    /**
     *  Compares a board game to another by its title
     */
    public static Comparator<BoardGame> byTitle =
            Comparator.comparing(BoardGame::getTitle);
}
