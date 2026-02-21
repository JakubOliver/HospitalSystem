import hospitalsystem.UI.DoctorMenu;
import hospitalsystem.UI.ExportMenu;
import hospitalsystem.UI.MainMenu;
import hospitalsystem.UI.PatientMenu;
import hospitalsystem.database.DatabaseException;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.util.ExportsUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class UITests extends TestsUsingDatabase {
    ByteArrayOutputStream output;

    @BeforeAll
    static void output(){}

    @BeforeEach
    public void setOutput() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @Test
    public void showPatient() {
        api.addPatient(samplePatientData);

        Scanner scanner = getPrebuildInput("        4        \n 1        \n enter \n 6 ");
        new PatientMenu(api, scanner);

        DataPacket<Patient> packet = api.getPatient(1);

        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertTrue(output.toString().contains(packet.data.toString()));
    }

    @Test
    public void showDoctor() {
        api.addDoctor(sampleDoctorData);

        Scanner scanner = getPrebuildInput("        4 \n  1        \n enter \n 6 ");
        new DoctorMenu(api, scanner);

        DataPacket<Doctor> packet = api.getDoctor(1);

        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertTrue(output.toString().contains(packet.data.toString()));
    }

    @Test
    public void showDoctorNotDoctor() {
        api.addPatient(samplePatientData);

        Scanner scanner = getPrebuildInput("        4 \n  1        \n enter \n 6 ");
        new DoctorMenu(api, scanner);

        assertTrue(output.toString().contains("Error: " + DatabaseException.doctorGetDatabaseError + ": Person with id " + 1 + " is not " + "doctor"));
    }

    @Test
    public void deleteDoctor() {
        api.addDoctor(sampleDoctorData);

        Scanner scanner = getPrebuildInput("        3 \n  1        \n enter \n 6 ");
        new DoctorMenu(api, scanner);

        DataPacket<Doctor> packet = api.getDoctor(1);
        assertFalse(packet.successful);
        assertTrue(output.toString().contains(GeneralPacket.Msg.successfulPacket));
    }

    @Test
    public void showAllDoctors(){
        List<Doctor> doctors = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            api.addDoctor(sampleDoctorData);
            DataPacket<Doctor> packet = api.getDoctor(i + 1);

            assertTrue(packet.successful);
            doctors.add(packet.data);
            doctors.add(packet.data);
        }

        Scanner scanner = getPrebuildInput("        5 \n enter \n 6 ");
        new DoctorMenu(api, scanner);

        String outputString = output.toString();

        for (Doctor doctor : doctors){
            assertTrue(outputString.contains(doctor.toString()));
        }
    }

    @Test
    public void traversalAcrossMenus(){
        Scanner scanner = getPrebuildInput("1\n6\n2\n6\n3\n8\n4\n5\n5\n");
        new MainMenu(api, scanner);

        String outputString = output.toString();
        assertTrue(outputString.contains("Delete existing patient"));
        assertTrue(outputString.contains("Show all doctors"));
        assertTrue(outputString.contains("Edit appointment"));
        assertTrue(outputString.contains("Export all"));
        assertTrue(outputString.contains("Back"));
        assertTrue(outputString.contains("End"));
    }

    @Test
    void exports(){
        for (int i = 0; i < 10; i++){
            assertTrue(api.addPatient(samplePatientData).successful);
            assertTrue(api.addDoctor(sampleDoctorData).successful);
            assertTrue(api.addAppointment(sampleAppointmentData).successful);
        }

        Scanner scanner = getPrebuildInput("4    \n enter \n 5");
        new ExportMenu(api, scanner);

        File directory = new File(ExportsUtil.exportDirectoryDestination);
        assertTrue(directory.exists() && directory.isDirectory());

        File patients = new File(directory, ExportsUtil.patientExportDestination);
        assertTrue(patients.exists());
        assertTrue(patients.lastModified() > (System.currentTimeMillis() - 100_000)); //Checks whether the file modified in last 100 seconds.

        File doctors = new File(directory, ExportsUtil.doctorExportDestination);
        assertTrue(doctors.exists());
        assertTrue(doctors.lastModified() > (System.currentTimeMillis() - 100_000));

        File appointments =  new File(directory, ExportsUtil.appointmentExportDestination);
        assertTrue(appointments.exists());
        assertTrue(appointments.lastModified() > (System.currentTimeMillis() - 100_000));
    }
}
