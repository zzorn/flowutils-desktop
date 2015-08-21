package org.flowutils.desktop.rawimage;

/**
 * Something that renders a RawImage
 */
public interface RawImageRenderer {

    /**
     * @param target the target to render to.
     *               Remember to call flush on the target after the image has been rendered.
     */
    void renderImage(RawImage target);

}
