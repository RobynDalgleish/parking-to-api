package to.parking.core;

import lombok.Value;

import java.time.Duration;

@Value
public class ParkingSpot {
    Integer id;
    String streetName;
    String streetSideCardinal;
    String streetSegment;
    String validTime;
    String permittedTime;
    Duration permittedDuration;
    String startZone;
    String endZone;
    String cleanValidTime;
    Boolean anytimeWeekend;
    Boolean exception;
}
