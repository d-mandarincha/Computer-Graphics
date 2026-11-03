import java.awt.Point;
import java.util.List;

public interface RasterAlgorithm {
    String getName();

    List<Point> drawLine(int x1, int y1, int x2, int y2);

    default List<Point> drawCircle(int xc, int yc, int radius) {
        throw new UnsupportedOperationException("Этот алгоритм не строит окружности.");
    }
}
