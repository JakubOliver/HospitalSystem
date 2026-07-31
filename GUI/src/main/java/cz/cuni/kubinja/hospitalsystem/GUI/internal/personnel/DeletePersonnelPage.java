package cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Shared workflow for loading and deleting a patient or doctor.
 *
 * @param <T> Type of personnel being deleted.
 */
public abstract class DeletePersonnelPage<T extends Person> extends PersonnelActionPage<T> {
    private VBox details;
    private Button delete;
    private T loadedPersonnel;
    private IdInput idInput;

    protected DeletePersonnelPage(
            Navigator navigator,
            Hospital hospital,
            String personnelName
    ) {
        super(navigator, hospital, personnelName);
    }

    @Override
    public final String getTitle() {
        return "Delete existing " + personnelNameLowerCase();
    }

    @Override
    protected final Node createBody() {
        details = createCenteredBox(0);

        delete = createActionButton(
            "Delete " + personnelNameLowerCase()
        );
        delete.setDisable(true);
        ActionPage.applyErrorTextStyle(delete);
        delete.setOnAction(event -> confirmDelete());

        idInput = new IdInput(
            personnelName(),
            "Load",
            this::loadPersonnel
        );
        idInput.textProperty().addListener(
            (observable, oldValue, newValue) -> clearPersonnel()
        );

        return createCenteredBox(
            22,
            idInput,
            new Separator(),
            details,
            delete
        );
    }

    protected abstract DataPacket<T> getPersonnel(int id);

    protected abstract GeneralPacket deletePersonnel(int id);

    protected abstract ActionPage.Detail[] additionalDetails(T personnel);

    private void loadPersonnel() {
        DataPacket<T> packet = getPersonnel(idInput.getPersonnelId());
        if (showApiError(packet)) {
            clearPersonnel();
            return;
        }

        loadedPersonnel = packet.data;
        details.getChildren().setAll(
            personnelDetails(
                loadedPersonnel,
                additionalDetails(loadedPersonnel)
            )
        );
        delete.setDisable(false);
    }

    private void clearPersonnel() {
        loadedPersonnel = null;
        if (details != null) {
            details.getChildren().clear();
        }
        if (delete != null) {
            delete.setDisable(true);
        }
    }

    private void confirmDelete() {
        if (!confirmAction(
            "Delete " + personnelNameLowerCase(),
            "Delete " + personnelNameLowerCase() + " "
                + loadedPersonnel.getId() + "?",
            loadedPersonnel.getFirstName() + " "
                + loadedPersonnel.getLastName()
                + "\nThis action cannot be undone."
        )) {
            return;
        }

        GeneralPacket packet = deletePersonnel(loadedPersonnel.getId());
        if (!showApiError(packet)) {
            complete(
                personnelName() + " " + loadedPersonnel.getId()
                    + " was deleted."
            );
        }
    }
}
