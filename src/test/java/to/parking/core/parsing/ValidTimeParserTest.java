package to.parking.core.parsing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import to.parking.core.DailyValidTimes;
import to.parking.core.TemporalRange;
import to.parking.core.ValidTime;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ValidTimeParser Tests")
class ValidTimeParserTest {

    @ParameterizedTest(name = "Can parse ''{0}''")
    @MethodSource("parsesDailyValidTimesCorrectlyArguments")
    @DisplayName("Parses daily valid times correctly")
    void parsesDailyValidTimesCorrectly(String input, List<DailyValidTimes> expected) {

        var validTime = ValidTimeParser.parse(input);

        assertThat(validTime)
            .isNotNull()
            .extracting(ValidTime::getDailyValidTimes)
            .containsExactly(expected);
    }

    static Stream<Arguments> parsesDailyValidTimesCorrectlyArguments() {
        return Stream.of(
            Arguments.of(
                "10:00 am-06:00 pm, Mon-Fri",
                List.of(
                    new DailyValidTimes(
                        new TemporalRange<>(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                        List.of(new TemporalRange<>(LocalTime.of(10, 0), LocalTime.of(18, 0)))
                    )
                )
            ),
            Arguments.of(
                "11:00 am-02:00 pm, Sat",
                List.of(
                    new DailyValidTimes(
                        new TemporalRange<>(DayOfWeek.SATURDAY, DayOfWeek.SATURDAY),
                        List.of(new TemporalRange<>(LocalTime.of(11, 0), LocalTime.of(14, 0)))
                    )
                )
            ),
            Arguments.of(
                "09:00 am-10:00 am, Mon-Fri, 11:00 am-12:00 pm, Sat-Sun",
                List.of(
                    new DailyValidTimes(
                        new TemporalRange<>(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                        List.of(new TemporalRange<>(LocalTime.of(9, 0), LocalTime.of(10, 0)))
                    ),
                    new DailyValidTimes(
                        new TemporalRange<>(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                        List.of(new TemporalRange<>(LocalTime.of(11, 0), LocalTime.of(12, 0)))
                    )
                )
            ),
            Arguments.of(
                "08:00 am-09:00 am, 10:00 am-11:00 am, 12:00 pm-01:00 pm, Mon-Fri",
                List.of(
                    new DailyValidTimes(
                        new TemporalRange<>(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                        List.of(
                            new TemporalRange<>(LocalTime.of(8, 0), LocalTime.of(9, 0)),
                            new TemporalRange<>(LocalTime.of(10, 0), LocalTime.of(11, 0)),
                            new TemporalRange<>(LocalTime.of(12, 0), LocalTime.of(13, 0))
                        )
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "Can parse ''{0}''")
    @MethodSource("parsesAnytimeCorrectlyArguments")
    @DisplayName("Parses anytime correctly")
    void parsesAnytimeCorrectly(String input, Boolean isAnytime) {

        var validTime = ValidTimeParser.parse(input);

        assertThat(validTime).isNotNull();
        assertThat(validTime.getAnytime()).isEqualTo(isAnytime);
    }

    static Stream<Arguments> parsesAnytimeCorrectlyArguments() {
        return Stream.of(
            Arguments.of("10:00 am-06:00 pm, Mon-Fri", false),
            Arguments.of("Anytime", true)
        );
    }

    @Test
    @DisplayName("Parses empty")
    void parsesEmptyCorrectly() {
        assertThatThrownBy(() -> ValidTimeParser.parse("")).isInstanceOf(RuntimeException.class).hasMessage("Invalid valid time.");
    }
}