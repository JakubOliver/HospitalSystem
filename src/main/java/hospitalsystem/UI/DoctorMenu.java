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
        PersonData personData = getPersonData();

        String specialization = getString("Specialization: ");
        String department = getString("Department: ");

        DoctorData  doctorData = new DoctorData(
                personData,
                new DoctorDetails(specialization, department)
        );

        GeneralPacket packet = api.addDoctor(doctorData);

        processPacketStatus(packet);
    }

    /**
     * Processes input data and edits doctor with the provided ID.
     */
    @Override
    public void edit(){
        int id = getInteger("ID: ");

        DataPacket<Doctor> packet = api.getDoctor(id);

        if (!processPacketStatusInSilence(packet)) return;

        Doctor doctor = packet.data;

        PersonData personData = getPersonData(doctor);

        String  specialization = getString(
                getQuestion("Specialization", doctor.getSpecialization()),
                doctor.getSpecialization()
        );

        String department = getString(
                getQuestion("Department", doctor.getDepartment()),
                doctor.getDepartment()
        );

        Doctor updateDoctor = new Doctor(
                new Person(doctor.getId(), personData),
                new DoctorDetails(specialization, department)
        );

        GeneralPacket response = api.updateDoctor(updateDoctor);

        processPacketStatus(response);
    }

    /**
     * Processes scanner input for identification number and afterward deletes doctor with this id.
     */
    @Override
    public void delete(){
        int id = getInteger("ID: ");

        GeneralPacket packet = api.deleteDoctor(id);

        processPacketStatus(packet);
    }

    /**
     * Processes scanner input for identification number and afterward prints data about this doctor.
     */
    @Override
    public void findById(){
        int id = getInteger("ID: ");

        DataPacket<Doctor> packet = api.getDoctor(id);

        if (!processPacketStatusInSilence(packet)) return;

        System.out.print(packet.data);

        waitForEnter();
    }

    /**
     * Prints data about every doctor in hospital system.
     */
    @Override
    public void all(){
        DataPacket<List<String>> packet = api.allDoctors();

        if (!processPacketStatusInSilence(packet)) return;

        for (String text : packet.data) {
            System.out.println(text);
        }

        waitForEnter();
    }
}
