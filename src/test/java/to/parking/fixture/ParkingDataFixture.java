package to.parking.fixture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;
import lombok.Getter;
import lombok.SneakyThrows;
import to.parking.core.ParkingData;
import to.parking.core.ParkingSpot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class ParkingDataFixture {

    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final ParkingData parkingData;
    private final List<ParkingSpot> parkingSpots;
    private final List<Map<String, Object>> parkingSpotDocuments;
    private final HttpRequest<MultipartBody> ingestionRequest;

    @SneakyThrows(IOException.class)
    private ParkingDataFixture(
        Path rawDataFile,
        Path expexctedDocumentsFile
    ) {
        this.parkingData = new ParkingData(Files.newInputStream(rawDataFile));
        this.parkingSpots = objectMapper.readValue(expexctedDocumentsFile.toFile(), new TypeReference<>() {});
        this.parkingSpotDocuments = objectMapper.readValue(expexctedDocumentsFile.toFile(), new TypeReference<>() {});
        var file = rawDataFile.toFile();
        this.ingestionRequest = HttpRequest.create(HttpMethod.POST, "/api/ingestion")
            .contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
            .body(
                MultipartBody.builder()
                    .addPart("data", file.getName(), MediaType.MULTIPART_FORM_DATA_TYPE, file)
                    .build()
            );
    }

    public static ParkingDataFixture standard() {
        return new ParkingDataFixture(
            readFromClasspath("/data/toronto.csv"),
            readFromClasspath("/data/toronto.json")
        );
    }

    @SneakyThrows({ URISyntaxException.class, IOException.class })
    private static Path readFromClasspath(String name) {
        var resource = Optional.ofNullable(ParkingDataFixture.class.getResource(name))
            .orElseThrow(() -> new FileNotFoundException(name + " could not be found."));
        return Paths.get(resource.toURI());
    }
}
