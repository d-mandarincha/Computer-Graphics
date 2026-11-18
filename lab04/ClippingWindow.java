import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClippingWindow {
    private final List<Point> vertices;

    public ClippingWindow(List<Point> vertices) {
        if (vertices == null || vertices.size() < 3) {
            throw new IllegalArgumentException(
                    "У многоугольника должно быть минимум 3 вершины"
            );
        }

        this.vertices = new ArrayList<>(vertices);

        if (!isConvex()) {
            throw new IllegalArgumentException(
                    "Окно отсечения должно быть выпуклым многоугольником"
            );
        }
    }

    public List<Point> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public boolean contains(Point point) {
        double sign = 0;
        int n = vertices.size();

        for (int i = 0; i < n; i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % n);

            double cross =
                    (b.x - a.x) * (point.y - a.y)
                    - (b.y - a.y) * (point.x - a.x);

            if (Math.abs(cross) < 1e-9) {
                continue;
            }

            if (sign == 0) {
                sign = Math.signum(cross);
            } else if (Math.signum(cross) != sign) {
                return false;
            }
        }

        return true;
    }

    public double getMinX() {
        return vertices.stream()
                .mapToDouble(p -> p.x)
                .min().orElse(0);
    }

    public double getMaxX() {
        return vertices.stream()
                .mapToDouble(p -> p.x)
                .max().orElse(0);
    }

    public double getMinY() {
        return vertices.stream()
                .mapToDouble(p -> p.y)
                .min().orElse(0);
    }

    public double getMaxY() {
        return vertices.stream()
                .mapToDouble(p -> p.y)
                .max().orElse(0);
    }

    private boolean isConvex() {
        int n = vertices.size();
        double direction = 0;

        for (int i = 0; i < n; i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % n);
            Point c = vertices.get((i + 2) % n);

            double cross =
                    (b.x - a.x) * (c.y - b.y)
                    - (b.y - a.y) * (c.x - b.x);

            if (Math.abs(cross) < 1e-9) {
                continue;
            }

            if (direction == 0) {
                direction = Math.signum(cross);
            } else if (Math.signum(cross) != direction) {
                return false;
            }
        }

        return direction != 0;
    }
}