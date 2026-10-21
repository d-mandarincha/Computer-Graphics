import java.awt.image.BufferedImage;

public final class SmoothingFilters {
    private SmoothingFilters() {}

    public static BufferedImage gaussian(BufferedImage source, int kernelSize) {
        validate(source, kernelSize);
        double[][] kernel = createGaussianKernel(kernelSize);
        return convolve(source, kernel);
    }

    public static BufferedImage mean(BufferedImage source, int kernelSize) {
        validate(source, kernelSize);
        double[][] kernel = new double[kernelSize][kernelSize];
        double value = 1.0 / (kernelSize * kernelSize);
        for (int y = 0; y < kernelSize; y++) {
            for (int x = 0; x < kernelSize; x++) kernel[y][x] = value;
        }
        return convolve(source, kernel);
    }

    public static BufferedImage median(BufferedImage source, int kernelSize) {
        validate(source, kernelSize);
        int width = source.getWidth(), height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int radius = kernelSize / 2;
        int count = kernelSize * kernelSize;
        int[] reds = new int[count], greens = new int[count], blues = new int[count];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int n = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int rgb = source.getRGB(clamp(x + dx, 0, width - 1),
                                                clamp(y + dy, 0, height - 1));
                        reds[n] = (rgb >> 16) & 255;
                        greens[n] = (rgb >> 8) & 255;
                        blues[n] = rgb & 255;
                        n++;
                    }
                }
                java.util.Arrays.sort(reds);
                java.util.Arrays.sort(greens);
                java.util.Arrays.sort(blues);
                result.setRGB(x, y, (reds[count / 2] << 16) |
                        (greens[count / 2] << 8) | blues[count / 2]);
            }
        }
        return result;
    }

    private static double[][] createGaussianKernel(int size) {
        double[][] kernel = new double[size][size];
        int radius = size / 2;
        double sigma = Math.max(0.8, size / 6.0);
        double sum = 0.0;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                double value = Math.exp(-(x * x + y * y) / (2.0 * sigma * sigma));
                kernel[y + radius][x + radius] = value;
                sum += value;
            }
        }
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                kernel[y][x] /= sum;
        return kernel;
    }

    private static BufferedImage convolve(BufferedImage source, double[][] kernel) {
        int width = source.getWidth(), height = source.getHeight();
        int size = kernel.length, radius = size / 2;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = 0, g = 0, b = 0;
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int rgb = source.getRGB(clamp(x + kx, 0, width - 1),
                                                clamp(y + ky, 0, height - 1));
                        double weight = kernel[ky + radius][kx + radius];
                        r += ((rgb >> 16) & 255) * weight;
                        g += ((rgb >> 8) & 255) * weight;
                        b += (rgb & 255) * weight;
                    }
                }
                result.setRGB(x, y, (clamp((int)Math.round(r), 0, 255) << 16) |
                        (clamp((int)Math.round(g), 0, 255) << 8) |
                        clamp((int)Math.round(b), 0, 255));
            }
        }
        return result;
    }

    static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void validate(BufferedImage source, int kernelSize) {
        if (source == null) throw new IllegalArgumentException("Сначала загрузите изображение.");
        if (kernelSize < 3 || kernelSize % 2 == 0)
            throw new IllegalArgumentException("Размер ядра должен быть нечётным числом не меньше 3.");
    }
}
