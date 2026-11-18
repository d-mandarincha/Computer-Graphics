import java.util.ArrayList;
import java.util.List;

public class MidpointClipping implements ClippingAlgorithm {

    private static final int MAX_DEPTH = 45;
    private static final double MIN_LENGTH = 0.001;

    public MidpointClipping() {
    }

    // Оставлен для совместимости с прежним вызовом конструктора.
    public MidpointClipping(double scale) {
    }

    @Override
    public String getName() {
        return "Метод средней точки";
    }

    @Override
    public List<Segment> clip(
            Segment segment,
            List<Point> polygon
    ) {
        List<Segment> result = new ArrayList<>();

        if (polygon == null || polygon.size() < 3) {
            return result;
        }

        clipRecursive(
                segment.p1,
                segment.p2,
                polygon,
                result,
                0
        );

        return mergeSegments(result);
    }

    private void clipRecursive(
            Point a,
            Point b,
            List<Point> polygon,
            List<Segment> result,
            int depth
    ) {
        ClippingWindow window = new ClippingWindow(polygon);

        boolean aInside = window.contains(a);
        boolean bInside = window.contains(b);

        // Если обе точки внутри выпуклого многоугольника,
        // весь отрезок находится внутри.
        if (aInside && bInside) {
            result.add(new Segment(a, b));
            return;
        }

        // Проверка пересечения ограничивающего прямоугольника.
        if (Math.max(a.x, b.x) < window.getMinX()
                || Math.min(a.x, b.x) > window.getMaxX()
                || Math.max(a.y, b.y) < window.getMinY()
                || Math.min(a.y, b.y) > window.getMaxY()) {
            return;
        }

        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (depth >= MAX_DEPTH || length <= MIN_LENGTH) {
            Point middle = new Point(
                    (a.x + b.x) / 2,
                    (a.y + b.y) / 2
            );

            if (window.contains(middle)) {
                result.add(new Segment(a, b));
            }

            return;
        }

        Point middle = new Point(
                (a.x + b.x) / 2,
                (a.y + b.y) / 2
        );

        clipRecursive(a, middle, polygon, result, depth + 1);
        clipRecursive(middle, b, polygon, result, depth + 1);
    }

    private List<Segment> mergeSegments(List<Segment> segments) {
        if (segments.isEmpty()) {
            return segments;
        }

        List<Segment> merged = new ArrayList<>();

        Segment current = segments.get(0);

        for (int i = 1; i < segments.size(); i++) {
            Segment next = segments.get(i);

            double dx = current.p2.x - next.p1.x;
            double dy = current.p2.y - next.p1.y;

            if (Math.sqrt(dx * dx + dy * dy) < 0.002) {
                current = new Segment(current.p1, next.p2);
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);
        return merged;
    }
}