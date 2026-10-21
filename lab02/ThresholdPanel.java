import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ThresholdPanel extends JPanel {
    private final ImagePanel sourceView = new ImagePanel("Исходное изображение");
    private final ImagePanel adaptiveView = new ImagePanel("Локальная бинаризация");
    private final JSpinner blockSize = new JSpinner(new SpinnerNumberModel(31, 3, 151, 2));
    private final JSpinner parameter = new JSpinner(new SpinnerNumberModel(-20, -100, 0, 5));
    private final JLabel parameterLabel = new JLabel("k · 100:");
    private final JComboBox<String> method = new JComboBox<>(new String[]{"Бернсен", "Ниблэк"});
    private BufferedImage source;

    public ThresholdPanel() {
        super(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton compare = new JButton("Выполнить обработку");
        controls.add(new JLabel("Размер локального окна:"));
        controls.add(blockSize);
        controls.add(new JLabel("Локальный метод:"));
        controls.add(method);
        controls.add(parameterLabel);
        controls.add(parameter);
        controls.add(compare);

        JPanel images = new JPanel(new GridLayout(1, 2, 8, 8));
        images.add(sourceView);
        images.add(adaptiveView);

        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        method.addActionListener(e -> updateParameterControl());
        compare.addActionListener(e -> processImage());
        updateParameterControl();
    }

    private void updateParameterControl() {
        boolean bernsen = method.getSelectedIndex() == 0;
        parameterLabel.setText(bernsen ? "Мин. контраст:" : "k · 100:");
        parameter.setModel(bernsen
                ? new SpinnerNumberModel(15, 0, 255, 1)
                : new SpinnerNumberModel(-20, -100, 0, 5));
    }

    public void setSource(BufferedImage image) {
        source = image;
        sourceView.setImage(image);
        adaptiveView.clear();
    }

    public BufferedImage getPreferredResult() {
        return adaptiveView.getImage();
    }

    private void processImage() {
        if (source == null) {
            JOptionPane.showMessageDialog(this, "Сначала откройте изображение.");
            return;
        }
        ThresholdProcessor.AdaptiveMethod selected = method.getSelectedIndex() == 0
                ? ThresholdProcessor.AdaptiveMethod.BERNSEN
                : ThresholdProcessor.AdaptiveMethod.NIBLACK;
        BufferedImage adaptive = ThresholdProcessor.adaptiveThreshold(
                source, selected, (Integer) blockSize.getValue(), (Integer) parameter.getValue());
        adaptiveView.setImage(adaptive);
    }
}
