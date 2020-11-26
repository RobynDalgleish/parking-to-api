package to.parking.app;

import lombok.extern.slf4j.Slf4j;
import to.parking.core.ParkingData;

import javax.inject.Singleton;

@Slf4j
@Singleton
public class IngestionOrchestrator {

    public void ingestParkingData(ParkingData parkingData) {
        try {
            parkingData.read().forEach((parkingSpot) -> log.info(parkingSpot.toString()));
        } catch (Exception e) {
            log.error("failed to read CSV file", e);
        }
    }
}