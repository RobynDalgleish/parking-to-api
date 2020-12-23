package to.parking.core;

import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

@Value
public class DailyValidTimes {

    EnumSet<DayOfWeek> days;
    List<TemporalRange<LocalTime>> times;
}
