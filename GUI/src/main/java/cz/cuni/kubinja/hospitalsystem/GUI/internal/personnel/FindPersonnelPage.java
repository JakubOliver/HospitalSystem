package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Shared workflow for finding and displaying a patient or doctor.
 *
 * @param <T> Type of personnel being displayed.
 */
public abstract class FindPersonnelPage<T extends Person> extends PersonnelActionPage<T> {
    private VBox details;
    private IdInput idInput;

    protected FindPersonnelPage(
            Navigator navigator,
            Hospital hospital,
            String personnelName
    ) {
        super(navigator, hospital, personnelName);
    }

    @Override
    public final String getTitle() {
        return "Find " + personnelNameLowerCase() + " by ID";
    }

    @Override
    protected final Node createBody() {
        details = createCenteredBox(0);
        idInput = new IdInput(
            personnelName(),
            "Find",
            this::findPersonnel
        );
        idInput.textProperty().addListener(
            (observable, oldValue, newValue) -> details.getChildren().clear()
        );

        return createCenteredBox(22, idInput, new Separator(), details);
    }

    protected abstract DataPacket<T> getPersonnel(int id);

    protected abstract ActionPage.Detail[] additionalDetails(T personnel);

    private void findPersonnel() {
        DataPacket<T> packet = getPersonnel(idInput.getPersonnelId());
        if (showApiError(packet)) {
            details.getChildren().clear();
            return;
        }

        details.getChildren().setAll(
            personnelDetails(packet.data, additionalDetails(packet.data))
        );
    }
}
