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

    /**
     * Creates dummy patient menu page.
     * <p>
     * This way how to create menu is only used for testing, because we want to have option how to call directly menu options and not only via prepared input data.
     *
     * @param api Hospital gives menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     * @param dummy Decides whether the menu page will be dummy (do not show menu, but only processes direct method calls)
     */
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
        PersonData personData = getPersonData();

        String anamnesis = getString("Anamnesis: ");

        DataPacket<Patient> packet = api.addPatient(new PatientData(
                personData,
                new PatientsDetails(anamnesis)
        ));

        processPacketStatus(packet);
    }

    /**
     * Processes input about patient and updates data for the patient with the provided id.
     */
    @Override
    public void edit(){
        int id = getInteger("ID: ");

        DataPacket<Patient> packet = api.getPatient(id);

        if (!processPacketStatusInSilence(packet)) return;

        Patient patient = packet.data;

        PersonData personData = getPersonData(patient);
        String  anamnesis = getString(
                getQuestion("Anamnesis", patient.getAnamnesis()),
                patient.getAnamnesis()
        );

        Patient updatePatient = new Patient(
                new Person(patient.getId(), personData),
                new PatientsDetails(anamnesis)
        );

        GeneralPacket response = api.updatePatient(updatePatient);

        processPacketStatus(response);
    }

    /**
     * Delete patient based on provided ID.
     */
    @Override
    public void delete(){
        int id = getInteger("ID: ");

        GeneralPacket packet = api.deletePatient(id);

        processPacketStatus(packet);
    }

    /**
     * Prints information about patient with provided ID.
     * <p>
     * ID is provided via input data in class scanner.
     */
    @Override
    public void findById(){
        int id = getInteger("ID: ");

        DataPacket<Patient> packet = api.getPatient(id);

        if (!processPacketStatusInSilence(packet)) return;

        printAndWait(packet.data.toString());
    }

    /**
     * Prints all patients in the hospital system.
     */
    @Override
    public void all(){
        DataPacket<List<Patient>> packet = api.allPatients();

        if (!processPacketStatusInSilence(packet)) return;

        for (Patient patient : packet.data){
            System.out.println(patient);
        }

        waitForEnter();
    }
}
