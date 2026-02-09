package hospitalsystem;

import hospitalsystem.UI.MainMenu;
import hospitalsystem.calendar.CalendarEntry;
import hospitalsystem.calendar.util.CalendarEntryData;
import hospitalsystem.database.Database;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.util.HospitalAPI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Hospital {
    private final Database database;

    public Hospital(String databasePath){
        database = new Database(databasePath);

        MainMenu mainMenu = new MainMenu(new HospitalAPI(this));
    }

    public void addPatient(PatientData patientData) {
        //TODO: validate

        try {
            database.addPatient(patientData.person().firstName(), patientData.person().lastName(), LocalDate.parse(patientData.person().dateOfBirth()), patientData.details().anamnesis()); //TODO: rozbalovani az na urovni databaze

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getPatientInfo(int id){
        try{
            return database.getPatient(id).toString();
        } catch (DatabaseException e){
            return "Error: " + e.getMessage();
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

    public void addAppointment(CalendarEntryData calendarEntryData) {
        //TODO: validate

        try {
            database.addAppointment(
                    calendarEntryData.patientsId(),
                    calendarEntryData.doctorsId(),
                    calendarEntryData.starTime(),
                    calendarEntryData.endTime()
            );

            System.out.println("Success!");
        } catch (DatabaseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
