package to.parking.core;

import lombok.Value;

import java.util.List;

@Value
public class ValidTime {

    List<DailyValidTimes> dailyValidTimes;
}
