package hospitalsystem;

import hospitalsystem.UI.MainMenu;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Hospital {
    private final Database database;

    public Hospital(String databasePath){
        database = new Database(databasePath);

        MainMenu mainMenu = new MainMenu(new HospitalAPI(this));

        //TODO: moc mi nedává smysl aby menu mělo databázi nebylo by loepší, kdyby mělo okdkaz na Hospital a metody by byli v hospital
    }

    public void addPatient(PatientData patientData) {
        //TODO: validate

        try {
            database.addPatient(patientData.person().firstName(), patientData.person().lastName(), LocalDate.parse(patientData.person().dateOfBirth()), patientData.details().anamnesis());

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void findAllPatient(){
        try{
            List<Patient> patients = database.getAllPatients();

            for (Patient patient : patients){
                System.out.println(patient);
            }
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addDoctor(DoctorData doctorData) {
        //TODO: validate
        try {
            database.addDoctor(doctorData.person().firstName(), doctorData.person().lastName(), LocalDate.parse(doctorData.person().dateOfBirth()), doctorData.details().specialization());

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addAppointment(){
        //TODO:

        try {
            database.addAppointment(
                    new Patient(5, "Kamil", "Dorazil", LocalDate.of(1999, 1, 1), "Broken leg"),
                    new Doctor(3, "Pepa", "Novak", LocalDate.of(1990, 2, 2), "Surgeon"),
                    LocalDateTime.of(2026,9,2,13,0),
                    LocalDateTime.of(2025,9,2,14,0));

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
