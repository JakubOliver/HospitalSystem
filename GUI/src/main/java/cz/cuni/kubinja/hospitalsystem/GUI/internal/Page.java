package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.Parent;

/**
 * A page that can be displayed by the GUI navigator.
 */
public interface Page {
    public double DEFAULT_WIDTH = 1000;
    public double DEFAULT_HEIGHT = 780;

    /**
     * Returns the title of the page.
     * @return Title of the page.
     */
    String getTitle();

    /**
     * Creates the content of the page.
     * @return Content of the page.
     */
    Parent createContent();

    /**
     * Returns preferred width of the page.
     * @return Preferred width of the page.
     */
    default double getPreferredWidth() {
        return DEFAULT_WIDTH;
    }

    /**
     * Returns preferred height of the page.
     * @return Preferred height of the page.
     */
    default double getPreferredHeight() {
        return DEFAULT_HEIGHT;
    }
}
