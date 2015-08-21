package org.flowutils.desktop.zoomandpan;

import org.flowutils.zoomandpan.ZoomAndPannable;

import java.awt.*;
import java.awt.event.*;

import static org.flowutils.Check.notNull;

/**
 * Listens to mouse gestures, and forwards zoom and pan events to a ZoomAndPannable object.
 */
public class ZoomAndPanGestureHandler extends MouseAdapter implements KeyListener {

    private ZoomAndPannable zoomAndPannable;
    private Component componentToListenTo;

    private boolean verticalZoomToggle = false;
    private boolean horizontalZoomToggle = false;
    private int verticalZoomKey = KeyEvent.VK_CONTROL;
    private int horizontalZoomKey = KeyEvent.VK_SHIFT;

    private int panButton = MouseEvent.BUTTON1;
    private boolean panOngoing = false;

    private int panPrevX;
    private int panPrevY;

    public ZoomAndPanGestureHandler() {
    }

    /**
     * @param zoomAndPannable something to zoom and pan based on mouse gestures.
     */
    public ZoomAndPanGestureHandler(ZoomAndPannable zoomAndPannable) {
        setZoomAndPannable(zoomAndPannable);
    }

    /**
     * @param zoomAndPannable something to zoom and pan based on mouse gestures.
     * @param component component to listen for mouse gestures on.
     */
    public ZoomAndPanGestureHandler(ZoomAndPannable zoomAndPannable, Component component) {
        setZoomAndPannable(zoomAndPannable);
        setComponentToListenTo(component);
    }

    /**
     * @param component component to listen for mouse gestures on.
     */
    public ZoomAndPanGestureHandler(Component component) {
        setComponentToListenTo(component);
    }

    /**
     * @return something to zoom and pan based on mouse gestures.
     */
    public ZoomAndPannable getZoomAndPannable() {
        return zoomAndPannable;
    }

    /**
     * @param zoomAndPannable something to zoom and pan based on mouse gestures.
     */
    public void setZoomAndPannable(ZoomAndPannable zoomAndPannable) {
        this.zoomAndPannable = zoomAndPannable;
    }

    /**
     * @return component to listen for mouse gestures on.
     */
    public Component getComponentToListenTo() {
        return componentToListenTo;
    }

    /**
     * @param componentToListenTo component to listen for mouse gestures on.
     */
    public void setComponentToListenTo(Component componentToListenTo) {
        if (this.componentToListenTo != null) {
            stopListeningTo(componentToListenTo);
        }

        this.componentToListenTo = componentToListenTo;

        if (this.componentToListenTo != null) {
            startListeningTo(componentToListenTo);
        }
    }


    @Override public void mousePressed(MouseEvent e) {
        if (e.getButton() == panButton) {
            pan(e);
        }
    }

    @Override public void mouseReleased(MouseEvent e) {
        if (e.getButton() == panButton) {
            pan(e);
            panOngoing = false;
        }
    }

    @Override public void mouseMoved(MouseEvent e) {
        if (panOngoing) {
            pan(e);
        }
    }

    @Override public void mouseDragged(MouseEvent e) {
        if (panOngoing) {
            pan(e);
        }
    }

    private void pan(MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();

        if (panOngoing) {
            if (zoomAndPannable != null && componentToListenTo != null) {
                int dx = panPrevX - x;
                int dy = y - panPrevY;

                double relativeDx = (double)dx / componentToListenTo.getWidth();
                double relativeDy = (double)dy / componentToListenTo.getHeight();

                zoomAndPannable.pan(relativeDx, relativeDy);

                // Invoke repaint
                if (componentToListenTo != null) {
                    componentToListenTo.repaint();
                }
            }
        }
        else {
            panOngoing = true;
        }

        panPrevX = x;
        panPrevY = y;
    }

    @Override public void mouseWheelMoved(MouseWheelEvent e) {
        if (zoomAndPannable != null) {
            final double wheelRotation = e.getPreciseWheelRotation();
            final double defaultZoomStep = zoomAndPannable.getDefaultZoomStep();
            final double zoomScale = Math.pow(defaultZoomStep, -wheelRotation);

            if (verticalZoomToggle && !horizontalZoomToggle) zoomAndPannable.zoom(1, zoomScale, 0.5, 0.5);
            else if (horizontalZoomToggle && !verticalZoomToggle) zoomAndPannable.zoom(zoomScale, 1, 0.5, 0.5);
            else zoomAndPannable.zoom(zoomScale);

            // Invoke repaint
            if (componentToListenTo != null) componentToListenTo.repaint();
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == verticalZoomKey) {
            verticalZoomToggle = true;
        }
        if (e.getKeyCode() == horizontalZoomKey) {
            horizontalZoomToggle = true;
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == verticalZoomKey) {
            verticalZoomToggle = false;
        }
        if (e.getKeyCode() == horizontalZoomKey) {
            horizontalZoomToggle = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {
        // Ignored
    }

    private void startListeningTo(Component component) {
        notNull(component, "component");

        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
        component.addKeyListener(this);
    }

    private void stopListeningTo(Component component) {
        notNull(component, "component");

        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeMouseWheelListener(this);
        component.removeKeyListener(this);
    }

}
