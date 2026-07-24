package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Shared visual structure for all menu pages.
 */
public abstract class MenuPage implements Page {
    protected final Navigator navigator;

    protected MenuPage(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public final Parent createContent() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30, 24, 30, 24));

        Label title = new Label(getTitle());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        root.getChildren().add(title);

        GridPane options = new GridPane();
        options.setAlignment(Pos.TOP_CENTER);
        options.setHgap(14);
        options.setVgap(12);
        options.maxWidthProperty().bind(root.widthProperty().multiply(0.8));
        options.prefWidthProperty().bind(root.widthProperty().multiply(0.8));

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(50);
        firstColumn.setFillWidth(true);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(50);
        secondColumn.setFillWidth(true);
        options.getColumnConstraints().addAll(firstColumn, secondColumn);

        addOptions(options);
        root.getChildren().add(options);

        VBox footer = createFooter();
        VBox.setMargin(footer, new Insets(24, 0, 0, 0));
        root.getChildren().add(footer);

        return root;
    }

    protected abstract void addOptions(GridPane options);

    protected String getFooterButtonText() {
        return "Back";
    }

    protected Runnable getFooterButtonAction() {
        return navigator::back;
    }

    protected final void addOption(GridPane options, String text, Runnable action) {
        Button button = createButton(text);
        button.setMinHeight(120);
        button.setOnAction(event -> action.run());
        addButton(options, button);
    }

    protected final void addDisabledOption(GridPane options, String text) {
        Button button = createButton(text);
        button.setMinHeight(120);
        button.setDisable(true);
        addButton(options, button);
    }

    private void addButton(GridPane options, Button button) {
        int index = options.getChildren().size();
        options.add(button, index % 2, index / 2);
    }

    private VBox createFooter() {
        VBox footer = new VBox(18);
        footer.setAlignment(Pos.CENTER);

        Separator separator = new Separator();

        Button footerButton = createButton(getFooterButtonText());
        footerButton.setPrefWidth(160);
        footerButton.setMaxWidth(160);
        footerButton.setOnAction(event -> getFooterButtonAction().run());

        footer.getChildren().addAll(separator, footerButton);
        return footer;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(42);
        return button;
    }
}
