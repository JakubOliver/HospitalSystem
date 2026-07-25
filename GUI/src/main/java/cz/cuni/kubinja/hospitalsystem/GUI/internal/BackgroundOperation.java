package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Runs blocking work outside the JavaFX application thread.
 */
public final class BackgroundOperation {
    private BackgroundOperation() {}

    /**
     * Runs the given operation in a background thread.
     *
     * @param operation Blocking operation to run.
     * @param onSuccess Callback for successful completion of the operation.
     * @param onFailure Callback for failure of the operation.
     * @param <T> Type of the result of the operation.
     */
    public static <T> void run(
            Supplier<T> operation,
            Consumer<T> onSuccess,
            Consumer<Throwable> onFailure
    ) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return operation.get();
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onFailure.accept(task.getException()));

        Thread thread = new Thread(task, "hospital-system-background-operation");
        thread.setDaemon(true);
        thread.start();
    }
}
