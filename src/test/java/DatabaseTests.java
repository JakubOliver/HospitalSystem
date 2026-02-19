import hospitalsystem.UI.PatientMenu;
import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.CalendarException;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.*;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests extends TestsUsingDatabase{
    @Test
    void addPatientCMD() {
        Scanner patientsScanner = getPrebuildInput("1 \n Pepa \n Novak \n 2001-01-01 \n Broken leg \n enter \n 6");

        new PatientMenu(api, patientsScanner);
    }

    @Test
    void addPatient() {
        GeneralPacket packet = api.addPatient(samplePatientData);

        assertTrue(packet.successful);
    }

    @Test
    void getPatient(){
        addPatient();

        DataPacket<Patient> packet = api.getPatient(1);

        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertEquals(new Patient(new Person(1, samplePersonData), samplePatientDetails).toString(), packet.data.toString());
    }

    @Test
    void getInvalidPatient(){
        DataPacket<Patient> packet = api.getPatient(1);

        assertFalse(packet.successful);
        assertNull(packet.data);
    }

    @Test
    void deletePatient(){
        addPatient();

        GeneralPacket packet = api.deletePatient(1);
        assertTrue(packet.successful);

        GeneralPacket packet2 = api.getPatient(1);
        assertFalse(packet2.successful); //Patient is not in database, therefore the query will not be successful.
    }

    @Test
    void getAllPatients(){
        for (int i = 0; i < 10; i++){
            addPatient();
        }

        DataPacket<List<String>> packet = api.allPatients();

        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertEquals(10, packet.data.size());
    }

    @Test
    void addDoctor(){
        GeneralPacket packet = api.addDoctor(sampleDoctorData);

        assertTrue(packet.successful);
    }

    @Test
    void getDoctor(){
        addDoctor();

        DataPacket<Doctor> packet = api.getDoctor(1);

        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertEquals(new Doctor(new Person(1, samplePersonData), sampleDoctorDetails).toString(), packet.data.toString());
    }

    @Test
    void deleteDoctor(){
        addDoctor();

        GeneralPacket packet = api.deleteDoctor(1);
        assertTrue(packet.successful);

        GeneralPacket packet2 = api.getDoctor(1);
        assertFalse(packet2.successful); //Patient is not in database, therefore the query will not be successful.
    }

    @Test
    void getAllDoctors(){
        for (int i = 0; i < 10; i++){
            addDoctor();
        }

        DataPacket<List<String>> packet = api.allDoctors();
        assertTrue(packet.successful);
        assertNotNull(packet.data);
        assertEquals(10, packet.data.size());
    }

    @Test
    void addAppointment(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(sampleAppointmentData);

        assertTrue(packet.successful);
    }

    @Test
    void addAppointmentInvalidLength(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(12).withMinute(30).withSecond(0).withNano(0)
        ));

        assertFalse(packet.successful);
        assertEquals(CalendarException.toShort, packet.error);
    }

    @Test
    void addAppointmentInvalidAlignment(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(12).withMinute(12).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(13).withMinute(45).withSecond(0).withNano(0)
        ));

        assertFalse(packet.successful);
        assertEquals(CalendarException.invalidAlignment, packet.error);
    }

    @Test
    void addAppointmentInvalidOrdering(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(14).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0)
        ));

        assertFalse(packet.successful);
        assertEquals(CalendarException.invalidOrdering, packet.error);
    }

    @Test
    void addAppointmentInvalidTimes(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(6).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0)
        ));

        assertFalse(packet.successful);
        assertEquals(CalendarException.invalidTimes, packet.error);
    }

    @Test
    void addAppointmentCollisionWithDoctor(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(10).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0)
        ));
        assertTrue(packet.successful);

        addPatient();

        GeneralPacket packet2 = api.addAppointment(new AppointmentData(
                3,
                2,
                LocalDateTime.now().withHour(11).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(15).withMinute(0).withSecond(0).withNano(0)
        ));

        assertFalse(packet2.successful);
        assertEquals(CalendarException.timeCollisionWIthDoctor, packet2.error);
    }

    @Test
    void addAppointmentCollisionWithPatient(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.now().withHour(10).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0)
        ));
        assertTrue(packet.successful);

        addDoctor();

        GeneralPacket packet2 = api.addAppointment(new AppointmentData(
                1,
                3,
                LocalDateTime.now().withHour(11).withMinute(30).withSecond(0).withNano(0),
                LocalDateTime.now().withHour(15).withMinute(0).withSecond(0).withNano(0)
        ));

        assertFalse(packet2.successful);
        assertEquals(CalendarException.timeCollisionWithPatient, packet2.error);
    }

    @Test
    void getAppointment(){
        addPatient();
        addDoctor();
        addAppointment();

        DataPacket<Patient> patientPacket = api.getPatient(1);
        assertTrue(patientPacket.successful);
        assertNotNull(patientPacket.data);

        DataPacket<Doctor> doctorPacket = api.getDoctor(2);
        assertTrue(doctorPacket.successful);
        assertNotNull(doctorPacket.data);

        DataPacket<Appointment> packet = api.getAppointment(1);
        assertTrue(packet.successful);

        Appointment appointment = new Appointment(
                1,
                patientPacket.data,
                doctorPacket.data,
                sampleAppointmentData.starTime(),
                sampleAppointmentData.endTime()
        );

        assertNotNull(packet.data);
        assertEquals(appointment.export(), packet.data.export());
    }

    @Test
    void getCalendar(){
        addCombination();

        DataPacket<String> packet = api.showCalendar();

        assertTrue(packet.successful);
        assertNotNull(packet.data);
    }

    @Test
    void deleteAppointment(){
        addAppointment();

        DataPacket<Appointment> packet = api.getAppointment(1);
        assertTrue(packet.successful);

        GeneralPacket packet2 = api.deleteAppointment(1);
        assertTrue(packet2.successful);

        DataPacket<Appointment> packet3 = api.getAppointment(1);
        assertFalse(packet3.successful);
    }

    @Test
    void updateAppointment(){
        addAppointment();

        DataPacket<Appointment> packet = api.getAppointment(1);
        assertTrue(packet.successful);
        assertNotNull(packet.data);

        Appointment newAppointment = new Appointment(
                packet.data.id,
                packet.data.patientId,
                packet.data.doctorId,
                packet.data.department,
                LocalDateTime.parse("2026-02-10T10:30"),
                LocalDateTime.parse("2026-02-10T14:00")
        );

        GeneralPacket packet2 = api.updateAppointment(newAppointment);
        assertTrue(packet2.successful);

        DataPacket<Appointment> packet3 = api.getAppointment(1);
        assertTrue(packet3.successful);
        assertNotNull(packet3.data);
        assertEquals(newAppointment.export(), packet3.data.export());
    }

    @Test
    void addCombination(){
        for (int i = 0; i < 10; i++){
            addPatient();
            addDoctor();

            DataPacket<Patient> patientPacket = api.getPatient(2*i + 1);
            assertTrue(patientPacket.successful);
            assertNotNull(patientPacket.data);

            DataPacket<Doctor> doctorPacket = api.getDoctor(2*(i+1));
            assertTrue(doctorPacket.successful);
            assertNotNull(doctorPacket.data);

            GeneralPacket appointmentPacket = api.addAppointment(new AppointmentData(
                    patientPacket.data.getId(),
                    doctorPacket.data.getId(),
                    LocalDateTime.of(2026, 2, 10, 10, 0),
                    LocalDateTime.of(2026, 2, 10, 14, 30)
            ));

            assertTrue(appointmentPacket.successful);
        }
    }

    @Test
    void blankExports(){
        addCombination();

        GeneralPacket packet = api.export();
        assertTrue(packet.successful);
    }

    @Test
    void blankPatientExports(){
        addCombination();

        GeneralPacket packet = api.exportPatients();
        assertTrue(packet.successful);
    }

    @Test
    void blankDoctorExports(){
        addCombination();

        GeneralPacket packet = api.exportDoctors();
        assertTrue(packet.successful);
    }

    @Test
    void blankExportsAppointments(){
        addCombination();

        GeneralPacket packet = api.exportAppointments();
        assertTrue(packet.successful);
    }

    @Test
    void lockedDatabase(){
        addDoctor();

        try (Connection connection = DriverManager.getConnection(testDB); Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            statement.execute("BEGIN EXCLUSIVE");

            GeneralPacket packet = api.addPatient(samplePatientData);
            assertFalse(packet.successful);

            DataPacket<Doctor> packet2 = api.getDoctor(1);
            assertFalse(packet2.successful);

            DataPacket<String> packet3 = api.showCalendar();
            assertFalse(packet3.successful);
        } catch (SQLException _){}
    }
}
