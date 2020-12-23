package to.parking.core;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

class ValidTimeParser {


    // Regular Expression Patterns foor matching
    private static final Pattern twelveHourTimePattern = Pattern.compile("((?:1[0-2]|0[1-9]):[0-5][0-9] (?:am|pm))");
    private static final Pattern dayOfWeekPattern = Pattern.compile("(Mon|Tues|Wed|Thurs|Fri|Sat|Sun)");
    private static final Pattern dayRangePattern = Pattern.compile(dayOfWeekPattern.pattern() + "(?:-" + dayOfWeekPattern.pattern() + ")?");
    private static final Pattern dailyValidTimesPattern = Pattern.compile(
        twelveHourTimePattern.pattern() + "-" + twelveHourTimePattern + ", " + dayRangePattern.pattern()
    );

    // Formatters used for parsing
    private static final DateTimeFormatter twelveHourTimeFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("hh:mm")
        .appendLiteral(" ")
        .appendText(ChronoField.AMPM_OF_DAY, Map.of(0L, "am", 1L, "pm"))
        .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter dayOfWeekFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("EEE")
        .toFormatter(Locale.ENGLISH);

    ValidTime parse(String input) {

        List<DailyValidTimes> dailyValidTimes = new ArrayList<>();

        var dailyValidTimesMatcher = dailyValidTimesPattern.matcher(input);
        if (dailyValidTimesMatcher.matches()) {
            var validHours = new TemporalRange<>(
                LocalTime.parse(dailyValidTimesMatcher.group(1), twelveHourTimeFormatter),
                LocalTime.parse(dailyValidTimesMatcher.group(2), twelveHourTimeFormatter)
            );
            var startDay = DayOfWeek.from(dayOfWeekFormatter.parse(dailyValidTimesMatcher.group(3)));
            var endDay = startDay;
            if(dailyValidTimesMatcher.group(4) != null) {
                endDay = DayOfWeek.from(dayOfWeekFormatter.parse(dailyValidTimesMatcher.group(4)));
            }
            var daysOfWeek = EnumSet.range(startDay, endDay);
            dailyValidTimes.add(new DailyValidTimes(daysOfWeek, List.of(validHours)));
        }
        return new ValidTime(dailyValidTimes);
    }
}
