import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MainFrame extends JFrame {
    private final FilterPanel filterPanel = new FilterPanel();
    private final ThresholdPanel thresholdPanel = new ThresholdPanel();
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel status = new JLabel("Откройте изображение, чтобы начать.");

    public MainFrame() {
        super("Обработка изображений — Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1250, 800);
        setLocationRelativeTo(null);

        JButton openButton = new JButton("Открыть изображение…");
        JButton saveButton = new JButton("Сохранить результат…");
        openButton.addActionListener(e -> openImage());
        saveButton.addActionListener(e -> saveResult());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.add(openButton);
        toolbar.add(saveButton);

        tabs.addTab("1. Сглаживающие фильтры", filterPanel);
        tabs.addTab("2. Локальная пороговая обработка", thresholdPanel);

        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        add(toolbar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите изображение");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                JOptionPane.showMessageDialog(this, "Формат изображения не поддерживается.",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            filterPanel.setSource(image);
            thresholdPanel.setSource(image);
            status.setText("Загружено: " + file.getName() + " (" +
                    image.getWidth() + " × " + image.getHeight() + ").");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не удалось прочитать файл: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveResult() {
        BufferedImage result = switch (tabs.getSelectedIndex()) {
            case 0 -> filterPanel.getResult();
            default -> thresholdPanel.getPreferredResult();
        };

        if (result == null) {
            JOptionPane.showMessageDialog(this,
                    "На выбранной вкладке сначала выполните обработку.",
                    "Нет результата", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить результат");
        chooser.setSelectedFile(new File("result.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        String name = file.getName().toLowerCase(Locale.ROOT);
        String format = name.endsWith(".jpg") || name.endsWith(".jpeg") ? "jpg"
                : name.endsWith(".bmp") ? "bmp" : "png";
        if (!name.matches(".*\\.(png|jpe?g|bmp)$")) {
            file = new File(file.getAbsolutePath() + ".png");
            format = "png";
        }

        try {
            boolean ok = ImageIO.write(result, format, file);
            if (ok) status.setText("Результат сохранён: " + file.getAbsolutePath());
            else JOptionPane.showMessageDialog(this, "Нет подходящего кодировщика формата " + format);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не удалось сохранить файл: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
