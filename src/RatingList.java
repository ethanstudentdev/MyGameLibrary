import java.util.ArrayList;

/**
 * A class that represents a list of ratings for a board game and has associated methods
 *
 * @author Ethan Johnson
 */
public class RatingList
{
    //Fields
    final ArrayList<Integer> ratings;

    //Constructors

    /**
     *  Constructs an arrayList of ratings
     */
    public RatingList()
    {
        this.ratings = new ArrayList<Integer>();
    }

    //Methods

    /**
     * Gets the size of the ratings list
     *
     * @return The size int of rating list
     */
    public int getSize(){return ratings.size();}

    /**
     * Gets the average of all ratings in the rating list
     *
     * @return The average float of rating list or 0 if no ratings exist
     */
    public float getAverage()
    {
        float sum = 0;
        for (int singleRating : ratings)
        {
            sum += singleRating;
        }

        if(Float.isNaN(sum / getSize()))
            return 0;
        else
            return (sum / getSize());
    }

    /**
     * Adds a rating to the rating list
     *
     * @param rating The rating to add to rating list
     */
    public void addRating(int rating) {ratings.add(rating);}
}
