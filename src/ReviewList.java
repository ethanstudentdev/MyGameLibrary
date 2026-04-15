import java.util.ArrayList;

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
