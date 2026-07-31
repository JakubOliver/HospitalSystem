package cz.cuni.kubinja.hospitalsystem.GUI.internal.appointment;

import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Locale;

/**
 * Search field and selector for hospital personnel.
 *
 * @param <T> Type of personnel being selected.
 */
final class PersonnelSelector<T extends Person> extends VBox {
    private final TextField search = new TextField();
    private final ComboBox<T> choices = new ComboBox<>();
    private final ObservableList<T> people = FXCollections.observableArrayList();
    private final FilteredList<T> filteredChoices = new FilteredList<>(people);

    PersonnelSelector(String personnelName) {
        super(6);
        setAlignment(Pos.TOP_LEFT);

        search.setPromptText("Search " + personnelName.toLowerCase(Locale.ROOT)
            + " by ID or name");
        choices.setPromptText("Select " + personnelName.toLowerCase(Locale.ROOT));
        choices.setMaxWidth(Double.MAX_VALUE);
        choices.setConverter(new StringConverter<>() {
            @Override
            public String toString(T person) {
                if (person == null) {
                    return "";
                }

                return person.getId() + " – "
                    + person.getFirstName() + " " + person.getLastName();
            }

            @Override
            public T fromString(String value) {
                return null;
            }
        });
        choices.setItems(filteredChoices);

        search.textProperty().addListener(
            (observable, oldValue, newValue) -> applyFilter(newValue)
        );

        getChildren().addAll(new Label(personnelName + ":"), search, choices);
    }

    void setPeople(List<T> people) {
        this.people.setAll(people);
        applyFilter(search.getText());
    }

    T getSelected() {
        return choices.getValue();
    }

    ObjectProperty<T> selectedProperty() {
        return choices.valueProperty();
    }

    void selectById(int id) {
        search.clear();

        people.stream()
            .filter(person -> person.getId() == id)
            .findFirst()
            .ifPresent(choices::setValue);
    }

    private void applyFilter(String value) {
        String query = value == null
            ? ""
            : value.trim().toLowerCase();

        filteredChoices.setPredicate(person -> {
            if (query.isEmpty()) {
                return true;
            }

            return Integer.toString(person.getId()).contains(query)
                || person.getFirstName().toLowerCase().contains(query)
                || person.getLastName().toLowerCase().contains(query)
                || (person.getFirstName() + " " + person.getLastName())
                    .toLowerCase()
                    .contains(query);
        });
    }
}
