package to.parking.core;

import lombok.Value;

import java.time.temporal.TemporalAccessor;

@Value
public class TemporalRange<T extends TemporalAccessor & Comparable<T>> {

    public TemporalRange(T start, T end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Temporal range start must not be after end.");
        }
        this.start = start;
        this.end = end;
    }

    T start;
    T end;
}
