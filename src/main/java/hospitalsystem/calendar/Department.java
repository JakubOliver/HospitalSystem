package hospitalsystem.calendar;

import hospitalsystem.calendar.util.AppointmentCompare;
import hospitalsystem.calendar.util.Parts;
import hospitalsystem.personnel.Person;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Manager of appointments for department.
 */
public class Department{
    record PeriodInfo(int start, int end, int layers){}
    record RowInfo(int layer, Parts part, LocalDateTime day){}

    /**
     * Data structure that remembers provided values and always provided the lowest not used.
     */
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

    /** Name of the department */
    public String name;

    /** Sorted set of the appointments connected with the department */
    public final SortedSet<Appointment> appointments;

    private final Map<Integer, List<Appointment>> layers;
    private final Map<Appointment, Integer> layerOfAppointment;

    private final int prefixSize = 10;
    private final int sizeOfSlot = 6;
    private final int lengthOfSlotInMinutes = 30;
    private final int numberOfSlotsInHour = 60 / lengthOfSlotInMinutes;

    private final LocalTime startTime = LocalTime.of(8, 0,0);
    private final LocalTime endTime = LocalTime.of(16,0,0);

    private final int sizeOfCalendar = (endTime.getHour() - startTime.getHour()) * numberOfSlotsInHour * sizeOfSlot;

    /**
     * Creates new hospital department.
     *
     * @param name Name of the department.
     */
    Department(String name){
        this.name = name;

        appointments = new TreeSet<>();
        layers = new HashMap<>();
        layerOfAppointment = new HashMap<>();
    }

    /**
     * Adds appointment info the department calendar.
     *
     * @param appointment Appointment that will be added into department calendar.
     */
    public void addAppointment(Appointment appointment){
        appointments.add(appointment);
    }

    /**
     * Computes hierarchy how to arrange appointments so the parallel appointments do not overlap in the calendar diagram.
     */
    public void computeLayers(){
        PriorityQueue<Appointment> heap = new PriorityQueue<>();
        LowestEmpty lowest = new LowestEmpty();

        /*
        Uses modified algorithm for max compact in interval graphs.
         */
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

    /**
     * Returns string representation of department calendar.
     *
     * @return String representation of department calendar.
     */
    @Override
    public String toString(){
        return toString(false);
    }

    /**
     * Returns string representation of department calendar.
     *
     * @param fromToday Denotes whether calendar will be exported whole or only appointments in the present and future.
     * @return String representation of department calendar.
     */
    public String toString(boolean fromToday){
        StringBuilder sb = new StringBuilder();

        sb.append(name).append("\n");

        List<Appointment> sortedAppointments = new ArrayList<>(appointments);

        int startIdx = 0;

        if (fromToday){
            LocalDate today = LocalDate.now();

            while (startIdx < sortedAppointments.size() && sortedAppointments.get(startIdx).startTime.toLocalDate().isBefore(today)){
                ++startIdx;
            }
        }

        if (startIdx == sortedAppointments.size()){
            sb.append("No appointments in the future.\n");
        }

        while (startIdx < sortedAppointments.size()){
            PeriodInfo week = getLastIdxOfSameWeek(sortedAppointments, startIdx);

            sb.append(processWeek(sortedAppointments, week));

            startIdx = week.end;
        }

        return sb.toString();
    }

    /**
     * Processes one week of appointments.
     *
     * @param appointments Sorted list of appointments.
     * @param info Information about the index of start and end of the time interval (week) and how many layers will be needed for the creation of calendar.
     * @return String representing diagram of the department calendar for the week specified by PeriodInfo.
     */
    private String processWeek(List<Appointment> appointments, PeriodInfo info){
        StringBuilder sb = new StringBuilder();
        int active = info.start;

        LocalDateTime startOfWeek = appointments.get(active).startTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        sb.append(startOfWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)).append(". week \n");

        sb.append(processEmptyDays(startOfWeek, appointments.get(active).startTime));

        while (active < info.end){
            PeriodInfo day = getLastIdxOfSameDay(appointments, active);
            active = day.end;

            sb.append(processDay(appointments, day));

            sb.append("\n");

            if (active != appointments.size()){
                sb.append(processEmptyDays(appointments, day));
            } else {
                LocalDateTime last = appointments.getLast().startTime;

                sb.append(processEmptyDays(
                        last.plusDays(1),
                        last.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1)
                ));
            }
        }

        return sb.toString();
    }

    /**
     * Process one day of appointments.
     *
     * @param appointments Sorted list of appointments.
     * @param info Information about the index of start and end of the time interval (day) and information about number of layers.
     * @return String representing diagram of the department calendar for the day.
     */
    private String processDay(List<Appointment> appointments, PeriodInfo info){
        StringBuilder sb = new StringBuilder();
        int active = info.start;

        LocalDateTime dayTime = appointments.get(active).startTime;

        for (int layer = 0; layer <= info.layers(); layer++){
            for (Parts part : Parts.values()){
                sb.append(processLayerPrefix(layer, part, dayTime));

                sb.append(processAppointments(appointments, info, new RowInfo(layer, part, dayTime)));
            }
        }

        return sb.toString();
    }

    /**
     * Processes days in which no appointments are arranged.
     *
     * @param appointments Sorted list of appointments.
     * @param info Information about the index of start and end of the time interval (empty days).
     * @return String representing diagram of the calendar for the empty days.
     */
    private String processEmptyDays(List<Appointment> appointments, PeriodInfo info){
        if (appointments.get(info.start).startTime.getDayOfWeek() == DayOfWeek.SUNDAY){
            return "";
        }

        return processEmptyDays(appointments.get(info.start).startTime.plusDays(1), appointments.get(info.end).startTime);
    }

    /**
     * Processes days in which no appointments are arranged.
     *
     * @param start Start time of the time interval in which no appointments are arranged.
     * @param end End time of the time interval in which no appointments are arranged.
     * @return String representing diagram of the calendar for the empty days.
     */
    private String processEmptyDays(LocalDateTime start, LocalDateTime end){
        StringBuilder sb = new StringBuilder();

        long daysToNext = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        LocalDateTime dayTime = start;

        for (int empty = 0; empty < daysToNext; empty++){
            int weekDif = start.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - dayTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            if (weekDif != 0) break;

            for (Parts part : Parts.values()){
                sb.append(printDate(dayTime, part));
                sb.append("-".repeat(sizeOfCalendar));
                sb.append("\n");
            }

            sb.append("\n");
            dayTime = dayTime.plusDays(1);
        }

        return sb.toString();
    }

    /**
     * Processes appointments in the given time interval into layers calendar diagram.
     *
     * @param appointments List of sorted appointments.
     * @param info Information about the time interval and number of layers.
     * @param row Information about in what state of calendar diagram the process is.
     * @return String representing diagram of calendar in the parts based on provided input information.
     */
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

    /**
     * Processes the blank space between appointments.
     *
     * @param time Time in which we are located in calendar.
     * @param appointment Nearest appointment to the time.
     * @return Returns string representing diagram of blank space in calendar.
     */
    private String processTimeBetweenAppointments(LocalDateTime time, Appointment appointment){
        return processTimeBetweenAppointments(time, appointment.startTime);
    }

    /**
     * Processes the blank space without any appointments.
     *
     * @param start Start time of the blank space.
     * @param end End time of the blank space.
     * @return Returns String representing diagram of blank space in calendar.
     */
    private String processTimeBetweenAppointments(LocalDateTime start, LocalDateTime end){
        int emptySlots = Math.toIntExact(Duration.between(start, end).toMinutes() / 30);

        return "-".repeat(emptySlots * sizeOfSlot);
    }

    /**
     * Returns diagram prefix for the days based on state in which the drawing of diagram is.
     *
     * @param layer Layer of the drawing.
     * @param part Part of the day the process is drawing.
     * @param time Time of the day.
     * @return diagram prefix for the days based on state in which the drawing of diagram is.
     */
    private String processLayerPrefix(int layer, Parts part, LocalDateTime time){
        if (layer == 0) {
            return printDate(time, part);
        } else {
            return " ".repeat(prefixSize);
        }
    }

    /**
     * Returns index of the last appointment in the same week.
     *
     * @param sortedAppointments Sorted list of appointments.
     * @param startIdx Index of the appointment for which we want to find last in the same week.
     * @return Index of the last appointment in the same week and number of layers in this period.
     */
    private PeriodInfo getLastIdxOfSameWeek(List<Appointment> sortedAppointments, int startIdx){
        AppointmentCompare sameWeek = (a1, a2) -> a1.startTime.toLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == a2.startTime.toLocalDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        return getLastIdxOfSame(sortedAppointments, startIdx, sameWeek);
    }

    /**
     * Returns index of the last appointment in the same day.
     *
     * @param sortedAppointments Sorted list of appointments.
     * @param startIdx Index of the appointment for which we want to find last in the same day.
     * @return Index of the last appointment in the same day and number of layers in this period.
     */
    private PeriodInfo getLastIdxOfSameDay(List<Appointment> sortedAppointments, int startIdx){
        AppointmentCompare sameDay = (a1, a2) -> a1.startTime.toLocalDate().equals(a2.startTime.toLocalDate());

        return getLastIdxOfSame(sortedAppointments, startIdx, sameDay);
    }

    /**
     * Generic function return last appointment satisfying provided compare function.
     *
     * @param appointments Sorted list of appointments.
     * @param startIdx Index of the appointment that we will compare other appointments.
     * @param dateCompare Function that we will use for denoting whether the appointments still satisfy relation.
     * @return Index of the last appointment satisfying the compare function and number of layers in this period.
     */
    private PeriodInfo getLastIdxOfSame(List<Appointment> appointments, int startIdx, AppointmentCompare dateCompare){
        int idx = startIdx;
        int layers = 0;

        while (idx <  appointments.size() && dateCompare.compare(appointments.get(startIdx), appointments.get(idx))){
            layers = Math.max(layers, layerOfAppointment.get(appointments.get(idx)));
            idx++;
        }

        return new PeriodInfo(startIdx, idx, layers);
    }

    /**
     * Returns string representing date of the day based on the part in which the drawing is.
     *
     * @param time Time of the day.
     * @param part Part in which the drawing is.
     * @return String representing date of the day based on the part in which the drawing is.
     */
    private String printDate(LocalDateTime time, Parts part){
        int lengthOfNumbers = 4;

        return switch (part){
            case TOP -> {
                String dayName = time.getDayOfWeek().toString();
                int offset = (prefixSize - dayName.length()) / 2;

                yield  " ".repeat(offset) + dayName + " ".repeat(prefixSize - dayName.length() - offset);
            }
            case MIDDLE -> {
                yield  String.format("%02d", time.getDayOfMonth()) + " ".repeat(prefixSize - lengthOfNumbers) + String.format("%02d", time.getMonthValue());
            }
            case BOTTOM -> {
                int offset = (prefixSize - lengthOfNumbers) / 2;

                yield  " ".repeat(offset) + time.getYear() + " ".repeat(prefixSize - lengthOfNumbers - offset);
            }
        };
    }

    //TODO:
    public int size(){
        return appointments.size();
    }

    public int numberOfAppearances(Person person){
        int appearances = 0;

        for (Appointment appointment : appointments){
            if (person.getId() == appointment.patientId || person.getId() == appointment.doctorId){
                appearances++;
            }
        }

        return appearances;
    }
}
