package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.IdInput;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Page for finding and displaying a doctor by ID.
 */
final class FindDoctorPage extends ActionPage {
    private IdInput idInput;
    private VBox details;

    FindDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Find doctor by ID";
    }

    @Override
    protected Node createBody() {
        details = new VBox();
        details.setAlignment(Pos.TOP_CENTER);

        idInput = new IdInput("Doctor", "Find", this::findDoctor);
        idInput.textProperty().addListener(
                (observable, oldValue, newValue) -> details.getChildren().clear()
        );

        VBox body = new VBox(22, idInput, new Separator(), details);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }

    private void findDoctor() {
        DataPacket<Doctor> packet = hospital.getDoctor(idInput.getPersonnelId());
        if (showApiError(packet)) {
            details.getChildren().clear();
            return;
        }

        details.getChildren().setAll(personnelDetails(
                packet.data,
                new Detail("Specialization", packet.data.getSpecialization()),
                new Detail("Department", packet.data.getDepartment())
        ));
    }
}
