package to.parking.app;

import lombok.extern.slf4j.Slf4j;
import to.parking.core.ParkingData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class IngestionOrchestrator {

    private final Indexer indexer;

    @Inject
    public IngestionOrchestrator(Indexer indexer) {
        this.indexer = indexer;
    }

    public void ingestParkingData(ParkingData parkingData) {
        indexer.index(parkingData);
    }
}