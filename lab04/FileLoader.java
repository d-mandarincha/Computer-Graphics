import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileLoader {

    public static class InputData {
        public List<Segment> segments;
        public ClippingWindow window;

        public InputData(
                List<Segment> segments,
                ClippingWindow window
        ) {
            this.segments = segments;
            this.window = window;
        }
    }

    public static InputData load(File file) throws Exception {
        try (Scanner scanner = new Scanner(file)) {

            int n = scanner.nextInt();

            if (n < 0) {
                throw new IllegalArgumentException(
                        "Количество отрезков не может быть отрицательным"
                );
            }

            List<Segment> segments = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                double x1 = scanner.nextDouble();
                double y1 = scanner.nextDouble();
                double x2 = scanner.nextDouble();
                double y2 = scanner.nextDouble();

                segments.add(new Segment(x1, y1, x2, y2));
            }

            int m = scanner.nextInt();

            if (m < 3) {
                throw new IllegalArgumentException(
                        "У окна отсечения должно быть минимум 3 вершины"
                );
            }

            List<Point> vertices = new ArrayList<>();

            for (int i = 0; i < m; i++) {
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();

                vertices.add(new Point(x, y));
            }

            if (scanner.hasNext()) {
                throw new IllegalArgumentException(
                        "В файле обнаружены лишние данные"
                );
            }

            ClippingWindow window = new ClippingWindow(vertices);

            return new InputData(segments, window);
        }
    }
}