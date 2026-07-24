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

    public Navigator(Stage stage) {
        this.stage = stage;
    }

    public void start(Page initialPage) {
        show(initialPage);
        stage.show();
    }

    public void navigate(Page page) {
        if (currentPage != null) {
            history.push(currentPage);
        }
        show(page);
    }

    public void back() {
        if (!history.isEmpty()) {
            show(history.pop());
        }
    }

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
