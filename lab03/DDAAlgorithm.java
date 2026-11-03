import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DDAAlgorithm implements RasterAlgorithm {
    @Override
    public String getName() {
        return "Алгоритм ЦДА (DDA)";
    }

    @Override
    public List<Point> drawLine(int x1, int y1, int x2, int y2) {
        List<Point> pixels = new ArrayList<>();

        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            pixels.add(new Point(x1, y1));
            return pixels;
        }

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;
        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            pixels.add(new Point((int) Math.round(x), (int) Math.round(y)));
            x += xIncrement;
            y += yIncrement;
        }

        return pixels;
    }
}
