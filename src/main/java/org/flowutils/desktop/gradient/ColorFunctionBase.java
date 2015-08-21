package org.flowutils.desktop.gradient;

import java.awt.*;

/**
 * Base class for ColorFunctions.  Override either the colorForValue or colorCodeForValue (or both) methods.
 */
public abstract class ColorFunctionBase implements ColorFunction {

    @Override public Color colorForValue(double value) {
        return new Color(colorCodeForValue(value));
    }

    @Override public int colorCodeForValue(double value) {
        return colorForValue(value).getRGB();
    }

    @Override public Color convert(Double value) {
        return colorForValue(value);
    }
}
