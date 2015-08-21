package org.flowutils.desktop.ui;

import org.flowutils.drawcontext.DrawContext;

/**
 * Something that can be rendered to a graphics context.
 */
public interface Renderable {

    /**
     * @param drawContext context to draw on.
     */
    void render(DrawContext drawContext);

}
