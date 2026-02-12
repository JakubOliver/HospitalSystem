package hospitalsystem.calendar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class Department{

    static class LowestEmpty{
        PriorityQueue<Integer> heap;
        int highest;

        LowestEmpty(){
            heap = new PriorityQueue<>();

            highest = 0;
        }

        int getLowest(){
            if (heap.isEmpty()){
                return highest++;
            }

            return heap.poll();
        }

        void getBack(int number){
            heap.add(number);
        }
    }

    public SortedSet<Appointment> appointments;

    private Map<Integer, List<Appointment>> layers;
    private Map<Appointment, Integer> layerOfAppointment;

    Department(){
        appointments = new TreeSet<>();
        layers = new HashMap<>();
        layerOfAppointment = new HashMap<>();
    }

    public void addAppointment(Appointment appointment){
        appointments.add(appointment);
    }

    //TODO: ukl8dat si cas poslednich zmen, ať to nemusím porad pocitat

    public void computeLayers(){
        PriorityQueue<Appointment> heap = new PriorityQueue<>();
        LowestEmpty lowest = new LowestEmpty();

        for (Appointment appointment : appointments){
            while (!heap.isEmpty() && heap.peek().endTime.isBefore(appointment.startTime)){
                Appointment old = heap.poll();

                lowest.getBack(layerOfAppointment.get(old));
            }

            heap.add(appointment);
            int layer = lowest.getLowest();

            if (!layers.containsKey(layer)){
                layers.put(layer, new ArrayList<>());
            }

            layers.get(layer).add(appointment);
            layerOfAppointment.put(appointment, layer);
        }
    }

    //TODO: some round time function for 30 minuts intervals

    @Override
    public String toString(){
        /*
        StringBuilder sb = new StringBuilder();

        for (int layer = 0; layer < layers.size(); layer++){
            for (int part = 0; part < 3; part++){
                LocalDateTime time = appointments.first().startTime.withHour(8).withMinute(0).withSecond(0).withNano(0);

                for (Appointment appointment : appointments){
                    if (layerOfAppointment.get(appointment) == layer){
                        for (int i = 0; i < (Duration.between(time, appointment.endTime).toMinutes()) / 30; i++){
                            sb.append("---");
                        }

                        sb.append(appointment.getStringForPart(part));
                        time = appointment.endTime;
                    }
                }
                sb.append("\n");
            }
        }

        List<Appointment> sortedAppointments = new ArrayList<>(appointments);

        LocalDateTime time = sortedAppointments.getFirst().startTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
        int firstOfDay = 0;

        for (int layer = 0; layer < layers.size(); layer++){
            for (int part = 0; part < 3; part++){
                for (int idx = firstOfDay; idx < sortedAppointments.size(); idx++){
                    if (!time.toLocalDate().equals(sortedAppointments.get(idx).startTime.toLocalDate())){
                        if (idx == firstOfDay){
                            sb.append(printEmpty(part));
                        }

                        if (part == 2){
                            firstOfDay = idx;
                        }

                        break;
                    }

                    if (layerOfAppointment.get(sortedAppointments.get(idx)) == layer){
                        for (int i = 0; i < (Duration.between(time, sortedAppointments.get(idx).startTime).toMinutes()) / 30; i++){
                            sb.append("---");
                        }

                        sb.append(sortedAppointments.get(idx).getStringForPart(part));
                        time = sortedAppointments.get(idx).endTime;
                    }
                }

                sb.append("\n");
            }

            time = time.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        }
        */

        StringBuilder sb = new StringBuilder();

        List<Appointment> sortedAppointments = new ArrayList<>(appointments);

        LocalDateTime time;

        int startIdx = 0;
        int lastIdx;

        while (startIdx < sortedAppointments.size()){
            lastIdx = getLastSameIdx(sortedAppointments, startIdx);

            for (int layer = 0; layer < layers.size(); layer++){
                for (int part = 0; part < 3; part++){
                    if (lastIdx <= startIdx){
                        sb.append(printEmpty(part));
                        break;
                    }

                    time = sortedAppointments.get(startIdx).startTime.withHour(8).withMinute(0).withSecond(0).withNano(0);

                    if (layer == 0) {
                        sb.append(printDate(time, part));
                    } else {
                        sb.append(" ".repeat(6));
                    }

                    for (int idx = startIdx; idx < lastIdx; idx++){
                        if (layerOfAppointment.get(sortedAppointments.get(idx)) == layer){
                            for (int i = 0; i < (Duration.between(time, sortedAppointments.get(idx).startTime).toMinutes()) / 30; i++){
                                sb.append("-".repeat(6));
                            }

                            sb.append(sortedAppointments.get(idx).getStringForPart(part));
                            time = sortedAppointments.get(idx).endTime;
                        }
                    }

                    for (int i = 0; i < (Duration.between(time, time.withHour(16).withMinute(0).withSecond(0)).toMinutes()) / 30; i++){
                        sb.append("-".repeat(6));
                    }

                    sb.append("\n");
                }
            }

            startIdx = lastIdx;
        }

        return sb.toString();
    }

    private int getLastSameIdx(List<Appointment> appointments, int startIdx){
        int idx = 0;

        while (idx < appointments.size() && appointments.get(startIdx).startTime.toLocalDate().equals(appointments.get(idx).startTime.toLocalDate())){
            idx++;
        }

        return idx;
    }

    private String printDate(LocalDateTime time, int part){
        if (part == 0){
            return time.getDayOfWeek().toString();
        } else if (part == 1){
            return String.format("%02d", time.getDayOfMonth()) + "  " + String.format("%02d", time.getMonthValue());
        } else {
            return " " + time.getYear() + " ";
        }
    }

    private String printEmpty(int part){
        if (part == 0 || part == 2){
            return "X X\n";
        }

        return " X \n";
    }
}

public class Calendar {
    Map<String, Department> departments;

    //TODO: validate times, isPatient, isDoctor etc.
    public Calendar() {
        departments = new HashMap<>();
    }

    public void importData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Appointment appointment = new Appointment(resultSet);

            if (!departments.containsKey(appointment.department)) {
                departments.put(appointment.department, new Department());
            }

            departments.get(appointment.department).addAppointment(appointment);
        }

        computeLayers();
    }

    private void computeLayers(){
        for (Department department : departments.values()) {
            department.computeLayers();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String department : departments.keySet()){
            sb.append(department).append("\n");
            sb.append(departments.get(department).toString());
        }

        return sb.toString();
    }
}