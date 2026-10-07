package lab01;
public class ColorConverter {

    public static double[] rgbToHsv(double[] rgb) {
        double rN = rgb[0] / 255.0;
        double gN = rgb[1] / 255.0;
        double bN = rgb[2] / 255.0;

        double max = Math.max(rN, Math.max(gN, bN));
        double min = Math.min(rN, Math.min(gN, bN));
        double delta = max - min;

        double h = 0;
        if (delta != 0) {
            if (max == rN) {
                h = 60 * (((gN - bN) / delta) % 6);
            } else if (max == gN) {
                h = 60 * (((bN - rN) / delta) + 2);
            } else {
                h = 60 * (((rN - gN) / delta) + 4);
            }
        }
        if (h < 0) h += 360;

        double s = (max == 0) ? 0 : (delta / max) * 100;
        double v = max * 100;

        return new double[]{h, s, v};
    }

    public static double[] hsvToRgb(double[] hsv) {
        double sN = hsv[1] / 100.0;
        double vN = hsv[2] / 100.0;

        double c = vN * sN;
        double x = c * (1 - Math.abs(((hsv[0] / 60.0) % 2) - 1));
        double m = vN - c;

        double r = 0, g = 0, b = 0;
        if (hsv[0] >= 0 && hsv[0] < 60) { r = c; g = x; b = 0; }
        else if (hsv[0] >= 60 && hsv[0] < 120) { r = x; g = c; b = 0; }
        else if (hsv[0] >= 120 && hsv[0] < 180) { r = 0; g = c; b = x; }
        else if (hsv[0] >= 180 && hsv[0] < 240) { r = 0; g = x; b = c; }
        else if (hsv[0] >= 240 && hsv[0] < 300) { r = x; g = 0; b = c; }
        else if (hsv[0] >= 300 && hsv[0] <= 360) { r = c; g = 0; b = x; }

        return new double[]{(r + m) * 255, (g + m) * 255, (b + m) * 255};
    }

    public static double[] rgbToCmyk(double[] rgb) {
        double rN = rgb[0] / 255.0;
        double gN = rgb[1] / 255.0;
        double bN = rgb[2] / 255.0;

        double k = 1.0 - Math.max(rN, Math.max(gN, bN));
        double c = (k == 1.0) ? 0 : (1.0 - rN - k) / (1.0 - k);
        double m = (k == 1.0) ? 0 : (1.0 - gN - k) / (1.0 - k);
        double y = (k == 1.0) ? 0 : (1.0 - bN - k) / (1.0 - k);

        return new double[]{c * 100, m * 100, y * 100, k * 100};
    }

    public static double[] cmykToRgb(double[] cmyk) {
        double cN = cmyk[0] / 100.0;
        double mN = cmyk[1] / 100.0;
        double yN = cmyk[2] / 100.0;
        double kN = cmyk[3] / 100.0;

        double r = 255 * (1 - cN) * (1 - kN);
        double g = 255 * (1 - mN) * (1 - kN);
        double b = 255 * (1 - yN) * (1 - kN);

        return new double[]{r, g, b};
    }
}