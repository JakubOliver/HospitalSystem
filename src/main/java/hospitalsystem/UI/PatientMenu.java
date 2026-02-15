package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.packet.GeneralPacket;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;
import hospitalsystem.personnel.util.PatientData;
import hospitalsystem.personnel.util.PatientsDetails;
import hospitalsystem.personnel.util.PersonData;

import java.util.List;
import java.util.Scanner;

/**
 * Menu page containing options connected with patients.
 */
public class PatientMenu extends Menu implements PersonnelMenu {
    /**
     * Creates patient menu page.
     *
     * @param api HospitalAPI giving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public PatientMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    public PatientMenu(Hospital api, Scanner scanner, boolean dummy) {
        super(api, scanner, dummy);
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

        DataPacket<Patient> packet = api.addPatient(new PatientData(
                personData,
                new PatientsDetails(anamnesis)
        ));

        processPacketStatus(scanner, packet);
    }

    @Override
    public void edit(){
        int id = getInteger(scanner, "ID: ");

        DataPacket<Patient> packet = api.getPatient(id);

        if (!processPacketStatusInSilence(scanner, packet)) return;

        Patient patient = packet.data;

        PersonData personData = getPersonData(scanner, patient);
        String  anamnesis = getString(
                scanner,
                getQuestion("Anamnesis", patient.getAnamnesis()),
                patient.getAnamnesis()
        );

        Patient updatePatient = new Patient(
                new Person(patient.getId(), personData),
                new PatientsDetails(anamnesis)
        );

        GeneralPacket response = api.updatePatient(updatePatient);

        processPacketStatus(scanner, response);
    }

    @Override
    public void delete(){
        int id = getInteger(scanner, "ID: ");

        GeneralPacket packet = api.deletePatient(id);

        processPacketStatus(scanner, packet);
    }

    /**
     * Prints information about patient with provided ID.
     * <p>
     * ID is provided via input data in class scanner.
     */
    @Override
    public void findById(){
        int id = getInteger(scanner, "ID: ");

        DataPacket<Patient> packet = api.getPatient(id);

        if (!processPacketStatusInSilence(scanner, packet)) return;

        printAndWait(scanner, packet.data.toString());
    }

    /**
     * Prints all patients in the hospital system.
     */
    @Override
    public void all(){
        DataPacket<List<String>> packet = api.allPatients();

        if (!processPacketStatusInSilence(scanner, packet)) return;

        for (String text : packet.data){
            System.out.println(text);
        }

        waitForEnter(scanner);
    }
}
