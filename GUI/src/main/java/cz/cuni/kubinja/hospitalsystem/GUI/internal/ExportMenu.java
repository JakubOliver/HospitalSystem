package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.function.Supplier;

/**
 * Menu containing export actions.
 */
public class ExportMenu extends ActionPage {
    private final BooleanProperty busy = new SimpleBooleanProperty(false);
    private Label status;
    private ProgressIndicator progress;

    public ExportMenu(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Export";
    }

    @Override
    public double getPreferredWidth() {
        return 560;
    }

    @Override
    public double getPreferredHeight() {
        return 560;
    }

    @Override
    protected Node createBody() {
        GridPane options = new GridPane();
        options.setHgap(14);
        options.setVgap(12);
        options.setAlignment(Pos.TOP_CENTER);

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(50);
        firstColumn.setFillWidth(true);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(50);
        secondColumn.setFillWidth(true);
        options.getColumnConstraints().addAll(firstColumn, secondColumn);

        addExportOption(
                options,
                "Export patients",
                "patients",
                hospital::exportPatients
        );
        addExportOption(
                options,
                "Export doctors",
                "doctors",
                hospital::exportDoctors
        );
        addExportOption(
                options,
                "Export appointments",
                "appointments",
                hospital::exportAppointments
        );
        addExportOption(
                options,
                "Export all",
                "all hospital data",
                hospital::export
        );

        progress = new ProgressIndicator();
        progress.setMaxSize(42, 42);
        progress.visibleProperty().bind(busy);
        progress.managedProperty().bind(progress.visibleProperty());

        status = new Label("Exports are saved as dated CSV files in the exports directory.");
        status.setWrapText(true);
        status.setMaxWidth(430);
        status.setAlignment(Pos.CENTER);
        status.setTextAlignment(TextAlignment.CENTER);

        VBox body = new VBox(22, options, progress, status);
        body.setAlignment(Pos.TOP_CENTER);
        options.maxWidthProperty().bind(body.widthProperty().multiply(0.8));
        options.prefWidthProperty().bind(body.widthProperty().multiply(0.8));
        return body;
    }

    @Override
    protected boolean shouldGrowBody() {
        return false;
    }

    private void addExportOption(
            GridPane options,
            String text,
            String description,
            Supplier<GeneralPacket> operation
    ) {
        Button button = new Button(text);
        button.setMinHeight(120);
        button.setMaxWidth(Double.MAX_VALUE);
        button.disableProperty().bind(busy);
        button.setOnAction(event -> runExport(description, operation));

        int optionIndex = options.getChildren().size();
        int column = optionIndex % 2;
        int row = optionIndex / 2;
        options.add(button, column, row);
    }

    private void runExport(String description, Supplier<GeneralPacket> operation) {
        busy.set(true);
        status.setText("Exporting " + description + "...");

        BackgroundOperation.run(
                operation,
                packet -> {
                    busy.set(false);
                    if (showApiError(packet)) {
                        status.setText("Export failed.");
                        return;
                    }

                    status.setText("Export completed successfully.");
                    showSuccess(
                            "The " + description
                                    + " export was saved in the exports directory."
                    );
                },
                exception -> {
                    busy.set(false);
                    status.setText("Export failed.");
                    showUnexpectedError(exception);
                }
        );
    }
}
