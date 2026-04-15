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
    private final RatingList ratings;
    private final ReviewList reviews;
    private String image;
    private String publisher;
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
    public BoardGame(String title, String description, RatingList ratings, ReviewList reviews, String image, String publisher)
    {
        this.title = title;
        this.description = description;
        this.genre = new ArrayList<String>();
        this.ratings = ratings;
        this.reviews = reviews;
        this.image = image;
        this.publisher = publisher;
    }

    /**
     *  Constructs a BoardGame object with no passed parameters and defines defaults
     */
    public BoardGame()
    {
        this.title = "no title found";
        this.description = "no description found";
        this.image = "no image found";
        this.publisher = "no publisher found";
        this.ratings = new RatingList();
        this.reviews = new ReviewList();
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
     * @return The arraylist of genres of a Board Game
     */
    public ArrayList<String> getGenre() {return genre;}

    /**
     * Gets the title for a BoardGame object
     *
     * @return String representing title of a Board Game
     */
    public String getTitle(){return title;}

    /**
     * Gets the image link for a BoardGame object
     *
     * @return String representing title of a Board Game
     */
    public String getImage(){return image;}

    /**
     * Gets the publisher for a BoardGame object
     *
     * @return String representing the publisher of a Board Game
     */
    public String getPublisher(){return publisher;}

    /**
     * Gets the ReviewList for a BoardGame object
     *
     * @return ReviewList representing all reviews of a board game
     */
    public ReviewList getReviews(){return reviews;}

    /**
     * Gets the RatingList for a BoardGame object
     *
     * @return RatingList representing all ratings of a board game
     */
    public RatingList getRatings(){return ratings;}

    //Adders

    /**
     * Adds a genre to the genres
     *
     * @param aGenre The genre that is being added to the genre arrayList object
     */
    public void addGenre(String aGenre) {genre.add(aGenre);}

    /**
     * Adds a rating to a BoardGames ratinglist
     *
     * @param rating The rating that is being added to the ratingList object
     */
    public void addRating(int rating) {ratings.addRating(rating);}

    /**
     * Adds a review to the BoardGames reviewList
     *
     * @param review The review that is being added to the reviewList object
     */
    public void addReview(String review){reviews.addReview(review);}

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

    /**
     * Sets the image link for a BoardGame object
     *
     * @param image The description of the Board Game
     */
    public void setImage(String image){this.image = image;}

    /**
     * Sets the publisher of a BoardGame object
     *
     * @param publisher The publisher of a Board Game
     */
    public void setPublisher(String publisher){this.publisher = publisher;}

    //Returns//
    /**
     *  Calculates the average rating for a BoardGame object
     *
     * @return The average rating for a BoardGame
     */
    public float getAvgRating() {return ratings.getAverage();}

    //Comparators
    /**
     *  Compares a board game to another by its title
     */
    public static Comparator<BoardGame> byTitle =
            Comparator.comparing(BoardGame::getTitle);

    /**
     *  Compares a board game to another by its rating average
     */
    public static Comparator<BoardGame> byRating =
            Comparator.comparing(BoardGame::getAvgRating);
}
