package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.beans.binding.BooleanExpression;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Shared control and layout construction for JavaFX pages.
 */
public abstract class BasePage implements Page {
    protected static final String EMPHASIZED_TEXT_STYLE =
            "-fx-font-weight: bold;";
    protected static final String PAGE_TITLE_STYLE =
            "-fx-font-size: 22px; " + EMPHASIZED_TEXT_STYLE;
    protected static final String SECTION_TITLE_STYLE =
            "-fx-font-size: 16px; " + EMPHASIZED_TEXT_STYLE;
    protected static final String DEFAULT_BUTTON_STYLE = "";

    protected static final double ACTION_BUTTON_HEIGHT = 42;
    protected static final double ACTION_BUTTON_WIDTH = 180;
    protected static final double SECONDARY_BUTTON_WIDTH = 160;
    protected static final double PROGRESS_INDICATOR_SIZE = 42;

    protected String getTitleStyle() {
        return PAGE_TITLE_STYLE;
    }

    protected final Label createPageTitle() {
        Label title = new Label(getTitle());
        title.setStyle(getTitleStyle());

        return title;
    }

    protected final Button createActionButton(String text, String style) {
        return createActionButton(text, style, ACTION_BUTTON_WIDTH);
    }

    protected final Button createActionButton(String text) {
        return createActionButton(text, DEFAULT_BUTTON_STYLE, ACTION_BUTTON_WIDTH);
    }

    protected final Button createActionButton(String text, double preferredWidth) {
        return createActionButton(text, DEFAULT_BUTTON_STYLE, preferredWidth);
    }

    protected final Button createActionButton(
            String text,
            String style,
            double preferredWidth
    ) {
        Button button = new Button(text);
        button.setMinHeight(ACTION_BUTTON_HEIGHT);
        button.setStyle(style);
        button.setPrefWidth(preferredWidth);

        return button;
    }

    protected final Button createMenuOptionButton(String text, String style) {
        Button button = createActionButton(text, style);
        button.setMinHeight(120);
        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    protected final Button createMenuOptionButton(String text) {
        return createMenuOptionButton(text, DEFAULT_BUTTON_STYLE);
    }

    protected final VBox createCenteredBox(double spacing, Node... children) {
        VBox box = new VBox(spacing, children);
        box.setAlignment(Pos.TOP_CENTER);

        return box;
    }

    protected final VBox createPageRoot(Insets padding) {
        VBox root = createCenteredBox(20);
        root.setPadding(padding);

        return root;
    }

    protected final GridPane createTwoColumnOptionsGrid() {
        GridPane options = new GridPane();
        options.setAlignment(Pos.TOP_CENTER);
        options.setHgap(14);
        options.setVgap(12);

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(50);
        firstColumn.setFillWidth(true);

        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(50);
        secondColumn.setFillWidth(true);

        options.getColumnConstraints().addAll(
                firstColumn,
                secondColumn
        );

        return options;
    }

    protected final VBox createNavigationFooter(
            String buttonText,
            Runnable action
    ) {
        Button button = createActionButton(buttonText, SECONDARY_BUTTON_WIDTH);
        button.setMaxWidth(SECONDARY_BUTTON_WIDTH);
        button.setOnAction(event -> action.run());

        VBox footer = new VBox(18, new Separator(), button);
        footer.setAlignment(Pos.CENTER);

        return footer;
    }

    protected final ProgressIndicator createProgressIndicator(
            BooleanExpression visibleWhen
    ) {
        return createProgressIndicator(
                visibleWhen,
                PROGRESS_INDICATOR_SIZE
        );
    }

    protected final ProgressIndicator createProgressIndicator(
            BooleanExpression visibleWhen,
            double size
    ) {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(size, size);
        progress.visibleProperty().bind(visibleWhen);
        progress.managedProperty().bind(progress.visibleProperty());

        return progress;
    }

    public static void applyEmphasizedTextStyle(Node node) {
        node.setStyle(EMPHASIZED_TEXT_STYLE);
    }

    public static void applySectionTitleStyle(Node node) {
        node.setStyle(SECTION_TITLE_STYLE);
    }
}
