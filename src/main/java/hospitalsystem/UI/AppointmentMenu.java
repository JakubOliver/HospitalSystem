package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.calendar.Appointment;
import hospitalsystem.calendar.util.AppointmentData;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu page containing options connected with appointments.
 */
public class AppointmentMenu extends Menu{

    /**
     * Creates appointment menu page.
     *
     * @param api Hospital giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    AppointmentMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Add new appointment", this::addAppointment);
        addOption("Edit appointment", this::editAppointment);
        addOption("Delete appointment", this::deleteAppointment);
        addOption("Show all appointments for patient", () -> showAllForPersonnel(PersonKinds.Patient));
        addOption("Show all appointments for doctor", () -> showAllForPersonnel(PersonKinds.Doctor));
        addOption("Show calendar for department", this::showDepartmentCalendar);
        addOption("List appointments", this::showCalendar);
        addOption("Back", this::end);
    }

    private int selectPerson(List<? extends Person> people){
        if (people.size() > 1) {
            for (int i = 0; i < people.size(); i++) {
                System.out.println((i + 1) + ". " + people.get(i).toString());
            }

            return people.get(getOption(people.size())).getId();
        }

        return people.getFirst().getId();
    }

    private Optional<AppointmentData> getAppointmentData(){
        int patientsId;
        if (createNew(Patient.getClassIdentifier())){
            PersonData personData = getPersonData();
            PatientsDetails patientDetails = getPatientDetails();

            DataPacket<Patient> packet = api.addPatient(new PatientData(personData, patientDetails));

            if(!processPacketStatusInSilence(packet)) return Optional.empty();

            patientsId = packet.data.getId();
        } else {
            boolean byId = getBool("Find patient by id?");

            if (byId) {
                patientsId = getInteger("Patient's ID: ");
            } else {
                String patientFistName = getString("Firstname: ");
                String patientLastName = getString("Lastname: ");

                DataPacket<List<Patient>> patients = api.getAllPatientWithName(patientFistName, patientLastName);

                if (!processPacketStatusInSilence(patients)) return Optional.empty();

                if (patients.data.isEmpty()) {
                    printAndWait("No patient with that name");
                    return Optional.empty();
                }

                patientsId = selectPerson(patients.data);
            }
        }

        int doctorsId;
        if (createNew(Doctor.getClassIdentifier())) {
            PersonData personData = getPersonData();
            DoctorDetails patientDetails = getDoctorDetails();

            DataPacket<Doctor> packet = api.addDoctor(new DoctorData(personData, patientDetails));

            if(!processPacketStatusInSilence(packet)) return Optional.empty();

            doctorsId = packet.data.getId();
        } else {
            boolean byId = getBool("Find doctor by id?");

            if (byId) {
                doctorsId = getInteger("Doctor's ID: ");
            } else {
                String patientFistName = getString("Firstname: ");
                String patientLastName = getString("Lastname: ");

                DataPacket<List<Doctor>> doctors = api.getAllDoctorsWithName(patientFistName, patientLastName);

                if (!processPacketStatusInSilence(doctors)) return Optional.empty();

                if (doctors.data.isEmpty()) {
                    printAndWait("No doctor with that name");
                    return Optional.empty();
                }

                doctorsId = selectPerson(doctors.data);
            }
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

    /**
     * Processes input data from scanner and deletes appointment with the provided ID.
     */
    public void deleteAppointment(){
        int id = getInteger("Appointment ID: ");

        GeneralPacket packet = api.deleteAppointment(id);

        processPacketStatus(packet);
    }

    /**
     * Processes input data from scanner and shows appointment for the personnel with the provided ID and kind.
     *
     * @param kind Kind of personnel (Patient or Doctor).
     */
    public void showAllForPersonnel(PersonKinds kind){
        DataPacket<List<String>> packet = switch(kind){
            case Patient -> {
                int id = getInteger("Patient ID: ");
                yield api.getAppointmentsForPersonnel(id, PersonKinds.Patient);
            }
            case Doctor -> {
                int id = getInteger("Doctor's ID: ");
                yield api.getAppointmentsForPersonnel(id, PersonKinds.Doctor);
            }
        };

        if (!processPacketStatusInSilence(packet)) return;

        packet.data.forEach(System.out::println);

        waitForEnter();
    }

    //TODO: calendar per week
    //TODO: showCalender from today

    /**
     * Processes input data from scanner and based on provided department shows calendar.
     */
    public void showDepartmentCalendar(){
        String department = getString(getQuestion("Department name"));
        boolean fromToday = getBool(getQuestion("From Today"));

        DataPacket<String> packet = api.getCalendarForDepartment(department, fromToday);

        if (!processPacketStatusInSilence(packet)) return;

        printAndWait(packet.data);
    }

    /**
     * Prints whole calendar for the hospital.
     */
    public void showCalendar(){
        boolean fromToday = getBool(getQuestion("From Today"));
        DataPacket<List<String>> packet = api.getCalendarRepresentation(fromToday);

        if (!processPacketStatusInSilence(packet)) return;

        packet.data.forEach(System.out::println);
        waitForEnter();
    }
}
