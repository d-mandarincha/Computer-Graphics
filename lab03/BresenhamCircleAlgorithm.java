import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BresenhamCircleAlgorithm implements RasterAlgorithm {
    @Override
    public String getName() {
        return "Брезенхем (окружность)";
    }

    @Override
    public List<Point> drawLine(int x1, int y1, int x2, int y2) {
        throw new UnsupportedOperationException("Алгоритм предназначен для окружностей.");
    }

    @Override
    public List<Point> drawCircle(int xc, int yc, int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Радиус не может быть отрицательным.");
        }

        Set<Point> pixels = new LinkedHashSet<>();
        int x = 0;
        int y = radius;
        int decision = 3 - 2 * radius;

        while (x <= y) {
            addSymmetricPoints(pixels, xc, yc, x, y);

            if (decision < 0) {
                decision += 4 * x + 6;
            } else {
                decision += 4 * (x - y) + 10;
                y--;
            }
            x++;
        }

        return new ArrayList<>(pixels);
    }

    private void addSymmetricPoints(Set<Point> pixels, int xc, int yc, int x, int y) {
        pixels.add(new Point(xc + x, yc + y));
        pixels.add(new Point(xc - x, yc + y));
        pixels.add(new Point(xc + x, yc - y));
        pixels.add(new Point(xc - x, yc - y));
        pixels.add(new Point(xc + y, yc + x));
        pixels.add(new Point(xc - y, yc + x));
        pixels.add(new Point(xc + y, yc - x));
        pixels.add(new Point(xc - y, yc - x));
    }
}
