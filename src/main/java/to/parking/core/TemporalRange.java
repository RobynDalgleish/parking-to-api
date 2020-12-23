package to.parking.core;

import lombok.Value;

import java.time.temporal.TemporalAccessor;

@Value
public class TemporalRange<T extends TemporalAccessor> {

    T start;
    T end;
}
