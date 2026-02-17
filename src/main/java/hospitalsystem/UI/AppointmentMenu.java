package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.util.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu page containing options connected with appointments.
 */
public class AppointmentMenu extends Menu{

    /**
     * Creates appointment menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    AppointmentMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Add new appointment", this::addAppointment);
        addOption("Edit appointment", this::editAppointment);
        addOption("Delete appointment", () -> {}); //TODO
        addOption("List appointments", this::showCalendar);
        addOption("Back", this::end);
    }

    //TODO: pridat update appointment z default hodnotama
    private Optional<AppointmentData> getAppointmentData(){
        int patientsId;
        if (createNew("patient")){
            PersonData personData = getPersonData();
            PatientsDetails patientDetails = getPatientDetails();

            DataPacket<Patient> packet = api.addPatient(new PatientData(personData, patientDetails));

            if(!processPacketStatusInSilence(packet)) return Optional.empty(); //TODO: mozna udelat metodu check person (dost se to opakuje)

            patientsId = packet.data.getId();
        } else {
            patientsId = getInteger("Patient's ID: ");
        }

        int doctorsId;
        if (createNew("doctor")){
            PersonData personData = getPersonData();
            DoctorDetails patientDetails = getDoctorDetails();

            DataPacket<Doctor> packet = api.addDoctor(new DoctorData(personData, patientDetails));

            if(!processPacketStatusInSilence(packet)) return Optional.empty();

            doctorsId = packet.data.getId();
        } else {
            doctorsId = getInteger("Doctor's ID: ");
        }

        LocalDateTime startTime = getDateTime("Start Time: ");
        LocalDateTime endTime = getDateTime("End Time: ");

        return Optional.of(new AppointmentData(
                patientsId,
                doctorsId,
                startTime,
                endTime
        ));
    }

    /**
     * Processes input data and calls for creating of new appointment in hospital system.
     */
    public void addAppointment(){
        //TODO: volba zda si vybrat (id nebo jmeno) nebo vytvořit noveho

        Optional<AppointmentData> appointmentData = getAppointmentData();

        if (appointmentData.isPresent()){
            GeneralPacket packet = api.addAppointment(appointmentData.get());

            processPacketStatus(packet);
        }
    }

    /**
     * Processes input data from scanner and edits appointment with the provided ID.
     */
    public void editAppointment(){
        int id = getInteger("Appointment ID: ");

        DataPacket<Appointment> packet = api.getAppointment(id);

        if (!processPacketStatusInSilence(packet)) return;

        Appointment appointment = packet.data;
        Optional<AppointmentData> appointmentData = getAppointmentData();

        if (appointmentData.isEmpty()) return;

        //TODO: pomoci genericType
        AppointmentData data = appointmentData.get();
        DataPacket<Doctor> doctorPacket = api.getDoctor(data.doctorsId());

        if (!processPacketStatusInSilence(doctorPacket)) return;

        DataPacket<Patient> patientPacket = api.getPatient(data.patientsId());

        if (!processPacketStatusInSilence(patientPacket)) return;

        GeneralPacket finalPacket = api.updateAppointment(new Appointment(
                appointment.id,
                patientPacket.data,
                doctorPacket.data,
                appointment.startTime,
                appointment.endTime
        ));

        processPacketStatus(finalPacket);
    }

    //TODO: calendar per deparmtne
    //TODO: calendar per week
    //TODO: showCalender from today

    /**
     * Prints whole calendar for the hospital.
     */
    public void showCalendar(){
        DataPacket<String> packet = api.showCalendar();

        if (!processPacketStatusInSilence(packet)) return;

        printAndWait(packet.data); //TODO: vymyslet trošku lepší způsub mozna aby i kalendář vypisovat, poněvadž mi přijde zvlášní, že se to ukládá to jednoho stringu, ale zas to v calendar je také
    }
}
