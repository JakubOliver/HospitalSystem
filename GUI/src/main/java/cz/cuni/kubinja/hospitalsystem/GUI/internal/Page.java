package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.Parent;

/**
 * A page that can be displayed by the GUI navigator.
 */
public interface Page {
    String getTitle();
    Parent createContent();
}
