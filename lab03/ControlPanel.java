
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JPanel {
    public final JComboBox<String> algorithmBox;

    public final JTextField x1Field = new JTextField("2", 5);
    public final JTextField y1Field = new JTextField("2", 5);
    public final JTextField x2Field = new JTextField("9", 5);
    public final JTextField y2Field = new JTextField("5", 5);
    public final JTextField radiusField = new JTextField("6", 5);

    public final JButton drawButton = new JButton("Построить");
    public final JButton clearButton = new JButton("Очистить");

    public final JSlider zoomSlider = new JSlider(10, 60, 30);

    public final JLabel statusLabel =
        new JLabel("Выберите алгоритм и задайте параметры.");

    public final JLabel limitsLabel =
        new JLabel("Допустимые координаты: вычисляются...");

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(new Color(246, 248, 251));

        algorithmBox = new JComboBox<>(new String[] {
            "Пошаговый алгоритм",
            "Алгоритм ЦДА (DDA)",
            "Брезенхем (отрезок)",
            "Брезенхем (окружность)"
        });

        add(sectionTitle("Алгоритм"));
        add(algorithmBox);
        add(Box.createVerticalStrut(18));

        add(sectionTitle("Параметры отрезка"));

        JPanel linePanel = new JPanel(new GridLayout(2, 4, 6, 5));
        linePanel.setOpaque(false);

        linePanel.add(new JLabel("X1"));
        linePanel.add(new JLabel("Y1"));
        linePanel.add(new JLabel("X2"));
        linePanel.add(new JLabel("Y2"));

        linePanel.add(x1Field);
        linePanel.add(y1Field);
        linePanel.add(x2Field);
        linePanel.add(y2Field);

        add(linePanel);
        add(Box.createVerticalStrut(12));

        add(sectionTitle("Параметры окружности"));

        JPanel circlePanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        circlePanel.setOpaque(false);
        circlePanel.add(new JLabel("Радиус: "));
        circlePanel.add(radiusField);

        add(circlePanel);
        add(Box.createVerticalStrut(15));

        add(sectionTitle("Границы координат"));

        limitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(limitsLabel);
        add(Box.createVerticalStrut(18));

        drawButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(drawButton);
        add(Box.createVerticalStrut(8));
        add(clearButton);
        add(Box.createVerticalStrut(22));

        add(sectionTitle("Масштаб сетки"));

        zoomSlider.setOpaque(false);
        zoomSlider.setMajorTickSpacing(10);
        zoomSlider.setMinorTickSpacing(5);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);

        add(zoomSlider);
        add(Box.createVerticalStrut(18));

        add(sectionTitle("Результат"));

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setVerticalAlignment(SwingConstants.TOP);

        add(statusLabel);
        add(Box.createVerticalGlue());
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }
}
