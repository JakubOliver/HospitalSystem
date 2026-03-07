package cz.cuni.kubinja.hospitalsystem.UI;

import cz.cuni.kubinja.hospitalsystem.Hospital;
import cz.cuni.kubinja.hospitalsystem.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.personnel.Patient;

import java.util.*;

/**
 * Menu page containing options connected with data reports.
 */
public class ReportsMenu extends Menu{
    /**
     * Data structure that stores top X entries with the biggest value.
     *
     * @param <T> Generic type for the key variable.
     */
    static class TopX<T>{
        /**
         * Data wrapper for the top X entry.
         *
         * @param data Data that will be used as key.
         * @param value Value of the entry.
         * @param <T> Generic type of the data/key.
         */
        record TopXEntry<T>(T data, int value){}

        public List<TopXEntry<T>> entries;
        private final int size;

        /**
         * Creates instance of top X data stricture, with the provided size.
         *
         * @param size Denotes how many top entries will be stored.
         */
        TopX(int size){
            this.size = size;
            entries = new ArrayList<>(size);
        }

        /**
         * Adds new entry.
         *
         * @param data Key data of the entry.
         * @param value Value of the entry.
         */
        void add(T data, int value){
            if (entries.size() >= size && value <= entries.getLast().value) return;

            TopXEntry<T> toMove = new TopXEntry<>(data, value);

            for(int i = 0; i < entries.size(); i++){
                if (toMove.value >= entries.get(i).value){
                    TopXEntry<T> temp = entries.get(i);
                    entries.set(i, toMove);
                    toMove = temp;
                }
            }

            if (entries.size() < size){
                entries.add(toMove);
            }
        }
    }

    /**
     * Creates report menu page.
     *
     * @param api Hospital api diving the menu options how to interact with hospital system.
     * @param scanner Scanner pointing to the input data.
     */
    public ReportsMenu(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Show statistics", this::statistics);
        addOption("Back", this::end);
    }

    /**
     * Computes and prints statistics about hospital, patients, doctors and appointments.
     */
    public void statistics(){
        DataPacket<List<Patient>> patientsPacket = api.allPatients();
        if (!processPacketStatusInSilence(patientsPacket)) return;
        List<Patient> patients = patientsPacket.data;

        DataPacket<List<Doctor>> doctorsPacket = api.allDoctors();
        if (!processPacketStatusInSilence(doctorsPacket)) return;
        List<Doctor> doctors = doctorsPacket.data;

        DataPacket<Calendar> calendarPacket = api.getCalendar();
        if (!processPacketStatusInSilence(calendarPacket)) return;
        Calendar calendar = calendarPacket.data;

        System.out.println("Absolute values");
        System.out.println("\tPatients: " + patients.size());
        System.out.println("\tDoctors: " + doctors.size());
        System.out.println("\tAppointments: " + calendar.size());

        System.out.println("Averages");

        System.out.println("\tIn average every patient was on " + String.format("%.3f", (patients.isEmpty() ? 0 : (float) calendar.size() / patients.size())) + " appointments");
        System.out.println("\tIn average every doctor worked "  + String.format("%.3f", (doctors.isEmpty() ? 0 : (float) calendar.size() / doctors.size())) + " appointments");

        System.out.println("5 patients with most visits:");
        TopX<Patient> topVisitingPatients = new TopX<>(5);
        Map<String, Integer> anamneses = new HashMap<>();
        for (Patient patient : patients){
            topVisitingPatients.add(patient, calendar.numberOfAppearances(patient));

            Integer occurrences = anamneses.get(patient.getAnamnesis());
            anamneses.put(patient.getAnamnesis(), occurrences == null ? 1 : ++occurrences);
        }

        for (int i = 0; i < topVisitingPatients.entries.size(); ++i){
            TopX.TopXEntry<Patient> patient = topVisitingPatients.entries.get(i);
            System.out.println("\t" + (i + 1) + ". " + patient.data.getFirstName() + " " + patient.data.getLastName() + " with " + patient.value + " appointments");
        }

        System.out.println("10 most common anamnesis:");
        TopX<String> commonAnamnesis = new TopX<>(10);
        anamneses.forEach(commonAnamnesis::add);

        for (int i = 0; i < commonAnamnesis.entries.size(); ++i){
            TopX.TopXEntry<String> anamnesis = commonAnamnesis.entries.get(i);

            System.out.println("\t" + (i + 1) + ". " + anamnesis.data + ":  " + anamnesis.value);
        }

        System.out.println("10 most common specializations:");
        Map<String, Integer> specializations = new HashMap<>();
        doctors.forEach(d -> {
            Integer occurrences = specializations.get(d.getSpecialization());
            specializations.put(d.getSpecialization(), occurrences == null ? 1 : ++occurrences);
        });

        TopX<String> commonSpecializations = new TopX<>(10);
        specializations.forEach(commonSpecializations::add);

        for (int i = 0; i < commonSpecializations.entries.size(); ++i){
            TopX.TopXEntry<String> specialization = commonSpecializations.entries.get(i);

            System.out.println("\t" + (i + 1) + ". " + specialization.data + ":  " + specialization.value);
        }

        waitForEnter();
    }
}
