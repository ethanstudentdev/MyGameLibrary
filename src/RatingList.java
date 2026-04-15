import java.util.ArrayList;

public class RatingList {
    //Fields
    final ArrayList<Integer> ratings;

    //Constructors
    public RatingList() {
        this.ratings = new ArrayList<Integer>();
    }

    //Methods
    public int getSize(){return ratings.size();}

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

    public void addRating(int rating) {ratings.add(rating);}
}
