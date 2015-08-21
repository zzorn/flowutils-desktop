package org.flowutils.desktop.rawimage;

import org.flowutils.Check;
import org.flowutils.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;


/**
 * Fast, low-level image, backed by a raw array of color data.
 */
public final class RawImage {

    private final int width;
    private final int height;

    private Image image = null;
    private int[] imageData = null;

    /**
     * Creates a new empty black RawImage with the specified size in pixels.
     */
    public RawImage(int width, int height) {
        Check.positive(width, "width");
        Check.positive(height, "height");

        this.width = width;
        this.height = height;

        initialize();
    }

    /**
     * @return width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return height of the image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the raw data of the image, ordered by row, where each element is a 32 bit color value with
     * 8 bit components, in the same order as used by Color.
     * Can be edited.
     */
    public int[] getBuffer() {
        return imageData;
    }

    /**
     * @return the color RGBA value at the specified pixel.  Throws exception if out of range.
     */
    public int getPixel(int x, int y) {
        if (x < 0 || x >= width ||
            y < 0 || y >= height) throw new IllegalArgumentException("The coordinate ("+x+", "+y+") is out of bounds, the image width is "+width+", and height is "+height+".");

        return imageData[x + y*width];
    }

    /**
     * @return the red component of the color value at the specified pixel, as a value between 0 and 1.  Throws exception if out of range.
     */
    public double getRed(int x, int y) {
        return getColorComponent(getPixel(x, y), 0);
    }

    /**
     * @return the green component of the color value at the specified pixel, as a value between 0 and 1.  Throws exception if out of range.
     */
    public double getGreen(int x, int y) {
        return getColorComponent(getPixel(x, y), 1);
    }

    /**
     * @return the blue component of the color value at the specified pixel, as a value between 0 and 1.  Throws exception if out of range.
     */
    public double getBlue(int x, int y) {
        return getColorComponent(getPixel(x, y), 2);
    }

    /**
     * @return the alpha component of the color value at the specified pixel, as a value between 0 and 1.  Throws exception if out of range.
     */
    public double getAlpha(int x, int y) {
        return getColorComponent(getPixel(x, y), 3);
    }

    /**
     * Sets the color RGBA value at the specified pixel.  Throws exception if coordinates are out of range.
     */
    public void setPixel(int x, int y, int colorCode) {
        if (x < 0 || x >= width ||
            y < 0 || y >= height) throw new IllegalArgumentException("The coordinate ("+x+", "+y+") is out of bounds, the image width is "+width+", and height is "+height+".");

        imageData[x + y*width] = colorCode;
    }

    /**
     * Sets the color value at the specified pixel.  Throws exception if coordinates are out of range.
     */
    public void setPixel(int x, int y, double red, double green, double blue) {

        int colorCode = colorComponent(red, 0) |
                        colorComponent(green, 1) |
                        colorComponent(blue, 2) |
                        colorComponent(1.0, 3);

        setPixel(x, y, colorCode);
    }

    /**
     * Sets the color value at the specified pixel.  Throws exception if coordinates are out of range.
     */
    public void setPixel(int x, int y, double red, double green, double blue, double alpha) {

        int colorCode = colorComponent(red, 0) |
                        colorComponent(green, 1) |
                        colorComponent(blue, 2) |
                        colorComponent(alpha, 3);

        setPixel(x, y, colorCode);
    }

    private int colorComponent(double c, int pos) {
        int value = (int) (c * 256);
        if (value < 0) value = 0;
        if (value > 255) value = 255;

        return value << (8 * pos);
    }

    private double getColorComponent(int colorCode, int pos) {
        colorCode = colorCode >>> (8 * pos);
        colorCode &= 0xFF;

        return colorCode / 255.0;
    }


    /**
     * Ensures the latest changes to the image data buffer are updated into the image.
     */
    public void flush() {
        getImage().flush();
    }

    /**
     * Clears the whole image to solid black.
     */
    public void clear() {
        clearToColor(Color.BLACK);
    }

    /**
     * Clears the whole image to the specified color.
     */
    public void clearToColor(Color color) {
        Check.notNull(color, "color");

        clearToColor(color.getRGB());
    }

    /**
     * Clears the whole image to the specified color, indicated by a 32 color code with a component for each color channel.
     */
    public void clearToColor(int colorCode) {
        Arrays.fill(imageData, colorCode);
    }

    /**
     * @return image representing the underlying raw data.
     * Use flush if needed to ensure the image represents the last version of the buffered raw data.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Renders the image to a graphics context, in the upper left corner.
     * Use flush if needed to ensure the image represents the last version of the buffered raw data.
     * @param context context to render the image to.
     */
    public void renderToGraphics(Graphics context) {
        renderToGraphics(context, 0, 0);
    }

    /**
     * Renders the image to a graphics context.
     * Use flush if needed to ensure the image represents the last version of the buffered raw data.
     * @param context context to render the image to.
     * @param x position to render the image to
     * @param y position to render the image to
     */
    public void renderToGraphics(Graphics context, int x, int y) {
        context.drawImage(image, x, y, null);
    }

    /**
     * @return a new buffered image containing the raw data of this image.
     */
    public BufferedImage createBufferedImage() {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // TODO: Draw alpha pixels correctly

        flush();

        buf.getGraphics().drawImage(image, 0, 0, null);

        return buf;
    }

    /**
     * Renders a filled rectangle on the raw image.
     */
    public void fillRect(int x, int y, int w, int h, int color) {
        if (x + w >= 0 && x < width &&
            y + h >= 0 && y < height) {

            int x1 = MathUtils.clamp(x, 0, width - 1);
            int x2 = MathUtils.clamp(x + w, 0, width - 1);
            int y1 = MathUtils.clamp(y, 0, height - 1);
            int y2 = MathUtils.clamp(y + h, 0, height - 1);

            int i;
            for (int yp = y1; yp < y2; yp++) {
                i = yp * width + x1;
                for (int xp = x1; xp < x2; xp++) {
                    imageData[i++] = color;
                }
            }
        }
    }

    private void initialize() {

        // Don't include alpha for normal on-screen rendering, as it takes longer due to masking.
        // For reference, a color model with an alpha channel would be created with
        // new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff, 0xff000000);
        DirectColorModel rgbColorModel = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);

        imageData = new int[width * height];
        MemoryImageSource imageSource = new MemoryImageSource(width, height, rgbColorModel, imageData, 0, width);
        imageSource.setAnimated(true);

        image = Toolkit.getDefaultToolkit().createImage(imageSource);

        clear();
    }
}

