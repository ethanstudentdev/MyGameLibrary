import java.util.ArrayList;

/**
 * A class that represents a list of reviews for a board game and has associated methods
 *
 * @author Ethan Johnson
 */
public class ReviewList
{
    //Fields
    final ArrayList<String> reviews;

    //Constructors

    /**
     * Constructs a ReviewList object
     */
    public ReviewList() {this.reviews = new ArrayList<String>();}

    //Methods

    /**
     * Gets the number of strings in ReviewList
     *
     * @return The number of strings in the review list - int
     */
    public int getSize(){return reviews.size();}

    /**
     * Adds a review to the review list
     *
     * @param review The review to add to the list - String
     */
    public void addReview(String review) {reviews.add(review);}
}
