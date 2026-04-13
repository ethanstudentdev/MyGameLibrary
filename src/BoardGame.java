import java.util.ArrayList;
/**
 * A class that represents a single BoardGame object
 */
public class BoardGame {
    //Fields

    private String title;
    private String description;
    private String publisher;
    private RatingList ratings;
    private ReviewList reviews;
    private String genre;

    //Constructors

    /**
     * Constructs a BoardGame object that can
     *
     * @param title        The title of the Board Game
     * @param description  The summary description of the Board Game
     * @param publisher    The publisher of the Board Game
     * @param genre        The genre of the Board Game
     * @param ratings      All the ratings of the board game
     * @param reviews      All the reviews of the board game
     */
    public BoardGame(String title, String description, String publisher, String genre, RatingList ratings, ReviewList reviews)
    {
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.genre = genre;
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
        this.publisher = "no publisher found";
        this.genre = "no genre found";
    }

    //Methods

    /**
     * Gets the description of a BoardGame object
     *
     * @return The description of the BoardGame
     */
    public String getDescription(){return description;}

    /**
     * Gets the publisher of a BoardGame object
     *
     * @return The publisher of the BoardGame
     */
    public String getPublisher(){return publisher;}

    /**
     * Gets the genre of a BoardGame object
     *
     * @return The genre of the BoardGame
     */
    public String getGenre(){return genre;}

    /**
     * Sets the title for a BoardGame object
     *
     * @param title The title of the Board Game
     */
    public void setTitle(String title){this.title = title;}

    public String getTitle(){return title;}

    /**
     * Gets the ratings of a BoardGame object
     *
     * @return The ratings of the BoardGame
     */
    public RatingList getRatings(){return ratings;}

    /**
     * Gets the reviews of a BoardGame object
     *
     * @return The reviews of the BoardGame
     */
    public ReviewList getReviews(){return reviews;}
}
