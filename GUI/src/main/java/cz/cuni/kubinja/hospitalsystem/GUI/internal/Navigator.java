package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Controls navigation within the application's single primary window.
 */
public final class Navigator {
    private final Stage stage;
    private final Deque<Page> history = new ArrayDeque<>();
    private Page currentPage;

    /**
     * Constructor of Navigator class.
     *
     * @param stage Primary window of the application.
     */
    public Navigator(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initialize the application and shows the initial page.
     *
     * @param initialPage Initial page to show.
     */
    public void start(Page initialPage) {
        show(initialPage);
        stage.show();
    }

    /**
     * Navigates to the specified page.
     *
     * @param page Page to navigate to.
     */
    public void navigate(Page page) {
        if (currentPage != null) {
            history.push(currentPage);
        }
        show(page);
    }

    /**
     * Navigates back to the previous page.
     */
    public void back() {
        if (!history.isEmpty()) {
            show(history.pop());
        }
    }

    /**
     * Closes the application.
     */
    public void close() {
        stage.close();
    }

    private void show(Page page) {
        currentPage = page;
        stage.setTitle(page.getTitle());
        stage.setScene(new Scene(
                page.createContent(),
                page.getPreferredWidth(),
                page.getPreferredHeight()
        ));
        stage.centerOnScreen();
    }
}
