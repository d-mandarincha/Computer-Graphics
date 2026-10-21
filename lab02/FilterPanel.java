import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FilterPanel extends JPanel {
    private final ImagePanel sourceView = new ImagePanel("Исходное изображение");
    private final ImagePanel resultView = new ImagePanel("Результат фильтрации");
    private final JComboBox<String> method = new JComboBox<>(
            new String[]{"Гауссов фильтр", "Медианный фильтр", "Размытие по среднему"});
    private final JSpinner kernel = new JSpinner(new SpinnerNumberModel(5, 3, 31, 2));
    private BufferedImage source;

    public FilterPanel() {
        super(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton apply = new JButton("Применить фильтр");
        controls.add(new JLabel("Метод:"));
        controls.add(method);
        controls.add(new JLabel("Размер ядра:"));
        controls.add(kernel);
        controls.add(apply);

        JPanel images = new JPanel(new GridLayout(1, 2, 8, 8));
        images.add(sourceView);
        images.add(resultView);
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        apply.addActionListener(e -> applyFilter());
    }

    public void setSource(BufferedImage image) {
        source = image;
        sourceView.setImage(image);
        resultView.clear();
    }

    public BufferedImage getResult() {
        return resultView.getImage();
    }

    private void applyFilter() {
        if (source == null) {
            JOptionPane.showMessageDialog(this, "Сначала откройте изображение.");
            return;
        }
        int size = (Integer) kernel.getValue();
        BufferedImage result = switch (method.getSelectedIndex()) {
            case 0 -> SmoothingFilters.gaussian(source, size);
            case 1 -> SmoothingFilters.median(source, size);
            default -> SmoothingFilters.mean(source, size);
        };
        resultView.setImage(result);
    }
}
