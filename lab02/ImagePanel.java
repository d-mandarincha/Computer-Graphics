import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private BufferedImage result;
    private final String title;

    public ImagePanel(String title) {
        this.title = title;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(title));
        setPreferredSize(new Dimension(320, 280));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.result = image;
        repaint();
    }

    public BufferedImage getImage() {
        return result;
    }

    public void clear() {
        image = null;
        result = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            g.setColor(Color.GRAY);
            String message = "Изображение не выбрано";
            g.drawString(message, Math.max(10, (getWidth() - g.getFontMetrics().stringWidth(message)) / 2),
                    getHeight() / 2);
            return;
        }

        int margin = 12;
        int availableWidth = getWidth() - margin * 2;
        int availableHeight = getHeight() - margin * 2 - 15;
        if (availableWidth <= 0 || availableHeight <= 0) return;

        double scale = Math.min((double) availableWidth / image.getWidth(),
                (double) availableHeight / image.getHeight());
        int w = Math.max(1, (int)(image.getWidth() * scale));
        int h = Math.max(1, (int)(image.getHeight() * scale));
        g.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
    }
}
