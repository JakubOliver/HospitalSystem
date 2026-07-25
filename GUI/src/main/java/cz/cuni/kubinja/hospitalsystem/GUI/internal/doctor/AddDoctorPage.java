package cz.cuni.kubinja.hospitalsystem.GUI.internal.doctor;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.ActionPage;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Page for creating a doctor.
 */
final class AddDoctorPage extends ActionPage {
    AddDoctorPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "Add new doctor";
    }

    @Override
    protected Node createBody() {
        DoctorForm form = new DoctorForm();
        Button save = new Button("Save doctor");

        save.setMinHeight(42);
        save.setPrefWidth(180);
        save.setDefaultButton(true);
        save.disableProperty().bind(form.validProperty().not());

        save.setOnAction(event -> {
            DataPacket<Doctor> packet = hospital.addDoctor(form.getDoctorData());
            if (!showApiError(packet)) {
                complete("Doctor was added with ID " + packet.data.getId() + ".");
            }
        });

        VBox body = new VBox(22, form, save);
        body.setAlignment(Pos.TOP_CENTER);
        return body;
    }
}
