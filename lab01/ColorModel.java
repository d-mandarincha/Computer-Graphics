import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ColorModel {

    public interface ColorChangeListener {
        void onColorChanged(ColorModel model);
    }

    private double[] rgb = new double[]{255, 0, 0};
    private double[] cmyk = new double[]{0, 100, 100, 0};
    private double[] hsv = new double[]{0, 100, 100};


    private final List<ColorChangeListener> listeners = new ArrayList<>();

    public void addListener(ColorChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (ColorChangeListener listener : listeners) {
            listener.onColorChanged(this);
        }
    }

    public void setRgb(double[] rgb) {
        this.rgb[0] = Math.max(0, Math.min(255, rgb[0]));
        this.rgb[1] = Math.max(0, Math.min(255, rgb[1]));
        this.rgb[2] = Math.max(0, Math.min(255, rgb[2]));

        this.cmyk = ColorConverter.rgbToCmyk(this.rgb);
        double oldHue = this.hsv[0];
        double[] newHsv = ColorConverter.rgbToHsv(this.rgb);

        if (newHsv[0] == 0 && Math.abs(oldHue - 360.0) < 0.001) {
            newHsv[0] = 360.0;
        }

        this.hsv = newHsv;
        notifyListeners();
    }

    public void setCmyk(double[] cmyk) {
        this.cmyk  = Arrays.copyOf(cmyk, cmyk.length);
        this.rgb = ColorConverter.cmykToRgb(this.cmyk);
        this.hsv = ColorConverter.rgbToHsv(this.rgb);
        notifyListeners();
    }

    public void setHsv(double[] hsv) {
        this.hsv = Arrays.copyOf(hsv, hsv.length);
        this.rgb = ColorConverter.hsvToRgb(this.hsv);
        this.cmyk = ColorConverter.rgbToCmyk(this.rgb);
        notifyListeners();
    }

    public double[] getRgb() { return Arrays.copyOf(rgb, rgb.length); }
    public double[] getCmyk() { return Arrays.copyOf(cmyk, cmyk.length); }
    public double[] getHsv() { return Arrays.copyOf(hsv, hsv.length); }

    public Color getAwtColor() {
        return new Color((int) Math.round(rgb[0]), (int) Math.round(rgb[1]), (int) Math.round(rgb[2]));
    }
}