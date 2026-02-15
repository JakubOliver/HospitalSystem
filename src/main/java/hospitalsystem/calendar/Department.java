package hospitalsystem.calendar;

import hospitalsystem.calendar.util.AppointmentCompare;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class Department{
    record PeriodInfo(int start, int end, int layers){}
    record RowInfo(int layer, int part, LocalDateTime day){}

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

    int prefixSize = 10;
    int sizeOfSlot = 6;
    int lengthOfSlotInMinutes = 30;
    int numberOfSlotsInHour = 60 / lengthOfSlotInMinutes;
    int numberOfParts = 3;

    LocalTime startTime = LocalTime.of(8, 0,0);
    LocalTime endTime = LocalTime.of(16,0,0);

    int sizeOfCalendar = (endTime.getHour() - startTime.getHour()) * numberOfSlotsInHour * sizeOfSlot;

    Department(String name){
        this.name = name;

        appointments = new TreeSet<>();
        layers = new HashMap<>();
        layerOfAppointment = new HashMap<>();
    }

    public void addAppointment(Appointment appointment){
        appointments.add(appointment);
    }

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

        int startIdx = 0;

        while (startIdx < sortedAppointments.size()){
            PeriodInfo week = getLastIdxOfSameWeek(sortedAppointments, startIdx);

            sb.append(processWeek(sortedAppointments, week));

            startIdx = week.end;
        }

        return sb.toString();
    }

    private String processWeek(List<Appointment> appointments, PeriodInfo info){
        StringBuilder sb = new StringBuilder();
        int active = info.start;

        LocalDateTime startOfWeek = appointments.get(active).startTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        sb.append(startOfWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)).append(". week \n");

        sb.append(processEmptyDays(startOfWeek, appointments.get(active).startTime));

        while (active < info.end){
            PeriodInfo day = getLastIdxOfSameDay(appointments, info.start);
            active = day.end;

            sb.append(processDay(appointments, day));

            sb.append("\n");

            if (active != appointments.size()){
                sb.append(processEmptyDays(appointments, day));
            } else {
                LocalDateTime last = appointments.getLast().startTime;
                System.out.println("--");
                System.out.println(last.plusDays(1));
                System.out.println("--");
                sb.append(processEmptyDays(
                        last.plusDays(1),
                        last.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
                ));
            }
        }

        return sb.toString();
    }

    private String processDay(List<Appointment> appointments, PeriodInfo info){
        StringBuilder sb = new StringBuilder();
        int active = info.start;

        LocalDateTime dayTime = appointments.get(active).startTime;

        for (int layer = 0; layer <= info.layers(); layer++){
            for (int part = 0; part < numberOfParts; part++){
                sb.append(processLayerPrefix(layer, part, dayTime));

                sb.append(processAppointments(appointments, info, new RowInfo(layer, part, dayTime)));
            }
        }

        return sb.toString();
    }

    private String processEmptyDays(List<Appointment> appointments, PeriodInfo info){
        if (appointments.get(info.start).startTime.getDayOfWeek() == DayOfWeek.SUNDAY){
            return "";
        }

        return processEmptyDays(appointments.get(info.start).startTime.plusDays(1), appointments.get(info.end).startTime);
    }

    private String processEmptyDays(LocalDateTime start, LocalDateTime end){
        StringBuilder sb = new StringBuilder();
        //long daysToNext = ChronoUnit.DAYS.between(appointments.get(info.start).startTime.toLocalDate(), appointments.get(info.end).startTime.toLocalDate());

        long daysToNext = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        //LocalDateTime dayTime = appointments.get(info.start).startTime;
        LocalDateTime dayTime = start;

        /*
        if (daysToNext > 3000){
            //TODO: neco specl

            return sb.toString();
        }
         */
        System.out.println("____________");
        //TODO: dodrzovat tydny at sedi nazvy, doplnit zacatky tydnu
        for (int empty = 0; empty < daysToNext; empty++){

            System.out.println(start);
            System.out.println(start.getDayOfWeek());
            System.out.println(start.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            System.out.println(dayTime);
            System.out.println(dayTime.getDayOfWeek());
            System.out.println(dayTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

            int weekDif = start.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - dayTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            System.out.println(weekDif);
            if (!(weekDif == 0)) break;

            for (int part = 0; part < numberOfParts; part++){
                sb.append(printDate(dayTime, part));
                sb.append("-".repeat(sizeOfCalendar));
                sb.append("\n");
            }

            sb.append("\n");
            dayTime = dayTime.plusDays(1);
        }

        return sb.toString();
    }

    private String processAppointments(List<Appointment> appointments, PeriodInfo info, RowInfo row){
        StringBuilder sb = new StringBuilder();
        LocalDateTime time = row.day.withHour(8).withMinute(0).withSecond(0).withNano(0);

        for (int idx = info.start; idx < info.end; idx++){
            if (layerOfAppointment.get(appointments.get(idx)) != row.layer) continue;

            sb.append(processTimeBetweenAppointments(time, appointments.get(idx)));
            sb.append(appointments.get(idx).getStringForPart(row.part));
            time = appointments.get(idx).endTime;
        }

        sb.append(processTimeBetweenAppointments(time, time.withHour(16).withMinute(0).withSecond(0)));

        sb.append("\n");

        return sb.toString();
    }

    private String processTimeBetweenAppointments(LocalDateTime time, Appointment appointment){
        return processTimeBetweenAppointments(time, appointment.startTime);
    }

    private String processTimeBetweenAppointments(LocalDateTime start, LocalDateTime end){
        int emptySlots = Math.toIntExact(Duration.between(start, end).toMinutes() / 30);

        return "-".repeat(emptySlots * sizeOfSlot);
    }

    private String processLayerPrefix(int layer, int part, LocalDateTime time){
        if (layer == 0) {
            return printDate(time, part);
        } else {
            return " ".repeat(prefixSize);
        }
    }

    private PeriodInfo getLastIdxOfSameWeek(List<Appointment> sortedAppointments, int startIdx){
        AppointmentCompare sameWeek = (a1, a2) -> {
            return a1.startTime.toLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == a2.startTime.toLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        };

        return getLastIdxOfSame(sortedAppointments, startIdx, sameWeek);
    }

    private PeriodInfo getLastIdxOfSameDay(List<Appointment> sortedAppointments, int startIdx){
        AppointmentCompare sameDay = (a1, a2) -> {return a1.startTime.toLocalDate().equals(a2.startTime.toLocalDate());};

        return getLastIdxOfSame(sortedAppointments, startIdx, sameDay);
    }

    private PeriodInfo getLastIdxOfSame(List<Appointment> appointments, int startIdx, AppointmentCompare dateCompare){
        int idx = startIdx;
        int layers = 0;

        while (idx <  appointments.size() && dateCompare.compare(appointments.get(startIdx), appointments.get(idx))){
            layers = Math.max(layers, layerOfAppointment.get(appointments.get(idx)));
            idx++;
        }

        return new PeriodInfo(startIdx, idx, layers);
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
