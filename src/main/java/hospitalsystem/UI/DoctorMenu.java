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
public class DoctorMenu extends Submenu {
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
    public void printMenu() {
        System.out.println("1. Add new doctor");
        System.out.println("2. Edit existing doctor");
        System.out.println("3. Delete existing doctor");
        System.out.println("4. Find doctor by ID");
        System.out.println("5. Show all doctors");
        System.out.println("6. Back"); //TODO: mozna spotit na volani s PetientMenu
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 6)){
            case 1:
                addDoctor(); break;
            case 2:
                editDoctor(); break;
            case 3:
                deleteDoctor(); break;
            case 4:
                findById(); break;
            case 5:
                allDoctors(); break;
            case 6:
                end(); break;
        }
    }

    /**
     * Processes input data and calls for creating of new doctor in hospital system.
     */
    private void addDoctor(){
        PersonData personData = getPersonData(scanner);

        String specialization = getString(scanner, "Specialization: ");
        String department = getString(scanner, "Department: ");

        DoctorData  doctorData = new DoctorData(personData, new DoctorDetails(specialization, department));

        api.addDoctor(doctorData);

        System.out.print("Press enter to continue...");
        scanner.nextLine();
    }

    private void editDoctor(){
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

    private void deleteDoctor(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.deleteDoctor(id);

        System.out.println(packet.resolveStatus());
        waitForEnter(scanner);
    }

    private void findById(){
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

    private void allDoctors(){
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
