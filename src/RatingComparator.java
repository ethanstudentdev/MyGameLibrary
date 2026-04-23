public class RatingComparator implements GameComparator{
    @Override
    public int compare(BoardGame g1, BoardGame g2) {
        float avg1 = g1.getAvgRating();
        float avg2 = g2.getAvgRating();
        return Float.compare(avg2, avg1);
    }
}
