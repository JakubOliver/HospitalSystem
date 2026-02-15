package hospitalsystem.calendar;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Department{

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

    public String name;

    public final SortedSet<Appointment> appointments;

    private final Map<Integer, List<Appointment>> layers;
    private final Map<Appointment, Integer> layerOfAppointment;

    Department(String name){
        this.name = name;

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
        StringBuilder sb = new StringBuilder();

        sb.append(name).append("\n");

        List<Appointment> sortedAppointments = new ArrayList<>(appointments);

        LocalDateTime time = sortedAppointments.getFirst().startTime.withHour(8).withMinute(0).withSecond(0).withNano(0);

        int startIdx = 0;
        int lastIdx;

        while (startIdx < sortedAppointments.size()){
            lastIdx = getLastSameIdx(sortedAppointments, startIdx);

            for (int layer = 0; layer < layers.size(); layer++){
                for (int part = 0; part < 3; part++){
                    time = sortedAppointments.get(startIdx).startTime.withHour(8).withMinute(0).withSecond(0).withNano(0);

                    if (layer == 0) {
                        sb.append(printDate(time, part));
                    } else {
                        sb.append(" ".repeat(10));
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

            sb.append("\n");

            if (lastIdx != sortedAppointments.size()){
                long daysToNext = ChronoUnit.DAYS.between(sortedAppointments.get(startIdx).startTime, sortedAppointments.get(lastIdx).startTime);

                if (daysToNext > 3){
                    for (int part = 0; part < 3; part++){
                        sb.append(printDate(time,part));
                        sb.append(printEmpty(part));
                    }
                    sb.append("\n");

                    //TODO: hezci kdyz nic neni ať to vypise nothing between
                } else {
                    for (int empty = 0; empty < daysToNext - 1; empty++){
                        time = time.plusDays(1);

                        //TODO: whole at ones
                        for (int part = 0; part < 3; part++){
                            sb.append(printDate(time, part));
                            sb.append("-".repeat((16 - 8) * 2 * 6));
                            sb.append("\n");
                        }

                        sb.append("\n");
                    }
                }
            }

            startIdx = lastIdx;
        }

        return sb.toString();
    }

    private String processEmptyDays(int days, LocalDateTime time){
        StringBuilder sb = new StringBuilder();

        for (int empty = 0; empty < days; empty++){
            time = time.plusDays(1);

            for (int part = 0; part < 3; part++){
                sb.append(printDate(time, part));
                sb.append("-".repeat((16 - 8) * 2 * 6));
                sb.append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private int getLastSameIdx(List<Appointment> appointments, int startIdx){
        int idx = startIdx;

        while (idx < appointments.size() && appointments.get(startIdx).startTime.toLocalDate().equals(appointments.get(idx).startTime.toLocalDate())){
            idx++;
        }

        return idx;
    }

    private String printDate(LocalDateTime time, int part){
        int size = 10;

        if (part == 0){
            String dayName = time.getDayOfWeek().toString();
            int offset = (size - dayName.length()) / 2;

            return " ".repeat(offset) + dayName + " ".repeat(size - dayName.length() - offset);
        } else if (part == 1){
            return String.format("%02d", time.getDayOfMonth()) + " ".repeat(size - 4) + String.format("%02d", time.getMonthValue());
        } else {
            int offset = (size - 4) / 2;

            return " ".repeat(offset) + time.getYear() + " ".repeat(size - 4 - offset);
        }
    }

    private String printEmpty(int part){
        if (part == 0 || part == 2){
            return "X X\n";
        }

        return " X \n";
    }
}
