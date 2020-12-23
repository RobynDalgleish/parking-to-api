package to.parking.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ValidTimeParser Tests")
class ValidTimeParserTest {

    private ValidTimeParser validTimeParser;

    @BeforeEach
    void setUp() {
        validTimeParser = new ValidTimeParser();
    }

    @ParameterizedTest(name = "Can parse ''{0}''")
    @MethodSource("parsesDailyValidTimesCorrectlyArguments")
    @DisplayName("Parses daily valid times correctly")
    void parsesDailyValidTimesCorrectly(String input, List<DailyValidTimes> expected) {

        var validTime = validTimeParser.parse(input);

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
                        EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                        List.of(new TemporalRange<>(LocalTime.of(10, 0), LocalTime.of(18, 0)))
                    )
                )
            ),
            Arguments.of(
                "11:00 am-02:00 pm, Sat",
                List.of(
                    new DailyValidTimes(
                        EnumSet.of(DayOfWeek.SATURDAY),
                        List.of(new TemporalRange<>(LocalTime.of(11, 0), LocalTime.of(14, 0)))
                    )
                )
            )
        );
    }
}