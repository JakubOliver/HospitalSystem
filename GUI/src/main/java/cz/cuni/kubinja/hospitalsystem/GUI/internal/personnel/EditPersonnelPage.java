package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;

/**
 * Shared workflow for loading and editing a patient or doctor.
 *
 * @param <T> Type of personnel being edited.
 */
public abstract class EditPersonnelPage<T extends Person> extends PersonnelActionPage<T> {
    private final BooleanProperty personnelLoaded =
            new SimpleBooleanProperty(false);
    private IdInput idInput;
    private int loadedPersonnelId;

    protected EditPersonnelPage(
            Navigator navigator,
            Hospital hospital,
            String personnelName
    ) {
        super(navigator, hospital, personnelName);
    }

    @Override
    public final String getTitle() {
        return "Edit existing " + personnelNameLowerCase();
    }

    @Override
    protected final Node createBody() {
        Node form = createPersonnelForm();
        form.disableProperty().bind(personnelLoaded.not());

        Button save = createActionButton("Save changes");
        save.setDefaultButton(true);
        save.disableProperty().bind(
            personnelLoaded.not().or(formValidProperty().not())
        );
        save.setOnAction(event -> savePersonnel());

        idInput = new IdInput(
            personnelName(),
            "Load",
            this::loadPersonnel
        );
        idInput.textProperty().addListener(
            (observable, oldValue, newValue) -> personnelLoaded.set(false)
        );

        return createCenteredBox(
            22,
            idInput,
            new Separator(),
            form,
            save
        );
    }

    protected abstract Node createPersonnelForm();

    protected abstract BooleanExpression formValidProperty();

    protected abstract DataPacket<T> getPersonnel(int id);

    protected abstract void setFormPersonnel(T personnel);

    protected abstract GeneralPacket updatePersonnel(int id);

    private void loadPersonnel() {
        DataPacket<T> packet = getPersonnel(idInput.getPersonnelId());
        if (showApiError(packet)) {
            personnelLoaded.set(false);
            return;
        }

        loadedPersonnelId = packet.data.getId();
        setFormPersonnel(packet.data);
        personnelLoaded.set(true);
    }

    private void savePersonnel() {
        GeneralPacket packet = updatePersonnel(loadedPersonnelId);
        if (!showApiError(packet)) {
            complete(
                personnelName() + " " + loadedPersonnelId + " was updated."
            );
        }
    }
}
