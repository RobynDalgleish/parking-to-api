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


    // Regular Expression Patterns for matching
    private static final Pattern twelveHourTimePattern = Pattern.compile("((?:1[0-2]|0[1-9]):[0-5][0-9] (?:am|pm))");
    private static final Pattern timeRangePattern = Pattern.compile(twelveHourTimePattern + "-" + twelveHourTimePattern);
    private static final Pattern dayOfWeekPattern = Pattern.compile("(Mon|Tue|Wed|Thu|Fri|Sat|Sun)");
    private static final Pattern dayRangePattern = Pattern.compile(dayOfWeekPattern + "(?:-" + dayOfWeekPattern + ")?");
    private static final Pattern dailyValidTimesPattern = Pattern.compile(timeRangePattern + "(?:, " + timeRangePattern + ")*, " + dayRangePattern);

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

        return new ValidTime(
            parseDailyValidTimes(input)
        );
    }

    private List<DailyValidTimes> parseDailyValidTimes(String input) {

        var dailyValidTimes = new ArrayList<DailyValidTimes>();
        var dailyValidTimesMatcher = dailyValidTimesPattern.matcher(input);

        while (dailyValidTimesMatcher.find()) {

            var timeRanges = new ArrayList<TemporalRange<LocalTime>>();
            var dailyValidTimeString = input.substring(dailyValidTimesMatcher.start(), dailyValidTimesMatcher.end());
            var timeRangeMatcher = timeRangePattern.matcher(dailyValidTimeString);

            while (timeRangeMatcher.find()) {
                timeRanges.add(
                    new TemporalRange<>(
                        LocalTime.parse(timeRangeMatcher.group(1), twelveHourTimeFormatter),
                        LocalTime.parse(timeRangeMatcher.group(2), twelveHourTimeFormatter)
                    )
                );
            }

            var daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
            var dayRangeMatcher = dayRangePattern.matcher(dailyValidTimeString);
            if (dayRangeMatcher.find()) {

                var startDay = DayOfWeek.from(dayOfWeekFormatter.parse(dayRangeMatcher.group(1)));
                var endDay = startDay;
                if (dayRangeMatcher.group(2) != null) {
                    endDay = DayOfWeek.from(dayOfWeekFormatter.parse(dayRangeMatcher.group(2)));
                }
                daysOfWeek = EnumSet.range(startDay, endDay);
            }

            dailyValidTimes.add(new DailyValidTimes(daysOfWeek, timeRanges));
        }

        return dailyValidTimes;
    }
}
