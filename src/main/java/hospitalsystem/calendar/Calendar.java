package hospitalsystem.calendar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Calendar {
    private final Map<String, Department> departments;

    //TODO: validate times, isPatient, isDoctor etc.
    public Calendar() {
        departments = new HashMap<>();
    }

    public void importData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Appointment appointment = new Appointment(resultSet);

            if (!departments.containsKey(appointment.department)) {
                departments.put(appointment.department, new Department(appointment.department));
            }

            departments.get(appointment.department).addAppointment(appointment);
        }

        computeLayers();
    }

    private void computeLayers(){
        for (Department department : departments.values()) {
            department.computeLayers();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String department : departments.keySet()){
            sb.append(departments.get(department).toString());
        }

        return sb.toString();
    }

    public void export(File destination) throws IOException {
        //TODO: mozna export seradit podle id
        try (FileWriter writer = new FileWriter(destination)){
            for (Department department : departments.values()){
                for (Appointment appointment : department.appointments){
                    writer.write(appointment.export() + "\n");
                }
            }
        }
    }
}