package to.parking.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import lombok.extern.slf4j.Slf4j;
import to.parking.app.IngestionOrchestrator;
import to.parking.core.ParkingData;

import javax.inject.Inject;
import java.io.IOException;

@Slf4j
@Controller
class IngestionController {

    private final IngestionOrchestrator ingestionOrchestrator;

    @Inject
    public IngestionController(IngestionOrchestrator ingestionOrchestrator) {

        this.ingestionOrchestrator = ingestionOrchestrator;
    }

    @Post("/api/ingestion")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    HttpResponse<?> ingestParkingData(@Part("data") CompletedFileUpload completedFileUpload) throws IOException {
        log.info("received file for ingestion {} of {} bytes", completedFileUpload.getFilename(), completedFileUpload.getSize());
        ingestionOrchestrator.ingestParkingData(new ParkingData(completedFileUpload.getInputStream()));
        return HttpResponse.noContent();
    }
}