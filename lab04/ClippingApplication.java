
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClippingApplication extends JFrame {

    private final DrawingPanel drawingPanel;

    private final List<Segment> segments = new ArrayList<>();
    private final List<Segment> result = new ArrayList<>();

    private ClippingWindow window;

    private final JComboBox<String> algorithmBox;

    public ClippingApplication() {
        setTitle("Отсечение отрезков выпуклым многоугольником");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        drawingPanel = new DrawingPanel();

        // Выбор алгоритма
        algorithmBox = new JComboBox<>(new String[]{
                "Метод средней точки",
                "Алгоритм Цируса — Бека"
        });

        // Кнопки управления
        JButton loadButton = new JButton("Загрузить файл");
        JButton executeButton = new JButton("Выполнить отсечение");
        JButton clearButton = new JButton("Очистить");

        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(loadButton);
        controlPanel.add(new JLabel("Алгоритм:"));
        controlPanel.add(algorithmBox);
        controlPanel.add(executeButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);

        // Обработчики кнопок
        loadButton.addActionListener(e -> loadFile());
        executeButton.addActionListener(e -> executeClipping());
        clearButton.addActionListener(e -> clearData());
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser(
                new File(System.getProperty("user.dir"))
        );

        chooser.setDialogTitle("Выберите файл с данными");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Текстовые файлы (*.txt)", "txt"
                )
        );

        int option = chooser.showOpenDialog(this);

        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = chooser.getSelectedFile();

            FileLoader.InputData data = FileLoader.load(file);

            segments.clear();
            result.clear();

            segments.addAll(data.segments);
            window = data.window;

            drawingPanel.setData(segments, result, window);

            JOptionPane.showMessageDialog(
                    this,
                    "Файл успешно загружен!\n"
                            + "Количество отрезков: " + segments.size()
                            + "\nКоличество вершин окна: "
                            + window.getVertices().size(),
                    "Загрузка завершена",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ошибка загрузки файла:\n" + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void executeClipping() {

        if (window == null || segments.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Сначала загрузите файл с данными!",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        result.clear();

        List<Point> polygon = window.getVertices();

        ClippingAlgorithm algorithm;

        int selected = algorithmBox.getSelectedIndex();

        if (selected == 0) {
            algorithm = new MidpointClipping(
                    drawingPanel.getScale()
            );
        } else {
            algorithm = new CyrusBeckClipping();
        }

        for (Segment segment : segments) {
            result.addAll(
                    algorithm.clip(segment, polygon)
            );
        }

        drawingPanel.setData(segments, result, window);

        JOptionPane.showMessageDialog(
                this,
                "Отсечение выполнено!\n"
                        + "Алгоритм: " + algorithm.getName()
                        + "\nИсходных отрезков: " + segments.size()
                        + "\nПолучено частей: " + result.size(),
                "Результат",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void clearData() {
        segments.clear();
        result.clear();
        window = null;

        drawingPanel.setData(segments, result, null);

        repaint();
    }
}
