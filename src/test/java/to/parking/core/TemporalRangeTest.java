package to.parking.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TemporalRange Tests")
class TemporalRangeTest {

    @ParameterizedTest(name = "{0} < {1}")
    @MethodSource("startCanBeBeforeEndArguments")
    @DisplayName("Start can be before end")
    <T extends TemporalAccessor & Comparable<T>> void startCanBeBeforeEnd(T start, T end) {

        assertThatCode(() -> new TemporalRange<>(start, end))
            .doesNotThrowAnyException();
    }

    private Stream<Arguments> startCanBeBeforeEndArguments() {
        return Stream.of(
            Arguments.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY),
            Arguments.of(Month.JUNE, Month.NOVEMBER),
            Arguments.of(LocalTime.of(10, 33), LocalTime.of(14, 48)),
            Arguments.of(LocalDate.of(2020, 2, 12), LocalDate.of(2020, 6, 16)),
            Arguments.of(
                ZonedDateTime.of(LocalDate.of(2020, 2, 12), LocalTime.of(10, 33), ZoneId.of("GMT")),
                ZonedDateTime.of(LocalDate.of(2020, 6, 16), LocalTime.of(14, 48), ZoneId.of("GMT"))

            )
        );
    }

    @ParameterizedTest(name = "{0} == {0}")
    @MethodSource("startCanBeEqualToEndArguments")
    @DisplayName("Start can be equal to end")
    <T extends TemporalAccessor & Comparable<T>> void startCanBeEqualToEnd(T start) {

        assertThatCode(() -> new TemporalRange<>(start, start))
            .doesNotThrowAnyException();
    }

    private Stream<Arguments> startCanBeEqualToEndArguments() {
        return Stream.of(
            Arguments.of(DayOfWeek.MONDAY),
            Arguments.of(Month.JUNE),
            Arguments.of(LocalTime.of(10, 33)),
            Arguments.of(LocalDate.of(2020, 2, 12)),
            Arguments.of(ZonedDateTime.of(LocalDate.of(2020, 2, 12), LocalTime.of(10, 33), ZoneId.of("GMT")))
        );
    }

    @ParameterizedTest(name = "{0} > {1}")
    @MethodSource("startCannotBeAfterEndArguments")
    @DisplayName("Start cannot be after end")
    <T extends TemporalAccessor & Comparable<T>> void startCannotBeAfterEnd(T start, T end) {

        assertThatCode(() -> new TemporalRange<>(start, end))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Temporal range start must not be after end.");
    }

    private Stream<Arguments> startCannotBeAfterEndArguments() {
        return Stream.of(
            Arguments.of(DayOfWeek.THURSDAY, DayOfWeek.MONDAY),
            Arguments.of(Month.NOVEMBER, Month.JUNE),
            Arguments.of(LocalTime.of(14, 48), LocalTime.of(10, 33)),
            Arguments.of(LocalDate.of(2020, 6, 16), LocalDate.of(2020, 2, 12)),
            Arguments.of(
                ZonedDateTime.of(LocalDate.of(2020, 6, 16), LocalTime.of(14, 48), ZoneId.of("GMT")),
                ZonedDateTime.of(LocalDate.of(2020, 2, 12), LocalTime.of(10, 33), ZoneId.of("GMT"))
            )
        );
    }
}