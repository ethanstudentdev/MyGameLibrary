import java.util.Comparator;

interface GameComparator extends Comparator<BoardGame> {
    @Override
    int compare(BoardGame g1, BoardGame g2);
}
