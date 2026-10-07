package lab01;
import javax.swing.*;
import java.awt.*;

public class ColorView extends JPanel {

    private final JPanel previewBox;
    private final JButton chooseColorBtn;
    private final JTextField colorHexField;

    public ColorView() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        previewBox = new JPanel();
        previewBox.setPreferredSize(new Dimension(120, 60));
        previewBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));

        chooseColorBtn = new JButton("Палитра");

        colorHexField = new JTextField(10);
        colorHexField.setEditable(false);
        colorHexField.setHorizontalAlignment(JTextField.CENTER);

        add(colorHexField);
        add(previewBox);
        add(chooseColorBtn);
    }

    public JButton getChooseColorBtn() {
        return chooseColorBtn;
    }

    public void updateColor(Color color) {
        previewBox.setBackground(color);
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        colorHexField.setText(hex);
        previewBox.repaint();
    }
}