package to.parking.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
class IngestionController {

    @Post("/api/ingestion")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    HttpResponse<?> ingestParkingData(@Part("data") CompletedFileUpload completedFileUpload){
        log.info("received file for ingestion {} of {} bytes", completedFileUpload.getFilename(), completedFileUpload.getSize());
        return HttpResponse.noContent();
    }
}
