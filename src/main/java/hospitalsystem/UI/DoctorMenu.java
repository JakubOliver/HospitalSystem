package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PersonData;

import java.util.List;
import java.util.Scanner;

/**
 * Menu page containing options connected with doctors.
 */
public class DoctorMenu extends Menu implements PersonnelMenu {
    /**
     * Creates doctor menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public DoctorMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Add new doctor", this::add);
        addOption("Edit existing doctor", this::edit);
        addOption("Delete existing doctor", this::delete);
        addOption("Find doctor by ID", this::findById);
        addOption("Show all doctors", this::all);
        addOption("Back", this::end);
    }

    /**
     * Processes input data and calls for creating of new doctor in hospital system.
     */
    @Override
    public void add(){
        PersonData personData = getPersonData(scanner);

        String specialization = getString(scanner, "Specialization: ");
        String department = getString(scanner, "Department: ");

        DoctorData  doctorData = new DoctorData(
                personData,
                new DoctorDetails(specialization, department)
        );

        GeneralPacket packet = api.addDoctor(doctorData);

        processPacketStatus(scanner, packet);
    }

    @Override
    public void edit(){
        int id = getInteger(scanner, "ID: ");

        DataPacket<Doctor> packet = api.getDoctor(id);

        if (!processPacketStatusInSilence(scanner, packet)) return;

        Doctor doctor = packet.data;

        PersonData personData = getPersonData(scanner, doctor);

        String  specialization = getString(
                scanner,
                getQuestion("Specialization", doctor.getSpecialization()),
                doctor.getSpecialization()
        );

        String department = getString(
                scanner,
                getQuestion("Department", doctor.getDepartment()),
                doctor.getDepartment()
        );

        Doctor updateDoctor = new Doctor(
                new Person(doctor.getId(), personData),
                new DoctorDetails(specialization, department)
        );

        GeneralPacket response = api.updateDoctor(updateDoctor);

        processPacketStatus(scanner, response);
    }

    @Override
    public void delete(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.deleteDoctor(id);

        processPacketStatus(scanner, packet);
    }

    @Override
    public void findById(){
        int id = getInteger(scanner, "ID: ");

        DataPacket<Doctor> packet = api.getDoctor(id);

        if (!processPacketStatusInSilence(scanner, packet)) return;

        System.out.print(packet.data);

        waitForEnter(scanner);
    }

    @Override
    public void all(){
        DataPacket<List<String>> packet = api.allDoctors();

        if (!processPacketStatusInSilence(scanner, packet)) return;

        for (String text : packet.data) {
            System.out.print(text);
        }

        waitForEnter(scanner);
    }
}
