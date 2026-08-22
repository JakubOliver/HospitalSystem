package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.beans.binding.BooleanExpression;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Shared workflow for creating a patient or doctor.
 *
 * @param <T> Type of personnel being created.
 */
public abstract class AddPersonnelPage<T extends Person> extends PersonnelActionPage<T> {
    protected AddPersonnelPage(
            Navigator navigator,
            Hospital hospital,
            String personnelName
    ) {
        super(navigator, hospital, personnelName);
    }

    @Override
    public final String getTitle() {
        return "Add new " + personnelNameLowerCase();
    }

    @Override
    protected final Node createBody() {
        Node form = createPersonnelForm();

        Button save = createActionButton(
            "Save " + personnelNameLowerCase()
        );

        save.setId("save-personnel");
        save.setDefaultButton(true);
        save.disableProperty().bind(formValidProperty().not());
        save.setOnAction(event -> savePersonnel());

        return createCenteredBox(22, form, save);
    }

    protected abstract Node createPersonnelForm();

    protected abstract BooleanExpression formValidProperty();

    protected abstract DataPacket<T> addPersonnel();

    private void savePersonnel() {
        DataPacket<T> packet = addPersonnel();

        if (!showApiError(packet)) {
            complete(
                personnelName() + " was added with ID "
                    + packet.data.getId() + "."
            );
        }
    }
}
