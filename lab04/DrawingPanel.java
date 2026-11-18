
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {

    private List<Segment> segments = new ArrayList<>();
    private List<Segment> result = new ArrayList<>();
    private ClippingWindow window;

    private double scale = 35;

    public DrawingPanel() {
        setBackground(Color.WHITE);
    }

    public void setData(
            List<Segment> segments,
            List<Segment> result,
            ClippingWindow window
    ) {
        this.segments = segments;
        this.result = result;
        this.window = window;
        repaint();
    }

    public double getScale() {
        return scale;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Центр координатной системы
        int originX = getWidth() / 2;
        int originY = getHeight() / 2;

        // Масштаб по умолчанию
        scale = 35;

        // Если есть данные, подбираем масштаб
        if (window != null) {
            double minX = window.getMinX();
            double maxX = window.getMaxX();
            double minY = window.getMinY();
            double maxY = window.getMaxY();

            for (Segment s : segments) {
                minX = Math.min(minX, Math.min(s.p1.x, s.p2.x));
                maxX = Math.max(maxX, Math.max(s.p1.x, s.p2.x));
                minY = Math.min(minY, Math.min(s.p1.y, s.p2.y));
                maxY = Math.max(maxY, Math.max(s.p1.y, s.p2.y));
            }

            double rangeX = Math.max(1, maxX - minX);
            double rangeY = Math.max(1, maxY - minY);

            double padding = 50;

            scale = Math.min(
                    (getWidth() - 2 * padding) / rangeX,
                    (getHeight() - 2 * padding) / rangeY
            );

            double centerX = (minX + maxX) / 2;
            double centerY = (minY + maxY) / 2;

            originX = (int) (getWidth() / 2.0 - centerX * scale);
            originY = (int) (getHeight() / 2.0 + centerY * scale);
        }

        // Рисуем сетку
        g2.setColor(new Color(230, 230, 230));

        int gridStep = 30;

        for (int x = originX % gridStep; x < getWidth(); x += gridStep) {
            g2.drawLine(x, 0, x, getHeight());
        }

        for (int y = originY % gridStep; y < getHeight(); y += gridStep) {
            g2.drawLine(0, y, getWidth(), y);
        }

        // Координатные оси
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));

        g2.drawLine(0, originY, getWidth(), originY);
        g2.drawLine(originX, 0, originX, getHeight());

        // Стрелки осей
        g2.drawLine(getWidth() - 12, originY - 6,
                getWidth(), originY);
        g2.drawLine(getWidth() - 12, originY + 6,
                getWidth(), originY);

        g2.drawLine(originX - 6, 12, originX, 0);
        g2.drawLine(originX + 6, 12, originX, 0);

        // Подписи осей
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("X", getWidth() - 20, originY - 10);
        g2.drawString("Y", originX + 10, 18);
        g2.drawString("0", originX + 7, originY + 15);

        // Деления и подписи координат
        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        for (int x = originX + gridStep; x < getWidth(); x += gridStep) {
            g2.drawString(
                    String.valueOf(Math.round((x - originX) / scale)),
                    x - 5,
                    originY + 15
            );
        }

        for (int x = originX - gridStep; x > 0; x -= gridStep) {
            g2.drawString(
                    String.valueOf(Math.round((x - originX) / scale)),
                    x - 5,
                    originY + 15
            );
        }

        for (int y = originY - gridStep; y > 0; y -= gridStep) {
            g2.drawString(
                    String.valueOf(Math.round((originY - y) / scale)),
                    originX + 7,
                    y + 4
            );
        }

        for (int y = originY + gridStep; y < getHeight(); y += gridStep) {
            g2.drawString(
                    String.valueOf(Math.round((originY - y) / scale)),
                    originX + 7,
                    y + 4
            );
        }

        // Если данные не загружены, оставляем только координатную плоскость
        if (window == null) {
            g2.dispose();
            return;
        }

        // Исходные отрезки
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));

        for (Segment s : segments) {
            drawSegment(g2, s, originX, originY);
        }

        // Окно отсечения
        List<Point> vertices = window.getVertices();

        int[] xPoints = new int[vertices.size()];
        int[] yPoints = new int[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            Point p = vertices.get(i);

            xPoints[i] = (int) Math.round(originX + p.x * scale);
            yPoints[i] = (int) Math.round(originY - p.y * scale);
        }

        g2.setColor(new Color(255, 0, 0, 35));
        g2.fillPolygon(xPoints, yPoints, vertices.size());

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(xPoints, yPoints, vertices.size());

        // Вершины многоугольника
        for (int i = 0; i < vertices.size(); i++) {
            g2.fillOval(
                    xPoints[i] - 4,
                    yPoints[i] - 4,
                    8,
                    8
            );
        }

        // Результат отсечения
        g2.setColor(new Color(0, 150, 0));
        g2.setStroke(new BasicStroke(3));

        for (Segment s : result) {
            drawSegment(g2, s, originX, originY);
        }

        g2.dispose();
    }

    private void drawSegment(
            Graphics2D g2,
            Segment s,
            int originX,
            int originY
    ) {
        int x1 = (int) Math.round(originX + s.p1.x * scale);
        int y1 = (int) Math.round(originY - s.p1.y * scale);

        int x2 = (int) Math.round(originX + s.p2.x * scale);
        int y2 = (int) Math.round(originY - s.p2.y * scale);

        g2.drawLine(x1, y1, x2, y2);
    }
}
