package lab01;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class ModelPanel extends JPanel {

    private final JSlider[] sliders;
    private final JTextField[] fields;
    private boolean isUpdating = false;
    private Consumer<double[]> onValuesChanged;

    public ModelPanel(String title, String[] labels, double[] minVals, double[] maxVals) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)
        ));

        int n = labels.length;
        sliders = new JSlider[n];
        fields = new JTextField[n];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < n; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            add(new JLabel(labels[i]), gbc);

            sliders[i] = new JSlider((int) minVals[i], (int) maxVals[i], (int) minVals[i]);
            gbc.gridx = 1; gbc.weightx = 1.0;
            add(sliders[i], gbc);

            fields[i] = new JTextField(5);
            fields[i].setHorizontalAlignment(JTextField.RIGHT);
            gbc.gridx = 2; gbc.weightx = 0;
            add(fields[i], gbc);

            final int index = i;

            
            sliders[i].addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (isUpdating) return;
                    fields[index].setText(String.format("%.1f", (double) sliders[index].getValue()).replace(',', '.'));
                    triggerUpdate();
                }
            });

            // Текстовое поле -> Обновление
            fields[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isUpdating) return;
                    try {
                        double val = Double.parseDouble(fields[index].getText().replace(',', '.'));
                        val = Math.max(minVals[index], Math.min(maxVals[index], val));
                        sliders[index].setValue((int) Math.round(val));
                        triggerUpdate();
                    } catch (NumberFormatException ex) {
                        fields[index].setText(String.format("%.1f", (double) sliders[index].getValue()).replace(',', '.'));
                    }
                }
            });
        }
    }

    public void setOnValuesChanged(Consumer<double[]> callback) {
        this.onValuesChanged = callback;
    }

    private void triggerUpdate() {
        if (onValuesChanged == null) return;
        double[] values = new double[sliders.length];
        for (int i = 0; i < sliders.length; i++) {
            values[i] = sliders[i].getValue();
        }
        onValuesChanged.accept(values);
    }

    public void setValues(double[] values) {
        isUpdating = true;
        for (int i = 0; i < values.length; i++) {
            sliders[i].setValue((int) Math.round(values[i]));
            fields[i].setText(String.format("%.1f", values[i]).replace(',', '.'));
        }
        isUpdating = false;
    }
}