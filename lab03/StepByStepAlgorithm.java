
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class StepByStepAlgorithm implements RasterAlgorithm {

    @Override
    public String getName() {
        return "Пошаговый алгоритм";
    }

    @Override
    public List<Point> drawLine(int x1, int y1, int x2, int y2) {
        List<Point> pixels = new ArrayList<>();

        int dx = x2 - x1;
        int dy = y2 - y1;

        double length = Math.sqrt(
            (double) dx * dx + (double) dy * dy
        );

        if (length == 0) {
            pixels.add(new Point(x1, y1));
            return pixels;
        }

        // Фиксированное количество шагов.
        int steps = (int) Math.ceil(length * 2);

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= steps; i++) {
            Point point = new Point(
                (int) Math.round(x),
                (int) Math.round(y)
            );

            // Не добавляем повторяющиеся клетки.
            if (pixels.isEmpty()
                    || !pixels.get(pixels.size() - 1).equals(point)) {
                pixels.add(point);
            }

            x += xIncrement;
            y += yIncrement;
        }

        return pixels;
    }
}
