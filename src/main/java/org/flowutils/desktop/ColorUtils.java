package org.flowutils.desktop;

import org.flowutils.MathUtils;
import org.flowutils.random.RandomSequence;

import java.awt.*;

import static org.flowutils.MathUtils.clamp;
import static org.flowutils.MathUtils.mix;

/**
 * Color related utility functions.
 */
public final class ColorUtils {


    /**
     * Mixes two colors.
     * @param mixAmount if 0, returns a, if 1, returns b, else returns a new color that is a mix of a and b.
     *                  The resulting color components are clamped to the 0..255 range, but the mixAmount can be negative or over 1 as well.
     */
    public static Color mixColors(double mixAmount, Color a, Color b) {
        if (mixAmount == 0) return a;
        else if (mixAmount == 1) return b;
        else return new Color(
                    mixComponent(mixAmount, a.getRed(), b.getRed()),
                    mixComponent(mixAmount, a.getGreen(), b.getGreen()),
                    mixComponent(mixAmount, a.getBlue(), b.getBlue()),
                    mixComponent(mixAmount, a.getAlpha(), b.getAlpha())
            );
    }


    /**
     * @param randomSequence the source for randomness.
     * @return a random color uniformly picked from the full color space, with full opaqueness (alpha 255).
     */
    public static Color randomColor(RandomSequence randomSequence) {
        return randomColor(randomSequence, false);
    }

    /**
     * @param randomSequence the source for randomness.
     * @param randomizeAlpha if true, the transparency of the color will also be randomized.
     * @return a random color uniformly picked from the full color space.
     */
    public static Color randomColor(RandomSequence randomSequence, boolean randomizeAlpha) {
        return new Color(randomSequence.nextInt(256),
                         randomSequence.nextInt(256),
                         randomSequence.nextInt(256),
                         randomizeAlpha ? randomSequence.nextInt(256) : 255);
    }


    /**
     * @return the hue of the specified color, from 0 to 1.
     */
    public static double hue(Color color) {
        double hue;
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        double cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;

        double cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        double saturation;
        if (cmax == 0) {
            saturation = 0;
        } else {
            saturation = (cmax - cmin) / cmax;
        }

        if (saturation == 0) {
            hue = 0;
        }
        else {
            double redc   = (cmax - r) / (cmax - cmin);
            double greenc = (cmax - g) / (cmax - cmin);
            double bluec  = (cmax - b) / (cmax - cmin);

            if (r == cmax) hue = bluec - greenc;
            else if (g == cmax) hue = 2.0 + redc - bluec;
            else hue = 4.0 + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0) hue = hue + 1.0;
        }

        return hue;
    }

    /**
     * @returnt the saturation of the specified color, 0 = greyscale, 1 = fully saturated.
     */
    public static double sat(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        double cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;

        double cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        double saturation;
        if (cmax == 0) {
            saturation = 0;
        } else {
            saturation = (cmax - cmin) / cmax;
        }

        return saturation;
    }

    /**
     * @return the luminance of the specified color, 0 = black, 1 = bright.
     */
    public static double lum(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        double maxComponent = (r > g) ? r : g;
        if (b > maxComponent) maxComponent = b;

        return maxComponent;
    }

    /**
     * @param hue range 0 (red) to 1 (red)
     * @param sat range 0 (greyscale) to 1 (fully saturated)
     * @param lum range 0 (black) to 1 (fully bright colors)
     * @return new color with the specified hue, saturation and luminance.
     */
    public static Color hslColor(double hue, double sat, double lum) {
        return hslColor(hue, sat, lum, 1.0);
    }

    /**
     * @param hue range 0 (red) to 1 (red)
     * @param sat range 0 (greyscale) to 1 (fully saturated)
     * @param lum range 0 (black) to 1 (fully bright colors)
     * @param alpha transparency, from 0 (transparent) to 1 (opaque)
     * @return new color with the specified hue, saturation, luminance and alpha.
     */
    public static Color hslColor(double hue, double sat, double lum, double alpha) {
        hue   = MathUtils.wrap0To1(hue);
        sat   = MathUtils.clamp0To1(sat);
        lum   = MathUtils.clamp0To1(lum);
        alpha = MathUtils.clamp0To1(alpha);

        double r = 0, g = 0, b = 0;
        if (sat == 0) {
            r = g = b = lum;
        } else {
            double h = (hue - Math.floor(hue)) * 6.0f;
            double f = h - Math.floor(h);
            double p = lum * (1.0f - sat);
            double q = lum * (1.0f - sat * f);
            double t = lum * (1.0f - (sat * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = lum;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = lum;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = lum;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = lum;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = lum;
                    break;
                case 5:
                    r = lum;
                    g = p;
                    b = q;
                    break;
            }
        }

        return new Color((float) r, (float) g, (float) b, (float) alpha);
    }

    private static int mixComponent(double mixAmount, int c1, int c2) {
        final int mixedValue = (int) mix(mixAmount, c1, c2);
        return clamp(mixedValue, 0, 255);
    }


    private ColorUtils() {
    }
}
