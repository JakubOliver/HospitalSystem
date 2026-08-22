package cz.cuni.kubinja.hospitalsystem.core.util;

import cz.cuni.kubinja.hospitalsystem.core.Hospital;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentData;
import cz.cuni.kubinja.hospitalsystem.core.calendar.AppointmentSummary;
import cz.cuni.kubinja.hospitalsystem.core.calendar.Calendar;
import cz.cuni.kubinja.hospitalsystem.core.database.exceptions.DatabaseException;
import cz.cuni.kubinja.hospitalsystem.core.packet.DataPacket;
import cz.cuni.kubinja.hospitalsystem.core.packet.GeneralPacket;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Doctor;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Patient;
import cz.cuni.kubinja.hospitalsystem.core.personnel.Person;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/*
  mvn -pl :core exec:java \
    -Dexec.mainClass=cz.cuni.kubinja.hospitalsystem.core.util.AppointmentGenerator \
    -Dexec.args="2027-01-01 2027-01-31 100 database.db"
 */

public final class AppointmentGenerator {
    private static final String DEFAULT_DATABASE_URL =
        "jdbc:sqlite:database.db";
    private static final Duration APPOINTMENT_DURATION = Duration.ofHours(1);
    private static final int MAX_PLANNING_ATTEMPTS = 100;

    private final Hospital hospital;
    private final Random random;

    public AppointmentGenerator(Hospital hospital) {
        this(hospital, new Random());
    }

    AppointmentGenerator(Hospital hospital, Random random) {
        this.hospital = Objects.requireNonNull(hospital);
        this.random = Objects.requireNonNull(random);
    }

    public List<AppointmentData> generate(
        LocalDate startDate,
        LocalDate endDate,
        int numberOfAppointments
    ) {
        validateArguments(startDate, endDate, numberOfAppointments);

        if (numberOfAppointments == 0) {
            return List.of();
        }

        List<Patient> patients = requireData(
            hospital.allPatients(),
            "patients"
        );
        List<Doctor> doctors = requireData(
            hospital.allDoctors(),
            "doctors"
        );
        List<AppointmentSummary> existingAppointments = requireData(
            hospital.getAppointmentSummaries(),
            "appointments"
        );

        if (patients.isEmpty()) {
            throw new IllegalStateException(
                "At least one patient is required to generate appointments."
            );
        }
        if (doctors.isEmpty()) {
            throw new IllegalStateException(
                "At least one doctor is required to generate appointments."
            );
        }

        List<AppointmentData> plan = createPlan(
            createStartSlots(startDate, endDate),
            patients,
            doctors,
            existingAppointments,
            numberOfAppointments
        );
        if (plan == null) {
            throw new IllegalStateException(
                "Unable to schedule " + numberOfAppointments
                    + " appointments in the requested date range with"
                    + " the available personnel."
            );
        }

        persist(plan);
        return List.copyOf(plan);
    }

    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4) {
            printUsage();
            return;
        }

        try {
            LocalDate startDate = LocalDate.parse(args[0]);
            LocalDate endDate = LocalDate.parse(args[1]);
            int numberOfAppointments = Integer.parseInt(args[2]);
            String databaseUrl = args.length == 4
                ? normalizeDatabaseUrl(args[3])
                : DEFAULT_DATABASE_URL;

            Hospital hospital = new Hospital(databaseUrl);
            List<AppointmentData> generated = new AppointmentGenerator(hospital)
                .generate(startDate, endDate, numberOfAppointments);

            System.out.println(
                "Generated " + generated.size() + " appointments."
            );
        } catch (DatabaseException | RuntimeException exception) {
            System.err.println(
                "Unable to generate appointments: " + exception.getMessage()
            );
        }
    }

    private static void validateArguments(
        LocalDate startDate,
        LocalDate endDate,
        int numberOfAppointments
    ) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                "Start date and end date are required."
            );
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "Start date must not be after end date."
            );
        }
        if (numberOfAppointments < 0) {
            throw new IllegalArgumentException(
                "Number of appointments must not be negative."
            );
        }

        LocalDateTime firstStart = startDate.atTime(
            Calendar.minStartingTime,
            0
        );
        LocalDateTime lastEnd = endDate.atTime(
            Calendar.maxEndingTime,
            0
        );
        if (
            !Calendar.isAppointmentWithinValidDateTime(firstStart)
            || !Calendar.isAppointmentWithinValidDateTime(lastEnd)
        ) {
            throw new IllegalArgumentException(
                "Dates must be between 2000-01-01 and 3000-01-01."
            );
        }
    }

    private static <T> T requireData(
        DataPacket<T> packet,
        String description
    ) {
        if (!packet.successful) {
            throw new IllegalStateException(
                "Unable to load " + description + ": "
                    + packet.resolveStatus()
            );
        }

        return packet.data;
    }

    private static List<LocalDateTime> createStartSlots(
        LocalDate startDate,
        LocalDate endDate
    ) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalTime firstTime = LocalTime.of(Calendar.minStartingTime, 0);
        LocalTime lastTime = LocalTime.of(
            Calendar.maxEndingTime,
            0
        ).minus(APPOINTMENT_DURATION);

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            LocalTime time = firstTime;
            while (!time.isAfter(lastTime)) {
                slots.add(LocalDateTime.of(date, time));
                time = time.plusMinutes(Calendar.alignmentOfAppointment);
            }
            date = date.plusDays(1);
        }

        return slots;
    }

    private List<AppointmentData> createPlan(
        List<LocalDateTime> startSlots,
        List<Patient> patients,
        List<Doctor> doctors,
        List<AppointmentSummary> existingAppointments,
        int numberOfAppointments
    ) {
        for (int attempt = 0; attempt < MAX_PLANNING_ATTEMPTS; attempt++) {
            List<AppointmentData> plan = tryCreatePlan(
                startSlots,
                patients,
                doctors,
                existingAppointments,
                numberOfAppointments
            );
            if (plan != null) {
                return plan;
            }
        }

        return null;
    }

    private List<AppointmentData> tryCreatePlan(
        List<LocalDateTime> startSlots,
        List<Patient> patients,
        List<Doctor> doctors,
        List<AppointmentSummary> existingAppointments,
        int numberOfAppointments
    ) {
        Occupancy occupancy = Occupancy.from(existingAppointments);
        List<AppointmentData> plan = new ArrayList<>();

        for (int index = 0; index < numberOfAppointments; index++) {
            List<LocalDateTime> usableSlots = startSlots.stream()
                .filter(start -> hasAvailablePersonnel(
                    start,
                    patients,
                    doctors,
                    occupancy
                ))
                .toList();
            if (usableSlots.isEmpty()) {
                return null;
            }

            LocalDateTime start = randomItem(usableSlots);
            LocalDateTime end = start.plus(APPOINTMENT_DURATION);
            Patient patient = randomItem(availablePeople(
                patients,
                occupancy.patientTimes,
                start,
                end
            ));
            Doctor doctor = randomItem(availablePeople(
                doctors,
                occupancy.doctorTimes,
                start,
                end
            ));

            AppointmentData appointment = new AppointmentData(
                patient.getId(),
                doctor.getId(),
                start,
                end
            );
            plan.add(appointment);
            occupancy.add(appointment);
        }

        return plan;
    }

    private static boolean hasAvailablePersonnel(
        LocalDateTime start,
        List<Patient> patients,
        List<Doctor> doctors,
        Occupancy occupancy
    ) {
        LocalDateTime end = start.plus(APPOINTMENT_DURATION);
        return hasAvailablePerson(
            patients,
            occupancy.patientTimes,
            start,
            end
        ) && hasAvailablePerson(
            doctors,
            occupancy.doctorTimes,
            start,
            end
        );
    }

    private static <T extends Person> boolean hasAvailablePerson(
        List<T> people,
        Map<Integer, List<TimeRange>> occupiedTimes,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return people.stream().anyMatch(
            person -> isAvailable(person, occupiedTimes, start, end)
        );
    }

    private static <T extends Person> List<T> availablePeople(
        List<T> people,
        Map<Integer, List<TimeRange>> occupiedTimes,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return people.stream()
            .filter(person -> isAvailable(
                person,
                occupiedTimes,
                start,
                end
            ))
            .toList();
    }

    private static boolean isAvailable(
        Person person,
        Map<Integer, List<TimeRange>> occupiedTimes,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return occupiedTimes.getOrDefault(person.getId(), List.of()).stream()
            .noneMatch(time -> time.overlaps(start, end));
    }

    private <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    private void persist(List<AppointmentData> appointments) {
        for (int index = 0; index < appointments.size(); index++) {
            GeneralPacket packet = hospital.addAppointment(
                appointments.get(index)
            );
            if (!packet.successful) {
                throw new IllegalStateException(
                    "Unable to persist generated appointment "
                        + (index + 1) + ": " + packet.resolveStatus()
                );
            }
        }
    }

    private static String normalizeDatabaseUrl(String value) {
        return value.startsWith("jdbc:") ? value : "jdbc:sqlite:" + value;
    }

    private static void printUsage() {
        System.err.println(
                "Usage: AppointmentGenerator <start-date> <end-date> <count>"
                        + " [database-path-or-jdbc-url]"
        );
    }

    private record TimeRange(LocalDateTime start, LocalDateTime end) {
        private boolean overlaps(
            LocalDateTime otherStart,
            LocalDateTime otherEnd
        ) {
            return otherStart.isBefore(end) && otherEnd.isAfter(start);
        }
    }

    private static final class Occupancy {
        private final Map<Integer, List<TimeRange>> patientTimes =
                new HashMap<>();
        private final Map<Integer, List<TimeRange>> doctorTimes =
                new HashMap<>();

        private static Occupancy from(
                List<AppointmentSummary> appointments
        ) {
            Occupancy occupancy = new Occupancy();
            for (AppointmentSummary appointment : appointments) {
                TimeRange time = new TimeRange(
                    appointment.startTime(),
                    appointment.endTime()
                );
                occupancy.add(
                    occupancy.patientTimes,
                    appointment.patientId(),
                    time
                );
                occupancy.add(
                    occupancy.doctorTimes,
                    appointment.doctorId(),
                    time
                );
            }

            return occupancy;
        }

        private void add(AppointmentData appointment) {
            TimeRange time = new TimeRange(
                appointment.starTime(),
                appointment.endTime()
            );
            add(patientTimes, appointment.patientsId(), time);
            add(doctorTimes, appointment.doctorsId(), time);
        }

        private void add(
            Map<Integer, List<TimeRange>> occupiedTimes,
            int personId,
            TimeRange time
        ) {
            occupiedTimes.computeIfAbsent(
                personId,
                ignored -> new ArrayList<>()
            ).add(time);
        }
    }
}
