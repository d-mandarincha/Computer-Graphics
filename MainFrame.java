import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        // Подписываемся на изменения самой модели
        model.addListener((m) -> {
            if (isSelfUpdating) return;
            isSelfUpdating = true;

            colorView.updateColor(m.getAwtColor());
            rgbPanel.setValues(m.getRgb());
            cmykPanel.setValues(m.getCmyk());
            hsvPanel.setValues(m.getHsv());

            isSelfUpdating = false;
        });

        // Слушатели панелей ввода
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

        // Кнопка палитры (JColorChooser)
        colorView.getChooseColorBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(
                        MainFrame.this, 
                        "Выберите цвет", 
                        model.getAwtColor()
                );
                if (selectedColor != null) {
                    model.setRgb(new double[]{selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()});
                }
            }
        });
    }
}