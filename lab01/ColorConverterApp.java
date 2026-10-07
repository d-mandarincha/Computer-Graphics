import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ColorConverterApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ColorModel model = new ColorModel();
            MainFrame frame = new MainFrame(model);
            frame.setVisible(true);
        });
    }
}