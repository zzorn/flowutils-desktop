package org.flowutils.desktop.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A Swing JFrame with sensible default settings.
 */
public class SimpleFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    /**
     * Creates a new empty frame with no title and opens it.
     */
    public SimpleFrame() {
        this("", null);
    }

    /**
     * Creates a new empty frame and opens it.
     *
     * @param title title of the frame.
     */
    public SimpleFrame(String title) {
        this(title, null);
    }

    /**
     * Creates a new frame and opens it.
     *
     * @param title title of the frame.
     * @param content UI component to show in the frame.
     */
    public SimpleFrame(String title, JComponent content) {
        this(title, content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Creates a new frame and opens it.
     *
     * @param title title of the frame.
     * @param content UI component to show in the frame.
     * @param initialWidth initial width of the frame.
     * @param initialHeight initial height of the frame.
     */
    public SimpleFrame(String title, JComponent content, int initialWidth, int initialHeight) {
        this(title, content, initialWidth, initialHeight, WindowConstants.EXIT_ON_CLOSE);
    }


    /**
     * Creates a new frame and opens it.
     *
     * @param title title of the frame.
     * @param content UI component to show in the frame.
     * @param initialWidth initial width of the frame.
     * @param initialHeight initial height of the frame.
     * @param closeOperation what to do when the frame is closed.  One of the constants from WindowConstants.
     */
    public SimpleFrame(String title, JComponent content, int initialWidth, int initialHeight, final int closeOperation) {
        setTitle(title);
        if (content != null) setContentPane(content);
        setPreferredSize(new Dimension(initialWidth, initialHeight));
        setDefaultCloseOperation(closeOperation);

        pack();
        setVisible(true);
    }
}

