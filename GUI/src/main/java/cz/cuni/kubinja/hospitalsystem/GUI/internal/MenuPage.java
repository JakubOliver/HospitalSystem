package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Shared visual structure for all menu pages.
 */
public abstract class MenuPage extends BasePage {
    private static final double PAGE_VERTICAL_PADDING = 30;
    private static final double PAGE_HORIZONTAL_PADDING = 24;
    private static final double OPTIONS_WIDTH_RATIO = 0.8;
    private static final double FOOTER_TOP_MARGIN = 24;

    protected final Navigator navigator;
    /** Style for menu buttons */
    public static final String MENU_BUTTON_STYLE =
            "-fx-font-size: 16px; " + EMPHASIZED_TEXT_STYLE;

    protected MenuPage(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public final Parent createContent() {
        VBox root = createPageRoot(new Insets(
                PAGE_VERTICAL_PADDING,
                PAGE_HORIZONTAL_PADDING,
                PAGE_VERTICAL_PADDING,
                PAGE_HORIZONTAL_PADDING
        ));

        root.getChildren().add(createPageTitle());

        GridPane options = createTwoColumnOptionsGrid();
        options.maxWidthProperty().bind(
                root.widthProperty().multiply(OPTIONS_WIDTH_RATIO)
        );
        options.prefWidthProperty().bind(
                root.widthProperty().multiply(OPTIONS_WIDTH_RATIO)
        );

        addOptions(options);
        root.getChildren().add(options);

        VBox footer = createNavigationFooter(
                getFooterButtonText(),
                getFooterButtonAction()
        );
        VBox.setMargin(footer, new Insets(FOOTER_TOP_MARGIN, 0, 0, 0));
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
        Button button = createMenuButton(text);
        button.setOnAction(event -> action.run());
        addButton(options, button);
    }

    protected final void addDisabledOption(GridPane options, String text) {
        Button button = createMenuButton(text);
        button.setDisable(true);
        addButton(options, button);
    }

    private void addButton(GridPane options, Button button) {
        int index = options.getChildren().size();
        options.add(button, index % 2, index / 2);
    }

    private Button createMenuButton(String text) {
        return createMenuOptionButton(text, MENU_BUTTON_STYLE);
    }
}
