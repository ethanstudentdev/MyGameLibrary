import java.util.ArrayList;

/**
 * A class that represents a list of reviews for a board game and has associated methods
 *
 * @author Ethan Johnson
 */
public class ReviewList {
    //Fields
    final ArrayList<String> reviews;

    //Constructors
    public ReviewList() {
        this.reviews = new ArrayList<String>();
    }

    //Methods
    public int getSize(){return reviews.size();}

    public void addReview(String review) {reviews.add(review);}
}
