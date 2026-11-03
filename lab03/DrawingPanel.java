
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JPanel {
    private List<Point> pixels = new ArrayList<>();
    private int cellSize = 30;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 650));
        setMinimumSize(new Dimension(400, 350));
    }

    public void setPixels(List<Point> pixels) {
        this.pixels = new ArrayList<>(pixels);
        repaint();
    }

    public void clear() {
        pixels.clear();
        repaint();
    }

    public void setCellSize(int cellSize) {
        this.cellSize = Math.max(10, Math.min(60, cellSize));
        repaint();
    }

    public int getCellSize() {
        return cellSize;
    }

    // Проверяет, находится ли координата в видимой области.
    public boolean isCoordinateVisible(int x, int y) {
        int originX = getWidth() / 2;
        int originY = getHeight() / 2;

        int screenX = originX + x * cellSize;
        int screenY = originY - y * cellSize;

        return screenX >= 0
                && screenX + cellSize <= getWidth()
                && screenY - cellSize >= 0
                && screenY <= getHeight();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF
        );

        int width = getWidth();
        int height = getHeight();

        int originX = width / 2;
        int originY = height / 2;

        drawGrid(g, width, height, originX, originY);
        drawPixels(g, originX, originY);
        drawAxes(g, width, height, originX, originY);

        g.dispose();
    }

    private void drawGrid(
        Graphics2D g,
        int width,
        int height,
        int originX,
        int originY
    ) {
        g.setColor(new Color(232, 236, 241));
        g.setStroke(new BasicStroke(1));

        int firstX = Math.floorMod(originX, cellSize);
        int firstY = Math.floorMod(originY, cellSize);

        for (int x = firstX; x < width; x += cellSize) {
            g.drawLine(x, 0, x, height);
        }

        for (int y = firstY; y < height; y += cellSize) {
            g.drawLine(0, y, width, y);
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(105, 115, 125));

        int labelStep = cellSize >= 22 ? 1 : 5;

        int minX = (int) Math.ceil(-originX / (double) cellSize);
        int maxX = (int) Math.floor((width - originX) / (double) cellSize);

        for (int x = minX; x <= maxX; x++) {
            if (x != 0 && x % labelStep == 0) {
                int sx = originX + x * cellSize;
                g.drawString(
                    Integer.toString(x),
                    sx + 3,
                    originY + 15
                );
            }
        }

        int minY = (int) Math.ceil((originY - height) / (double) cellSize);
        int maxY = (int) Math.floor(originY / (double) cellSize);

        for (int y = minY; y <= maxY; y++) {
            if (y != 0 && y % labelStep == 0) {
                int sy = originY - y * cellSize;
                g.drawString(
                    Integer.toString(y),
                    originX + 5,
                    sy - 3
                );
            }
        }

        g.drawString("0", originX + 5, originY + 15);
    }

    private void drawPixels(Graphics2D g, int originX, int originY) {
        g.setColor(new Color(48, 132, 220));

        for (Point point : pixels) {
            // Левая верхняя граница клетки.
            int screenX = originX + point.x * cellSize;
            int screenY = originY - (point.y + 1) * cellSize;

            // Закрашиваем клетку целиком.
            g.fillRect(
                screenX + 1,
                screenY + 1,
                cellSize - 1,
                cellSize - 1
            );
        }
    }

    private void drawAxes(
        Graphics2D g,
        int width,
        int height,
        int originX,
        int originY
    ) {
        g.setColor(new Color(55, 65, 80));
        g.setStroke(new BasicStroke(2f));

        g.drawLine(0, originY, width, originY);
        g.drawLine(originX, height, originX, 0);

        // Стрелки осей.
        g.drawLine(width - 1, originY, width - 10, originY - 5);
        g.drawLine(width - 1, originY, width - 10, originY + 5);

        g.drawLine(originX, 1, originX - 5, 10);
        g.drawLine(originX, 1, originX + 5, 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        g.drawString("X", width - 20, originY - 10);
        g.drawString("Y", originX + 10, 18);
    }
}
