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
public class PatientMenu extends Submenu {
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
    public void printMenu() {
        System.out.println(); //TODO: rozmyslet, zda dava u submenu odstup a opakovani
        System.out.println("1. Add new patient");
        System.out.println("2. Edit existing patient");
        System.out.println("3. Delete existing patient");
        System.out.println("4. Find patient by ID");
        System.out.println("5. Show all patients");
        System.out.println("6. Back");
    }

    @Override
    public void processMenu() {
        switch (getOption(scanner, 6)){
            case 1:
                addPatient(); break;
            case 2:
                editPatient(); break;
            case 3:
                deletePatient(); break;
            case 4:
                findById(); break;
            case 5:
                findAllPatient(); break;
            case 6:
                end(); break;
        }
    }

    /**
     * Processes input data and calls for creating of new patient in hospital system.
     */
    private void addPatient(){
        PersonData personData = getPersonData(scanner);

        String anamnesis = getString(scanner, "Anamnesis: ");

        GeneralPacket packet = api.addPatient(new PatientData(personData, new PatientsDetails(anamnesis)));

        System.out.println(packet.resolveStatus());

        waitForEnter(scanner);
    }

    private void editPatient(){
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

    private void deletePatient(){
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
    private void findById(){
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
    private void findAllPatient(){
        List<String> patients = api.findAllPatients();

        for(String patient : patients){
            System.out.println(patient);
        }

        waitForEnter(scanner);
    }
}
