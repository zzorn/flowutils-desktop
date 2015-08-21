package org.flowutils.desktop.rawimage;

/**
 * Abstract base class that loops through the pixels and renders each.
 */
public abstract class RawImageRendererBase implements RawImageRenderer {

    @Override public final void renderImage(RawImage target) {
        final int w = target.getWidth();
        final int h = target.getHeight();
        final int[] buffer = target.getBuffer();

        prepareRendering(target, w, h);

        // Render pixels
        int index = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                final int pixelColor = getPixelColor(x, y, w, h);
                buffer[index++] = pixelColor;
            }
        }

        finishRendering(target, w, h);

        target.flush();
    }

    /**
     * Called before pixels are rendered.
     * @param target target that will be rendered to.
     * @param width target width
     * @param height target height
     */
    protected void prepareRendering(RawImage target, int width, int height) {}

    /**
     * Called for each pixel when rendering the image.
     * @param x pixel x coordinate to get the color of
     * @param y pixel y coordinate to get the color of
     * @param totalWidth width of the picture that is rendered to.
     * @param totalHeight height of the picture that is rendered to.
     * @return the color code for the pixel, containing RGBA components in a 32 bit int.
     */
    protected abstract int getPixelColor(int x, int y, int totalWidth, int totalHeight);

    /**
     * Called after all pixels are rendered.
     * @param target target that was rendered to.
     * @param width target width
     * @param height target height
     */
    protected void finishRendering(RawImage target, int width, int height) {}
}
