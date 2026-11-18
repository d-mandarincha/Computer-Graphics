
import java.util.ArrayList;
import java.util.List;

public class CyrusBeckClipping implements ClippingAlgorithm {

    private static final double EPS = 1e-10;

    @Override
    public String getName() {
        return "Алгоритм Цируса — Бека";
    }

    @Override
    public List<Segment> clip(
            Segment s,
            List<Point> polygon) {

        List<Segment> result = new ArrayList<>();

        if (polygon == null || polygon.size() < 3) {
            return result;
        }

        double dx = s.p2.x - s.p1.x;
        double dy = s.p2.y - s.p1.y;

        // Начальные значения параметров отсечения
        double tEnter = 0.0;
        double tLeave = 1.0;

        // Определяем направление обхода многоугольника
        double area = 0;

        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());

            area += a.x * b.y - b.x * a.y;
        }

        if (Math.abs(area) < EPS) {
            return result;
        }

        double orientation = area > 0 ? 1 : -1;

        // Обрабатываем каждую сторону многоугольника
        for (int i = 0; i < polygon.size(); i++) {

            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());

            double ex = b.x - a.x;
            double ey = b.y - a.y;

            // Вычисляем внутреннюю нормаль
            double nx = orientation * -ey;
            double ny = orientation * ex;

            double numerator =
                    nx * (a.x - s.p1.x) +
                    ny * (a.y - s.p1.y);

            double denominator =
                    nx * dx + ny * dy;

            // Проверяем параллельность отрезка стороне
            if (Math.abs(denominator) < EPS) {

                // Отрезок параллелен границе и находится снаружи
                if (numerator > EPS) {
                    return result;
                }

                continue;
            }

            double t = numerator / denominator;

            // Определяем вход и выход из окна
            if (denominator > 0) {
                tEnter = Math.max(tEnter, t);
            } else {
                tLeave = Math.min(tLeave, t);
            }

            // Проверяем, существует ли видимая часть
            if (tEnter > tLeave + EPS) {
                return result;
            }
        }

        // Проверяем границы исходного отрезка
        if (tEnter > 1 + EPS || tLeave < -EPS) {
            return result;
        }

        tEnter = Math.max(0, tEnter);
        tLeave = Math.min(1, tLeave);

        if (tEnter > tLeave + EPS) {
            return result;
        }

        // Вычисляем координаты концов видимой части
        Point p1 = new Point(
                s.p1.x + tEnter * dx,
                s.p1.y + tEnter * dy
        );

        Point p2 = new Point(
                s.p1.x + tLeave * dx,
                s.p1.y + tLeave * dy
        );

        // Не добавляем отрезки нулевой длины
        double clippedDx = p2.x - p1.x;
        double clippedDy = p2.y - p1.y;

        if (clippedDx * clippedDx + clippedDy * clippedDy
                > EPS * EPS) {

            result.add(new Segment(p1, p2));
        }

        return result;
    }
}
