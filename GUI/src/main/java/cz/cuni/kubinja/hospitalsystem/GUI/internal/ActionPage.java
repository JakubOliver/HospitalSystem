package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared page structure, navigation and feedback behavior for action workflows.
 */
public abstract class ActionPage extends BasePage {
    protected static final double DEFAULT_CENTERED_BOX_SPACING = 18;
    private static final String ERROR_TEXT_STYLE =
            "-fx-text-fill: #b00020;";
    private static final String INLINE_ERROR_TEXT_STYLE =
            ERROR_TEXT_STYLE + " -fx-font-size: 11px;";
    private static final double PAGE_VERTICAL_PADDING = 30;
    private static final double PAGE_HORIZONTAL_PADDING = 36;
    private static final double PREFERRED_WIDTH = 700;
    private static final double DETAILS_HORIZONTAL_GAP = 18;
    private static final double DETAILS_VERTICAL_GAP = 12;
    private static final double DETAIL_VALUE_MAX_WIDTH = 450;

    protected final Navigator navigator;
    protected final Hospital hospital;
    private int detailRow;

    protected ActionPage(Navigator navigator, Hospital hospital) {
        this.navigator = navigator;
        this.hospital = hospital;
    }

    @Override
    public final Parent createContent() {
        VBox root = createPageRoot(new Insets(
                PAGE_VERTICAL_PADDING,
                PAGE_HORIZONTAL_PADDING,
                PAGE_VERTICAL_PADDING,
                PAGE_HORIZONTAL_PADDING
        ));

        Node body = createBody();

        if (shouldGrowBody()) {
            VBox.setVgrow(body, Priority.ALWAYS);
        }

        root.getChildren().addAll(
                createPageTitle(),
                body,
                createNavigationFooter("Back", navigator::back)
        );
        return root;
    }

    @Override
    public double getPreferredWidth() {
        return PREFERRED_WIDTH;
    }

    protected abstract Node createBody();

    protected boolean shouldGrowBody() {
        return true;
    }

    protected boolean showApiError(GeneralPacket packet) {
        if (packet.successful) {
            return false;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hospital System");
        alert.setHeaderText("The operation failed");
        alert.setContentText(packet.resolveStatus());
        alert.showAndWait();

        return true;
    }

    protected void showUnexpectedError(Throwable throwable) {
        Exception exception = throwable instanceof Exception error
                ? error
                : new Exception(throwable);

        showApiError(new GeneralPacket(exception));
    }

    protected void complete(String message) {
        showSuccess(message);
        navigator.back();
    }

    protected void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hospital System");
        alert.setHeaderText("Operation completed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected boolean confirmAction(
            String title,
            String header,
            String content
    ) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle(title);
        confirmation.setHeaderText(header);
        confirmation.setContentText(content);

        return confirmation.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK;
    }

    protected final <T> void runBackgroundOperation(
            BooleanProperty busy,
            Supplier<T> operation,
            Consumer<T> onSuccess
    ) {
        runBackgroundOperation(
                busy,
                operation,
                onSuccess,
                this::showUnexpectedError
        );
    }

    protected final <T> void runBackgroundOperation(
            BooleanProperty busy,
            Supplier<T> operation,
            Consumer<T> onSuccess,
            Consumer<Throwable> onFailure
    ) {
        busy.set(true);
        BackgroundOperation.run(
                operation,
                result -> {
                    busy.set(false);
                    onSuccess.accept(result);
                },
                exception -> {
                    busy.set(false);
                    onFailure.accept(exception);
                }
        );
    }

    /**
     * Applies the shared error color to a node.
     *
     * @param node Node that should use the error color.
     */
    public static void applyErrorTextStyle(Node node) {
        node.setStyle(ERROR_TEXT_STYLE);
    }

    /**
     * Applies the shared compact error-label style to a node.
     *
     * @param node Node displaying an inline validation error.
     */
    public static void applyInlineErrorTextStyle(Node node) {
        node.setStyle(INLINE_ERROR_TEXT_STYLE);
    }

    protected GridPane personnelDetails(Person person, Detail... additionalDetails) {
        GridPane details = new GridPane();
        detailRow = 0;

        details.setHgap(DETAILS_HORIZONTAL_GAP);
        details.setVgap(DETAILS_VERTICAL_GAP);
        details.setAlignment(Pos.TOP_CENTER);

        addDetail(details, "ID", Integer.toString(person.getId()));
        addDetail(details, "First name", person.getFirstName());
        addDetail(details, "Last name", person.getLastName());
        addDetail(details, "Date of birth", person.getDateOfBirth().toString());

        for (Detail detail : additionalDetails) {
            addDetail(details, detail.name(), detail.value());
        }

        return details;
    }

    /**
     * Data wrapper for generic detail.
     *
     * @param name Name of the detail.
     * @param value Value (text) of the detail.
     */
    public record Detail(String name, String value) {}

    private void addDetail(GridPane details, String name, String value) {
        Label nameLabel = new Label(name + ":");
        applyEmphasizedTextStyle(nameLabel);

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(DETAIL_VALUE_MAX_WIDTH);

        details.add(nameLabel, 0, detailRow);
        details.add(valueLabel, 1, detailRow);

        detailRow++;
    }
}
