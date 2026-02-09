package hospitalsystem.calendar.util;

import java.time.LocalDateTime;

public record CalendarEntryData(int patientsId, int doctorsId, LocalDateTime starTime, LocalDateTime endTime) { }
