package to.parking.core;

import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Value
public class DailyValidTimes {

    TemporalRange<DayOfWeek> days;
    List<TemporalRange<LocalTime>> times;
}
