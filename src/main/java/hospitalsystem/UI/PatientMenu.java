package hospitalsystem.UI;

import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.packet.PersonPacket;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;
import hospitalsystem.util.HospitalAPI;

import java.util.List;
import java.util.Scanner;

/**
 * Menu page containing options connected with patients.
 */
public class PatientMenu extends Submenu implements PersonnelMenu {
    /**
     * Creates patient menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public PatientMenu(HospitalAPI api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Add new patient", this::add);
        addOption("Edit existing patient", this::edit);
        addOption("Delete existing patient", this::delete);
        addOption("Find patient by ID", this::findById);
        addOption("Show all patients", this::all);
        addOption("Back", this::end);
    }

    /**
     * Processes input data and calls for creating of new patient in hospital system.
     */
    @Override
    public void add(){
        PersonData personData = getPersonData(scanner);

        String anamnesis = getString(scanner, "Anamnesis: ");

        GeneralPacket packet = api.addPatient(new PatientData(personData, new PatientsDetails(anamnesis)));

        System.out.println(packet.resolveStatus());

        waitForEnter(scanner);
    }

    @Override
    public void edit(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.findPatient(id);

        if (!packet.successful){
            System.out.println(packet.resolveStatus());
            waitForEnter(scanner);

            return;
        }

        if (!(packet instanceof PersonPacket personPacket)){
            System.out.println("Invalid packet"); //TODO: nejak lepe vyresit
            waitForEnter(scanner);

            return;
        }

        if (!(personPacket.person instanceof Patient patient)){
            System.out.println("Invalid packet"); //TODO: nejak lepe vyresit
            waitForEnter(scanner);

            return;
        }

        PersonData personData = getPersonData(scanner, personPacket.person);
        String  anamnesis = getString(
                scanner,
                getQuestion("Anamnesis", patient.getAnamnesis()),
                patient.getAnamnesis()
        );

        Patient updatePatient = new Patient(
                new Person(personPacket.person.getId(), personData),
                new PatientsDetails(anamnesis)
        );

        GeneralPacket response = api.updatePatient(updatePatient);

        System.out.println(response.resolveStatus());
        waitForEnter(scanner);
    }

    @Override
    public void delete(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.deletePatient(id);

        System.out.println(packet.resolveStatus());
        waitForEnter(scanner);
    }

    /**
     * Prints information about patient with provided ID.
     * <p>
     * ID is provided via input data in class scanner.
     */
    @Override
    public void findById(){
        int id = getInteger(scanner, "ID: ");

        //System.out.println(api.findPatient(id));
        GeneralPacket packet = api.findPatient(id);

        System.out.println(packet.resolveStatus());

        if (packet.successful){
            if (packet instanceof PersonPacket){
                System.out.println(((PersonPacket) packet).person);
            } else {
                System.out.println("Invalid packet"); //TODO: nejak lepe vyresit
            }
        }

        waitForEnter(scanner);
    }

    /**
     * Prints all patients in the hospital system.
     */
    @Override
    public void all(){
        List<String> patients = api.findAllPatients();

        for(String patient : patients){
            System.out.println(patient);
        }

        waitForEnter(scanner);
    }
}
