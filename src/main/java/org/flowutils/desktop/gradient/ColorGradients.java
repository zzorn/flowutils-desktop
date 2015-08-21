package org.flowutils.desktop.gradient;


import java.awt.*;

/**
 * Utility class with some common color gradients.
 */
public final class ColorGradients {

    public static final ColorGradient RAINBOW = new ColorGradient(
            new Color(200, 0, 130),
            new Color(255, 0, 0),
            new Color(255, 128, 0),
            new Color(255, 220, 0),
            new Color(150, 220, 0),
            new Color(0, 200, 0),
            new Color(0, 180, 120),
            new Color(0, 150, 255),
            new Color(0, 60, 255),
            new Color(0, 0, 255),
            new Color(150, 0, 230)
    ).makeReadOnly();

    public static final ColorGradient BLUERED = new ColorGradient(
            new Color(0, 0, 255),
            new Color(80, 160, 255),
            new Color(220, 220, 220),
            new Color(255, 160, 80),
            new Color(255, 0, 0)
    ).makeReadOnly();

    public static final ColorGradient GREYSCALE = new ColorGradient(
            new Color(0, 0, 0),
            new Color(255, 255, 255)
    ).makeReadOnly();

    public static final ColorGradient WARM_TO_COLD = new ColorGradient(
            new Color(255, 220, 0),
            new Color(255, 140, 0),
            new Color(230, 50, 0),
            new Color(200, 0, 130),
            new Color(150, 0, 160),
            new Color(100, 0, 200),
            new Color(50, 0, 230)
    ).makeReadOnly();

    public static final ColorGradient GREEN_TO_RED = new ColorGradient(
            new Color(0, 220, 0),
            new Color(170, 230, 0),
            new Color(250, 230, 0),
            new Color(245, 170, 0),
            new Color(240, 0, 0)
    ).makeReadOnly();

    public static final ColorGradient COMPUTER_COLORS = new ColorGradient(
            new Color(0, 0, 0),
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(0, 0, 255),
            new Color(255, 0, 255),
            new Color(255, 255, 255)
    ).makeReadOnly();

    private ColorGradients() {
    }
}
