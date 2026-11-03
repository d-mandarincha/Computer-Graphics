import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BresenhamLineAlgorithm implements RasterAlgorithm {
    @Override
    public String getName() {
        return "Брезенхем (отрезок)";
    }

    @Override
    public List<Point> drawLine(int x1, int y1, int x2, int y2) {
        List<Point> pixels = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int error = dx - dy;

        while (true) {
            pixels.add(new Point(x1, y1));

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int doubledError = 2 * error;

            if (doubledError > -dy) {
                error -= dy;
                x1 += sx;
            }
            if (doubledError < dx) {
                error += dx;
                y1 += sy;
            }
        }

        return pixels;
    }
}
