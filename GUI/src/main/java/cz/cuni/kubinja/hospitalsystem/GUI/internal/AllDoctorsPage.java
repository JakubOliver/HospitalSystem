package cz.cuni.kubinja.hospitalsystem.GUI.internal;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Page displaying all doctors in a table.
 */
final class AllDoctorsPage extends PersonnelTablePage<Doctor> {
    AllDoctorsPage(Navigator navigator, Hospital hospital) {
        super(navigator, hospital);
    }

    @Override
    public String getTitle() {
        return "All doctors";
    }

    @Override
    protected String getEmptyMessage() {
        return "No doctors found.";
    }

    @Override
    protected void addSpecificColumns(TableView<Doctor> table) {
        TableColumn<Doctor, String> specialization = new TableColumn<>("Specialization");
        specialization.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getSpecialization())
        );

        TableColumn<Doctor, String> department = new TableColumn<>("Department");
        department.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getDepartment())
        );

        table.getColumns().addAll(List.of(specialization, department));
    }

    @Override
    protected DataPacket<List<Doctor>> getAllPersonnel() {
        return hospital.allDoctors();
    }
}
