package org.flowutils.desktop;

import org.flowutils.random.RandomSequence;

import java.awt.*;

import static org.flowutils.MathUtils.*;

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

    // TODO: Implement
    // public static Color hslColor(double hue, double saturation, double luminance) {


    private static int mixComponent(double mixAmount, int c1, int c2) {
        final int mixedValue = (int) mix(mixAmount, c1, c2);
        return clamp(mixedValue, 0, 255);
    }


    private ColorUtils() {
    }
}
