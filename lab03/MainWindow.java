
import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.util.List;

public class MainWindow extends JFrame {
    private final DrawingPanel drawingPanel = new DrawingPanel();
    private final ControlPanel controlPanel = new ControlPanel();

    private final RasterAlgorithm stepAlgorithm = new StepByStepAlgorithm();
    private final RasterAlgorithm ddaAlgorithm = new DDAAlgorithm();
    private final RasterAlgorithm bresenhamLine = new BresenhamLineAlgorithm();
    private final RasterAlgorithm bresenhamCircle = new BresenhamCircleAlgorithm();

    public MainWindow() {
        super("Визуализация алгоритмов растеризации");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        controlPanel.setPreferredSize(new Dimension(285, 0));

        controlPanel.drawButton.addActionListener(e -> draw());
        controlPanel.clearButton.addActionListener(e -> clear());

        controlPanel.zoomSlider.addChangeListener(e -> {
            drawingPanel.setCellSize(controlPanel.zoomSlider.getValue());
            updateCoordinateLimits();
        });

        controlPanel.algorithmBox.addActionListener(e -> updateParameterMode());

        drawingPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateCoordinateLimits();
            }
        });

        updateParameterMode();
    }

    private void updateParameterMode() {
        boolean circleMode = controlPanel.algorithmBox.getSelectedIndex() == 3;

        controlPanel.radiusField.setEnabled(circleMode);
        controlPanel.x2Field.setEnabled(!circleMode);
        controlPanel.y2Field.setEnabled(!circleMode);
        controlPanel.x1Field.setEnabled(true);
        controlPanel.y1Field.setEnabled(true);
    }

    // Получение допустимых координат с учётом видимой области.
    private int getMaxX() {
        return drawingPanel.getWidth() / (2 * drawingPanel.getCellSize());
    }

    private int getMaxY() {
        return drawingPanel.getHeight() / (2 * drawingPanel.getCellSize());
    }

    private int getMinX() {
        return -getMaxX();
    }

    private int getMinY() {
        return -getMaxY();
    }

    private void updateCoordinateLimits() {
        if (drawingPanel.getWidth() <= 0 || drawingPanel.getHeight() <= 0) {
            return;
        }

        int minX = getMinX();
        int maxX = getMaxX();
        int minY = getMinY();
        int maxY = getMaxY();

        controlPanel.limitsLabel.setText(
            "<html>Допустимые координаты:<br>"
            + "X: от " + minX + " до " + maxX + "<br>"
            + "Y: от " + minY + " до " + maxY
            + "</html>"
        );
    }

    private void checkPoint(int x, int y, String name) {
        if (x < getMinX() || x > getMaxX()
                || y < getMinY() || y > getMaxY()) {

            throw new IllegalArgumentException(
                name + " (" + x + "; " + y + ") находится за пределами "
                + "видимой области.\n"
                + "Допустимые координаты:\n"
                + "X: от " + getMinX() + " до " + getMaxX() + "\n"
                + "Y: от " + getMinY() + " до " + getMaxY()
            );
        }
    }

    private void draw() {
        try {
            int algorithmIndex = controlPanel.algorithmBox.getSelectedIndex();

            int x1 = parseField(controlPanel.x1Field, "X1 / X центра");
            int y1 = parseField(controlPanel.y1Field, "Y1 / Y центра");

            RasterAlgorithm algorithm;
            List<Point> result;

            if (algorithmIndex == 3) {
                int radius = parseField(controlPanel.radiusField, "Радиус");

                if (radius < 0) {
                    throw new IllegalArgumentException(
                        "Радиус не может быть отрицательным."
                    );
                }

                // Проверяем все крайние точки окружности.
                checkPoint(x1 - radius, y1, "Левая граница окружности");
                checkPoint(x1 + radius, y1, "Правая граница окружности");
                checkPoint(x1, y1 - radius, "Нижняя граница окружности");
                checkPoint(x1, y1 + radius, "Верхняя граница окружности");

                algorithm = bresenhamCircle;

                long startTime = System.nanoTime();
                result = algorithm.drawCircle(x1, y1, radius);
                long elapsed = System.nanoTime() - startTime;

                drawingPanel.setPixels(result);
                showResult(algorithm, result, elapsed);
                return;

            } else {
                int x2 = parseField(controlPanel.x2Field, "X2");
                int y2 = parseField(controlPanel.y2Field, "Y2");

                checkPoint(x1, y1, "Начальная точка");
                checkPoint(x2, y2, "Конечная точка");

                switch (algorithmIndex) {
                    case 0:
                        algorithm = stepAlgorithm;
                        break;
                    case 1:
                        algorithm = ddaAlgorithm;
                        break;
                    case 2:
                        algorithm = bresenhamLine;
                        break;
                    default:
                        throw new IllegalStateException("Неизвестный алгоритм.");
                }

                long startTime = System.nanoTime();
                result = algorithm.drawLine(x1, y1, x2, y2);
                long elapsed = System.nanoTime() - startTime;

                drawingPanel.setPixels(result);
                showResult(algorithm, result, elapsed);
            }

        } catch (NumberFormatException ex) {
            showError("Введите целые числа во все поля координат и радиуса.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Не удалось построить изображение: " + ex.getMessage());
        }
    }

    private void showResult(
        RasterAlgorithm algorithm,
        List<Point> result,
        long elapsed
    ) {
        controlPanel.statusLabel.setText(
            "<html><b>Алгоритм:</b> " + algorithm.getName()
            + "<br><b>Количество пикселей:</b> " + result.size()
            + "<br><b>Время:</b> " + elapsed + " нс"
            + "<br><br><font color='#64748b'>"
            + "Одна клетка соответствует одной целочисленной координате."
            + "</font></html>"
        );
    }

    private int parseField(JTextField field, String fieldName) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(
                "Некорректное значение: " + fieldName
            );
        }
    }

    private void clear() {
        drawingPanel.clear();
        controlPanel.statusLabel.setText(
            "Поле очищено. Выберите алгоритм и задайте параметры."
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Ошибка ввода",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
