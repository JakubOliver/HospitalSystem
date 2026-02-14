package hospitalsystem.UI;

import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.PersonPacket;
import hospitalsystem.packet.TextPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.DoctorData;
import hospitalsystem.personnel.util.DoctorDetails;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.Scanner;

/**
 * Menu page containing options connected with doctors.
 */
public class DoctorMenu extends Submenu implements PersonnelMenu {
    /**
     * Creates doctor menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public DoctorMenu(HospitalAPI api, Scanner scanner) {
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

        DoctorData  doctorData = new DoctorData(personData, new DoctorDetails(specialization, department));

        api.addDoctor(doctorData);

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    @Override
    public void edit(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.findDoctor(id);

        if (!packet.successful){ //TODO: dat spolecnet do menu metody at nemusi se opakovat kod
            System.out.println(packet.resolveStatus());
            waitForEnter(scanner);

            return;
        }

        if (!(packet instanceof PersonPacket personPacket)){
            System.out.println("Invalid packet"); //TODO: nejak lepe vyresit
            waitForEnter(scanner);

            return;
        }

        if (!(personPacket.person instanceof Doctor doctor)){
            System.out.println("Invalid packet"); //TODO: nejak lepe vyresit
            waitForEnter(scanner);

            return;
        }

        PersonData personData = getPersonData(scanner, personPacket.person);
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
                new Person(personPacket.person.getId(), personData),
                new DoctorDetails(specialization, department)
        );

        GeneralPacket response = api.updateDoctor(updateDoctor);

        System.out.println(response.resolveStatus());
        waitForEnter(scanner);
    }

    @Override
    public void delete(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.deleteDoctor(id);

        System.out.println(packet.resolveStatus());
        waitForEnter(scanner);
    }

    @Override
    public void findById(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.findDoctor(id);

        System.out.println(packet.resolveStatus());

        if (packet.successful){
            if (packet instanceof PersonPacket personPacket){
                System.out.println(personPacket.person);
            } else {
                System.out.println("Invalid packet");
            }
        }

        waitForEnter(scanner);
    }

    @Override
    public void all(){
        GeneralPacket packet = api.findAllDoctors();

        if (!packet.successful){
            System.out.println(packet.resolveStatus());
            waitForEnter(scanner);
            return;
        }

        if (packet instanceof TextPacket textPacket){
            System.out.println(textPacket.text);
        }

        waitForEnter(scanner);
    }
}
