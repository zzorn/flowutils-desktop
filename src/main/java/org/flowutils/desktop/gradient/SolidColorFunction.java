package org.flowutils.desktop.gradient;


import java.awt.*;

import static org.flowutils.Check.notNull;

/**
 * A constant color function that returns the same color for all values.
 */
public final class SolidColorFunction extends ColorFunctionBase {

    private Color color;
    private int colorCode;

    public SolidColorFunction() {
        this(Color.BLACK);
    }

    public SolidColorFunction(int colorCode) {
        this(new Color(colorCode));
    }

    public SolidColorFunction(double red, double green, double blue) {
        this(red, green, blue, 1.0);
    }

    public SolidColorFunction(double red, double green, double blue, double alpha) {
        this(new Color((float) red, (float)green, (float) blue, (float) alpha));
    }

    public SolidColorFunction(Color color) {
        setColor(color);
    }

    /**
     * @return the color returned.
     */
    public Color getColor() {
        return color;
    }

    public void setColor(int colorCode) {
        setColor(new Color(colorCode));
    }

    public void setColor(double red, double green, double blue) {
        setColor(red, green, blue, 1.0);
    }

    public void setColor(double red, double green, double blue, double alpha) {
        setColor(new Color((float) red, (float)green, (float) blue, (float) alpha));
    }

    /**
     * @param color the color to return.
     */
    public void setColor(Color color) {
        notNull(color, "color");

        this.color = color;
        colorCode = color.getRGB();
    }

    @Override public Color colorForValue(double value) {
        return color;
    }

    @Override public int colorCodeForValue(double value) {
        return colorCode;
    }
}
