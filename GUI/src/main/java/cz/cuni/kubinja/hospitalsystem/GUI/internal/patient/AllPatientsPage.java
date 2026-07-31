package cz.cuni.kubinja.hospitalsystem.GUI.internal.patient;

import cz.cuni.kubinja.hospitalsystem.GUI.internal.Navigator;
import cz.cuni.kubinja.hospitalsystem.GUI.internal.personnel.PersonnelTablePage;
import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;

/**
 * Page displaying all patients in a table.
 */
final class AllPatientsPage extends PersonnelTablePage<Patient> {
    AllPatientsPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "All patients";
    }

    @Override
    protected String getEmptyMessage() {
        return "No patients found.";
    }

    @Override
    protected void addSpecificColumns(TableView<Patient> table) {
        TableColumn<Patient, String> anamnesis = new TableColumn<>("Anamnesis");
        anamnesis.setCellValueFactory(
            cell -> new SimpleStringProperty(cell.getValue().getAnamnesis())
        );
        table.getColumns().add(anamnesis);
    }

    @Override
    protected DataPacket<List<Patient>> getAllPersonnel() {
        return hospital.allPatients();
    }
}
