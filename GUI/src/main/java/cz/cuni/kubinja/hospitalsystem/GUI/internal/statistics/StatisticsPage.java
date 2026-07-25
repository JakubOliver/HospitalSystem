package cz.cuni.kubinja.hospitalsystem.GUI.internal.statistics;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics.Occurrence;
import cz.cuni.kubinja.hospitalsystem.core.statistics.HospitalStatistics.PatientVisits;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Locale;

/**
 * Page displaying hospital statistics.
 */
final class StatisticsPage extends ActionPage {
    private final VBox statisticsContent = new VBox(22);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    StatisticsPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Hospital statistics";
    }

    @Override
    public double getPreferredWidth() {
        return 900;
    }

    @Override
    protected Node createBody() {
        statisticsContent.setPadding(new Insets(4, 14, 12, 4));

        ScrollPane scrollPane = new ScrollPane(statisticsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        ProgressIndicator progress = createProgressIndicator(loading, 48);

        Button refresh = createActionButton(
                "Refresh",
                SECONDARY_BUTTON_WIDTH
        );
        refresh.disableProperty().bind(loading);
        refresh.setOnAction(event -> loadStatistics());

        VBox body = createCenteredBox(14, progress, scrollPane, refresh);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        loadStatistics();
        return body;
    }

    private void loadStatistics() {
        statisticsContent.getChildren().setAll(new Label("Loading statistics..."));

        runBackgroundOperation(
                loading,
                hospital::getStatistics,
                packet -> {
                    if (showApiError(packet)) {
                        showLoadError();
                        return;
                    }

                    showStatistics(packet.data);
                },
                exception -> {
                    showLoadError();
                    showUnexpectedError(exception);
                }
        );
    }

    private void showStatistics(HospitalStatistics statistics) {
        statisticsContent.getChildren().setAll(
                sectionTitle("Absolute values"),
                totals(statistics),
                sectionTitle("Averages"),
                averages(statistics),
                sectionTitle("5 patients with most visits"),
                patientVisitsTable(statistics.patientsWithMostVisits()),
                sectionTitle("10 most common anamneses"),
                occurrenceTable(
                        statistics.commonAnamneses(),
                        "Anamnesis",
                        "No anamneses found."
                ),
                sectionTitle("10 most common specializations"),
                occurrenceTable(
                        statistics.commonSpecializations(),
                        "Specialization",
                        "No specializations found."
                )
        );
    }

    private GridPane totals(HospitalStatistics statistics) {
        GridPane grid = valueGrid();
        addValue(grid, 0, "Patients", Integer.toString(statistics.patientCount()));
        addValue(grid, 1, "Doctors", Integer.toString(statistics.doctorCount()));
        addValue(grid, 2, "Appointments", Integer.toString(statistics.appointmentCount()));
        return grid;
    }

    private GridPane averages(HospitalStatistics statistics) {
        GridPane grid = valueGrid();
        addValue(
                grid,
                0,
                "Appointments per patient",
                String.format(
                        Locale.ROOT,
                        "%.3f",
                        statistics.averageAppointmentsPerPatient()
                )
        );
        addValue(
                grid,
                1,
                "Appointments per doctor",
                String.format(
                        Locale.ROOT,
                        "%.3f",
                        statistics.averageAppointmentsPerDoctor()
                )
        );
        return grid;
    }

    private TableView<PatientVisits> patientVisitsTable(List<PatientVisits> entries) {
        TableView<PatientVisits> table = new TableView<>();
        table.setPlaceholder(new Label("No patients found."));
        table.setPrefHeight(190);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<PatientVisits, Void> rank = rankColumn();

        TableColumn<PatientVisits, Number> id = new TableColumn<>("ID");
        id.setCellValueFactory(cell -> new ReadOnlyIntegerWrapper(cell.getValue().patientId()));

        TableColumn<PatientVisits, String> patient = new TableColumn<>("Patient");
        patient.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().firstName() + " " + cell.getValue().lastName()
        ));

        TableColumn<PatientVisits, Number> appointments =
                new TableColumn<>("Appointments");
        appointments.setCellValueFactory(
                cell -> new ReadOnlyIntegerWrapper(cell.getValue().appointmentCount())
        );

        table.getColumns().addAll(List.of(rank, id, patient, appointments));
        table.getItems().setAll(entries);
        return table;
    }

    private TableView<Occurrence> occurrenceTable(
            List<Occurrence> entries,
            String valueTitle,
            String emptyMessage
    ) {
        TableView<Occurrence> table = new TableView<>();
        table.setPlaceholder(new Label(emptyMessage));
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Occurrence, Void> rank = rankColumn();

        TableColumn<Occurrence, String> value = new TableColumn<>(valueTitle);
        value.setCellValueFactory(
                cell -> new ReadOnlyStringWrapper(cell.getValue().value())
        );

        TableColumn<Occurrence, Number> count = new TableColumn<>("Count");
        count.setCellValueFactory(
                cell -> new ReadOnlyIntegerWrapper(cell.getValue().count())
        );

        table.getColumns().addAll(List.of(rank, value, count));
        table.getItems().setAll(entries);
        return table;
    }

    private <T> TableColumn<T, Void> rankColumn() {
        TableColumn<T, Void> rank = new TableColumn<>("#");
        rank.setMinWidth(45);
        rank.setMaxWidth(55);
        rank.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : Integer.toString(getIndex() + 1));
            }
        });
        return rank;
    }

    private GridPane valueGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(10);
        return grid;
    }

    private void addValue(GridPane grid, int row, String name, String value) {
        Label nameLabel = new Label(name + ":");
        applyEmphasizedTextStyle(nameLabel);
        grid.add(nameLabel, 0, row);
        grid.add(new Label(value), 1, row);
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        applySectionTitleStyle(label);
        return label;
    }

    private void showLoadError() {
        statisticsContent.getChildren().setAll(
                new Label("Statistics could not be loaded. Use Refresh to try again.")
        );
    }

}
