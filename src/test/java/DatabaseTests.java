import cz.cuni.kubinja.hospitalsystem.UI.PatientMenu;
import cz.cuni.kubinja.hospitalsystem.calendar.Appointment;
import cz.cuni.kubinja.hospitalsystem.calendar.CalendarException;
import cz.cuni.kubinja.hospitalsystem.calendar.util.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.database.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.personnel.Person;
import cz.cuni.kubinja.hospitalsystem.personnel.util.PersonKinds;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        DataPacket<List<Patient>> packet = api.allPatients();

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

        DataPacket<List<Doctor>> packet = api.allDoctors();
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
    void addAppointmentAcrossMultipleDays(){
        addPatient();
        addDoctor();

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.parse("2026-01-01T12:00"),
                LocalDateTime.parse("2026-03-01T16:00")
        ));

        assertFalse(packet.successful);
        assertEquals(CalendarException.notSameDay, packet.error);
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

        DataPacket<List<String>> packet = api.getCalendarRepresentation(false);

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
    void listDoctorsAppointments(){
        List<String> appointments = new ArrayList<>();

        addDoctor();
        for (int i = 0; i < 10; i++){
            addPatient();

            GeneralPacket packet = api.addAppointment(new AppointmentData(
                    i + 2,
                    1,
                    LocalDateTime.of(2026, i + 1, 10, 10, 0),
                    LocalDateTime.of(2026, i + 1, 10, 14, 30)
            ));
            assertTrue(packet.successful);

            DataPacket<Appointment> response = api.getAppointment(i + 1);
            assertTrue(response.successful);
            assertNotNull(response.data);
            appointments.add(response.data.toString());
        }

        DataPacket<List<String>> appointmentsPacket = api.getAppointmentsForPersonnel(1, PersonKinds.Doctor);

        assertTrue(appointmentsPacket.successful);
        assertNotNull(appointmentsPacket.data);

        for (String appointment : appointments){
            assertTrue(appointmentsPacket.data.contains(appointment));
        }
    }

    @Test
    void listPatientInvalidType(){
        addDoctor();
        DataPacket<List<String>> packet = api.getAppointmentsForPersonnel(1, PersonKinds.Patient);

        assertFalse(packet.successful);
        assertNotNull(packet.error);
        assertTrue(packet.error.contains(MessageFormat.format(DatabaseException.invalidTypeOfPersonDatabaseError, 1, Patient.getClassIdentifier())));
    }

    @Test
    void appointmentWithTwoPatients(){
        api.addPatient(samplePatientData);
        api.addPatient(samplePatientData);

        GeneralPacket packet = api.addAppointment(new AppointmentData(
                1,
                2,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 14, 30)
        ));

        assertFalse(packet.successful);
        assertNotNull(packet.error);
        assertTrue(packet.error.contains(MessageFormat.format(DatabaseException.invalidTypeOfPersonDatabaseError, 2, Doctor.getClassIdentifier())));
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

            DataPacket<List<String>> packet3 = api.getCalendarRepresentation(false);
            assertFalse(packet3.successful);
        } catch (SQLException _){}
    }

    @Test
    void updatePatient(){
        GeneralPacket packet = api.addPatient(samplePatientData);
        assertTrue(packet.successful);

        DataPacket<Patient> packet2 = api.getPatient(1);
        assertTrue(packet2.successful);
        assertNotNull(packet2.data);

        Patient newPatient = new Patient(
                packet2.data.getId(),
                "Lukas",
                "Sedlak",
                packet2.data.getDateOfBirth(),
                "Pack pain"
        );

        GeneralPacket packet3 = api.updatePatient(newPatient);
        assertTrue(packet3.successful);

        DataPacket<Patient> packet4 = api.getPatient(1);
        assertTrue(packet4.successful);
        assertNotNull(packet4.data);

        assertEquals(newPatient.toString(), packet4.data.toString());
    }

    @Test
    void updatePatientInvalid(){
        Patient newPatient = new Patient(
                1,
                "Lukas",
                "Sedlak",
                LocalDate.of(1970, 1, 1),
                "Pack pain"
        );

        GeneralPacket packet = api.updatePatient(newPatient);
        assertFalse(packet.successful);
        assertNotNull(packet.error);
        assertTrue(packet.error.contains("Id does not exist!"));

        DataPacket<Patient> packet2 = api.getPatient(1);
        assertFalse(packet2.successful);
        assertNotNull(packet2.error);
        assertTrue(packet2.error.contains("Id does not exist!"));
    }

    @Test
    void updateDoctorInvalid(){
        Doctor doctor = new Doctor(
                new Person(1, samplePersonData),
                sampleDoctorDetails
        );

        GeneralPacket packet = api.addPatient(samplePatientData);
        assertTrue(packet.successful);

        GeneralPacket response = api.updateDoctor(doctor);
        assertFalse(response.successful);
        assertNotNull(response.error);
        assertTrue(response.error.contains(MessageFormat.format(DatabaseException.invalidTypeOfPersonDatabaseError, 1, Doctor.getClassIdentifier())));
    }

    @Test
    void updateAppointmentInvalid(){
        GeneralPacket packet = api.addPatient(samplePatientData);
        assertTrue(packet.successful);

        GeneralPacket packet2 = api.addDoctor(sampleDoctorData);
        assertTrue(packet2.successful);

        GeneralPacket packet3 = api.addAppointment(sampleAppointmentData);
        assertTrue(packet3.successful);

        Appointment newAppointment = new Appointment(
                1,
                new Patient(new Person(1, samplePersonData), samplePatientDetails),
                new Doctor(new Person(40, samplePersonData), sampleDoctorDetails),
                LocalDateTime.of(2001, 1, 1, 10, 0),
                LocalDateTime.of(2001, 1, 1, 11, 0)
        );

        GeneralPacket packet4 = api.updateAppointment(newAppointment);
        assertFalse(packet4.successful);
        assertNotNull(packet4.error);
        assertTrue(packet4.error.contains("Id does not exist!"), () -> packet4.error);
    }

    @Test
    void deletePatientByUsingDeleteDoctor(){
        GeneralPacket packet = api.addPatient(samplePatientData);
        assertTrue(packet.successful);

        GeneralPacket packet2 = api.deleteDoctor(1);
        assertFalse(packet2.successful);
        assertNotNull(packet2.error);
        assertTrue(packet2.error.contains(MessageFormat.format(DatabaseException.invalidTypeOfPersonDatabaseError, 1, Doctor.getClassIdentifier())));

        DataPacket<Patient> packet3 = api.getPatient(1);
        assertTrue(packet3.successful);
        assertNotNull(packet3.data);
        assertEquals(new Patient(new Person(1, samplePersonData), samplePatientDetails).toString(), packet3.data.toString());
    }
}
