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

        final JColorChooser chooser = new JColorChooser(model.getAwtColor());

        setJavaChooserHue(chooser, currentHsv[0]);
        setJavaChooserCmyk(chooser, model.getCmyk());

        ActionListener okListener = e -> {
            Color selectedColor = chooser.getColor();
            if (selectedColor == null) {
                return;
            }

            String selectedPanel = "";

            for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
                if (panel.isShowing()) {
                    selectedPanel = panel.getDisplayName();
                    break;
                }
            }

            switch (selectedPanel) {
            case "CMYK":
                model.setCmyk(getJavaChooserCmyk(chooser));
                break;
            case "HSV":
                model.setHsv(getJavaChooserHSV(chooser));
                break;
            default:
                model.setRgb(new double[]{
                    selectedColor.getRed(),
                    selectedColor.getGreen(),
                    selectedColor.getBlue()
                });
                break;
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


private double[] getJavaChooserHSV(JColorChooser chooser){
    for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
        if (!"HSV".equalsIgnoreCase(panel.getDisplayName())) {
            continue;
        }

        List<JSlider> sliders = new ArrayList<>();
        collectSliders(panel, sliders);

        return new double[]{
            sliders.get(0).getValue(),
            sliders.get(1).getValue() * 100.0 / 255,
            sliders.get(2).getValue() * 100.0 / 255,
        };
    }

    return model.getHsv();
}

    
private double[] getJavaChooserCmyk(JColorChooser chooser){
    for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
        if (!"CMYK".equalsIgnoreCase(panel.getDisplayName())) {
            continue;
        }

        List<JSlider> sliders = new ArrayList<>();
        collectSliders(panel, sliders);

        return new double[]{
            sliders.get(0).getValue() * 100.0 / 255,
            sliders.get(1).getValue() * 100.0 / 255,
            sliders.get(2).getValue() * 100.0 / 255,
            sliders.get(3).getValue() * 100.0 / 255
        };
    }

    return model.getCmyk();
}
    
private void setJavaChooserCmyk(JColorChooser chooser, double[] cmyk) {
    for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
        if (!"CMYK".equalsIgnoreCase(panel.getDisplayName())) {
            continue;
        }

        List<JSlider> sliders = new ArrayList<>();
        collectSliders(panel, sliders);
        for (int i = 0; i < 4; i++) {
            int value = (int) Math.round(cmyk[i] * 255 / 100.0);
            sliders.get(i).setValue(value);
        }

        return;
    }

}

    private void setJavaChooserHue(JColorChooser chooser, double hue) {
        for (AbstractColorChooserPanel panel : chooser.getChooserPanels()) {
            if (!"HSV".equalsIgnoreCase(panel.getDisplayName())) {
                continue;
            }

            List<JSlider> sliders = new ArrayList<>();
            collectSliders(panel, sliders);
            sliders.get(0).setValue(hue >= 359 ? 360 : 0);         
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
