import java.awt.image.BufferedImage;
import java.util.Arrays;

// Локальная бинаризация изображения методами Бернсена и Ниблэка 
public final class ThresholdProcessor {
    private ThresholdProcessor() {}

    public enum AdaptiveMethod { BERNSEN, NIBLACK }

    // Локальная бинаризация grayscale: Бернсен или Ниблэк
    public static BufferedImage adaptiveThreshold(BufferedImage source, AdaptiveMethod method,
                                                   int blockSize, int parameter) {
        requireBlockSize(source, blockSize);
        if (method == null) throw new IllegalArgumentException("Выберите метод обработки.");
        return adaptivePlane(intensityPlane(source), method, blockSize, parameter);
    }

    private static BufferedImage adaptivePlane(int[][] plane, AdaptiveMethod method,
                                                int blockSize, int parameter) {
        int h = plane.length, w = plane[0].length, radius = blockSize / 2;
        double[][] thresholds = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (method == AdaptiveMethod.BERNSEN) {
                    int min = 255, max = 0;
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dx = -radius; dx <= radius; dx++) {
                            int value = plane[clamp(y + dy, 0, h - 1)][clamp(x + dx, 0, w - 1)];
                            min = Math.min(min, value);
                            max = Math.max(max, value);
                        }
                    }
                    int minimumContrast = clamp(parameter, 0, 255);
                    int contrast = max - min;
                    // При слабом локальном контрасте применяем фиксированный порог 127.
                    thresholds[y][x] = contrast >= minimumContrast ? (min + max) / 2.0 : 127;
                } else {
                    double sum = 0.0, sumSquares = 0.0;
                    int count = 0;
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dx = -radius; dx <= radius; dx++) {
                            int value = plane[clamp(y + dy, 0, h - 1)][clamp(x + dx, 0, w - 1)];
                            sum += value;
                            sumSquares += (double) value * value;
                            count++;
                        }
                    }
                    double mean = sum / count;
                    double variance = Math.max(0.0, sumSquares / count - mean * mean);
                    double k = parameter / 100.0;
                    thresholds[y][x] = mean + k * Math.sqrt(variance);
                }
            }
        }
        return binaryByThreshold(plane, thresholds);
    }

    public static int[][] intensityPlane(BufferedImage source) {
        requireImage(source);
        int w = source.getWidth(), h = source.getHeight();
        int[][] plane = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = source.getRGB(x, y);
                int red = (rgb >>> 16) & 255;
                int green = (rgb >>> 8) & 255;
                int blue = rgb & 255;
                plane[y][x] = (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
            }
        }
        return plane;
    }

    private static BufferedImage binaryByThreshold(int[][] plane, double[][] thresholds) {
        int h = plane.length, w = plane[0].length;
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int value = plane[y][x] > thresholds[y][x] ? 255 : 0;
                result.setRGB(x, y, (value << 16) | (value << 8) | value);
            }
        }
        return result;
    }

    private static void requireBlockSize(BufferedImage source, int size) {
        requireImage(source);
        if (size < 3 || size % 2 == 0)
            throw new IllegalArgumentException("Размер окна должен быть нечётным числом не меньше 3.");
    }

    private static void requireImage(BufferedImage source) {
        if (source == null) throw new IllegalArgumentException("Сначала загрузите изображение.");
        if (source.getWidth() < 1 || source.getHeight() < 1)
            throw new IllegalArgumentException("Изображение пустое.");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
