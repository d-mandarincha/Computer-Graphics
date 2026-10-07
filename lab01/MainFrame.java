import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final ColorModel model;
    private boolean isSelfUpdating = false;

    private ColorView colorView;
    private ModelPanel rgbPanel;
    private ModelPanel cmykPanel;
    private ModelPanel hsvPanel;

    public MainFrame(ColorModel model) {
        this.model = model;

        setTitle("Color Converter App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        setupListeners();

        model.setRgb(new double[]{255, 0, 0});

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        colorView = new ColorView();

        rgbPanel = new ModelPanel("RGB (0..255)",
                new String[]{"R:", "G:", "B:"},
                new double[]{0, 0, 0},
                new double[]{255, 255, 255});

        cmykPanel = new ModelPanel("CMYK (0..100%)",
                new String[]{"C:", "M:", "Y:", "K:"},
                new double[]{0, 0, 0, 0},
                new double[]{100, 100, 100, 100});

        hsvPanel = new ModelPanel("HSV (0..360°, 0..100%)",
                new String[]{"H:", "S:", "V:"},
                new double[]{0, 0, 0},
                new double[]{360, 100, 100});

        JPanel modelsContainer = new JPanel(new GridLayout(1, 3, 10, 0));
        modelsContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        modelsContainer.add(rgbPanel);
        modelsContainer.add(cmykPanel);
        modelsContainer.add(hsvPanel);

        add(colorView, BorderLayout.NORTH);
        add(modelsContainer, BorderLayout.CENTER);
    }

    private void setupListeners() {
        model.addListener((m) -> {
            if (isSelfUpdating) return;
            isSelfUpdating = true;

            colorView.updateColor(m.getAwtColor());
            rgbPanel.setValues(m.getRgb());
            cmykPanel.setValues(m.getCmyk());
            hsvPanel.setValues(m.getHsv());

            isSelfUpdating = false;
        });

        rgbPanel.setOnValuesChanged(vals -> {
            if (isSelfUpdating) return;
            model.setRgb(vals);
        });

        cmykPanel.setOnValuesChanged(vals -> {
            if (isSelfUpdating) return;
            model.setCmyk(vals);
        });

        hsvPanel.setOnValuesChanged(vals -> {
            if (isSelfUpdating) return;
            model.setHsv(vals);
        });

        colorView.getChooseColorBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openJavaColorChooser();
            }
        });
    }

    private void openJavaColorChooser() {
        final double[] currentHsv = model.getHsv();

        // Остаётся именно стандартный Swing JColorChooser.
        final JColorChooser chooser = new JColorChooser(model.getAwtColor());

        // Стандартная Java HSV-панель сначала получает H=0 для чистого красного.
        // Если в нашей модели был H=360, возвращаем 360 штатному HSV-слайдеру.
        setJavaChooserHue(chooser, currentHsv[0]);

        ActionListener okListener = e -> {
            Color selectedColor = chooser.getColor();
            if (selectedColor != null) {
                model.setRgb(new double[]{
                        selectedColor.getRed(),
                        selectedColor.getGreen(),
                        selectedColor.getBlue()
                });
            }
        };

        JDialog dialog = JColorChooser.createDialog(
                this,
                "Выберите цвет",
                true,
                chooser,
                okListener,
                null
        );

        dialog.setVisible(true);
    }

    private void setJavaChooserHue(JColorChooser chooser, double hue) {
        if (Math.abs(hue - 360.0) > 0.001 && Math.abs(hue) > 0.001) {
            return;
        }

        for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
            if (!"HSV".equalsIgnoreCase(panel.getDisplayName())) {
                continue;
            }

            List<JSlider> sliders = new ArrayList<>();
            collectSliders(panel, sliders);

            
            if (!sliders.isEmpty()
                    && sliders.get(0).getMinimum() == 0
                    && sliders.get(0).getMaximum() == 360) {
                sliders.get(0).setValue(hue >= 359.999 ? 360 : 0);
            }
            return;
        }
    }

    private void collectSliders(Component component, List<JSlider> result) {
        if (component instanceof JSlider) {
            result.add((JSlider) component);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                collectSliders(child, result);
            }
        }
    }
}
