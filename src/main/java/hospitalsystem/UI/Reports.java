package hospitalsystem.UI;

import hospitalsystem.Hospital;
import hospitalsystem.calendar.Calendar;
import hospitalsystem.packet.DataPacket;
import hospitalsystem.personnel.Doctor;
import hospitalsystem.personnel.Patient;
import hospitalsystem.personnel.Person;

import java.util.*;

public class Reports extends Menu{
    static class TopX<T>{
        record TopXEntry<T>(T data, int value){}

        public List<TopXEntry<T>> entries;
        private int size;

        TopX(int size){
            this.size = size;
            entries = new ArrayList<>(size);
        }

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

    public Reports(Hospital api, Scanner scanner) {
        super(api, scanner);
    }

    @Override
    public void defineMenu() {
        addOption("Show statistics", this::statistics);
        addOption("Back", this::end);
    }

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

        System.out.println("\tIn average every patient was on " + String.format("%.3f", (float) calendar.size() / patients.size()) + " appointments");
        System.out.println("\tIn average every doctor worked "  + String.format("%.3f", (float) calendar.size() / doctors.size()) + " appointments");

        System.out.println("5 patients with most visits:");
        TopX<Patient> topVisitingPatients = new TopX<>(5);
        Map<String, Integer> anamneses = new HashMap<>();
        for (Patient patient : patients){
            topVisitingPatients.add(patient, calendar.numberOfAppearances(patient));

            Integer occurences = anamneses.get(patient.getAnamnesis());
            anamneses.put(patient.getAnamnesis(), occurences == null ? 1 : ++occurences);
        }

        for (TopX.TopXEntry<Patient> patient : topVisitingPatients.entries){
            System.out.println("\t" + patient.data.getFirstName() + " " + patient.data.getLastName() + " with " + patient.value + " appointments");
        }

        System.out.println("10 most common anamnesis:");
        TopX<String> commonAnamnesis = new TopX<>(10);
        anamneses.forEach(commonAnamnesis::add);

        for (TopX.TopXEntry<String> anamnesis : commonAnamnesis.entries){
            System.out.println("\t" + anamnesis.data + ":  " + anamnesis.value);
        }

        System.out.println("10 most common specializations:");
        Map<String, Integer> specializations = new HashMap<>();
        doctors.forEach(d -> {
            Integer occurences = specializations.get(d.getSpecialization());
            specializations.put(d.getSpecialization(), occurences == null ? 1 : ++occurences);
        });

        //TODO: do specificke funkce
        TopX<String> commonSpecializations = new TopX<>(10);
        specializations.forEach(commonSpecializations::add);

        for (TopX.TopXEntry<String> specialization : commonSpecializations.entries){
            System.out.println("\t" + specialization.data + ":  " + specialization.value);
        }

        waitForEnter();
    }
}
