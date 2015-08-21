package org.flowutils.desktop.gradient;

import org.flowutils.Check;
import org.flowutils.mapping.Mapper;

import java.awt.Color;import java.lang.Number;import java.lang.Override;

import static org.flowutils.Check.notNull;

/**
 * Converts source values to a color using some ColorFunction (e.g. a ColorGradient).
 * Optionally maps the source value to a more suitable range for the color function first, using scaling and offsets.
 */
// NOTE: Somewhat superfluous now that ColorFunction implements Mapper<Double, Color>
public final class ColorMapper<S extends Number> implements Mapper<S, Color> {

    private static final Color DEFAULT_COLOR = Color.DARK_GRAY;

    private static final double DEFAULT_PRE_OFFSET = 0.0;
    private static final double DEFAULT_POST_OFFSET = 0.0;
    private static final double DEFAULT_SCALE = 1.0;

    private ColorFunction colorFunction;

    private double preOffset = DEFAULT_PRE_OFFSET;
    private double scale = DEFAULT_SCALE;
    private double postOffset = DEFAULT_PRE_OFFSET;

    private Color defaultColor = DEFAULT_COLOR;

    /**
     * Creates a new ColorValueConverter without any ColorFunction.  All outputs will be the default color (dark gray).
     */
    public ColorMapper() {
        this(null);
    }

    /**
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     */
    public ColorMapper(ColorFunction colorFunction) {
        this(colorFunction, DEFAULT_COLOR);
    }

    /**
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param defaultColor the color to use when no value is available, or the color function hasn't been specified.
     */
    public ColorMapper(ColorFunction colorFunction, Color defaultColor) {
        this(colorFunction, DEFAULT_PRE_OFFSET, DEFAULT_SCALE, DEFAULT_POST_OFFSET, defaultColor);
    }

    /**
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     */
    public ColorMapper(ColorFunction colorFunction, double preOffset, double scale, double postOffset) {
        this(colorFunction, preOffset, scale, postOffset, DEFAULT_COLOR);
    }

    /**
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     * @param defaultColor the color to use when no value is available, or the color function hasn't been specified.
     */
    public ColorMapper(ColorFunction colorFunction,
                       double preOffset,
                       double scale,
                       double postOffset,
                       Color defaultColor) {
        setColorFunction(colorFunction);
        setSourceValueMapping(preOffset, scale, postOffset);
        setDefaultColor(defaultColor);
    }

    /**
     * Utility method for creating a ColorValueConverter.
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     */
    public static <S extends Number> ColorMapper<S> create(ColorFunction colorFunction) {
        return new ColorMapper<S>(colorFunction);
    }

    /**
     * Utility method for creating a ColorValueConverter.
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param defaultColor the color to use when no value is available, or the color function hasn't been specified.
     */
    public static <S extends Number> ColorMapper<S> create(ColorFunction colorFunction, Color defaultColor) {
        return new ColorMapper<S>(colorFunction, defaultColor);
    }

    /**
     * Utility method for creating a ColorValueConverter.
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     */
    public static <S extends Number> ColorMapper<S> create(ColorFunction colorFunction, double preOffset, double scale, double postOffset) {
        return new ColorMapper<S>(colorFunction, preOffset, scale, postOffset);
    }

    /**
     * Utility method for creating a ColorValueConverter.
     * @param colorFunction color function to convert the value to a color with.  For example a color gradient.
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     * @param defaultColor the color to use when no value is available, or the color function hasn't been specified.
     */
    public static <S extends Number> ColorMapper<S> create(ColorFunction colorFunction, double preOffset, double scale, double postOffset, Color defaultColor) {
        return new ColorMapper<S>(colorFunction, preOffset, scale, postOffset, defaultColor);
    }

    /**
     * @return color function to use for converting (mapped) source values to output colors.
     */
    public ColorFunction getColorFunction() {
        return colorFunction;
    }

    /**
     * @param colorFunction  color function to use for converting (mapped) source values to output colors.
     */
    public void setColorFunction(ColorFunction colorFunction) {
        this.colorFunction = colorFunction;
    }

    /**
     * @return the color to use when no value is available, or the color function hasn't been specified.
     */
    public Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * @param defaultColor the color to use when no value is available, or the color function hasn't been specified.
     */
    public void setDefaultColor(Color defaultColor) {
        notNull(defaultColor, "defaultColor");

        this.defaultColor = defaultColor;
    }

    /**
     * Used to scale the source values before being fed to the color function.
     * Color gradients are often defined from 0 or -1 to 1, while the source values might have a larger range.
     * This mapping can be used to project the source values to a more suitable range for the color function.
     *
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     */
    public void setSourceValueMapping(double preOffset, double scale, double postOffset) {
        Check.normalNumber(preOffset, "preOffset");
        Check.normalNumber(scale, "scale");
        Check.normalNumber(postOffset, "postOffset");

        this.preOffset = preOffset;
        this.scale = scale;
        this.postOffset = postOffset;
    }

    /**
     * @return number to be added to the source before scaling it.  Defaults to zero.
     */
    public double getPreOffset() {
        return preOffset;
    }

    /**
     * @return scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     */
    public double getScale() {
        return scale;
    }

    /**
     * @return offset to add to the mapped value last.  Defaults to zero.
     */
    public double getPostOffset() {
        return postOffset;
    }

    /**
     * @param preOffset number to be added to the source before scaling it.  Defaults to zero.
     */
    public void setPreOffset(double preOffset) {
        this.preOffset = preOffset;
    }

    /**
     * @param scale scaling to multiply the source with after the preOffset has been added to it.  Defaults to one.
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * @param postOffset offset to add to the mapped value last.  Defaults to zero.
     */
    public void setPostOffset(double postOffset) {
        this.postOffset = postOffset;
    }

    @Override public Color convert(S sourceValue) {
        if (colorFunction == null || sourceValue != null) {
            return colorFunction.colorForValue((sourceValue.doubleValue() + preOffset) * scale + postOffset);
        } else {
            return defaultColor;
        }
    }
}
