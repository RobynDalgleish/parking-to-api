package to.parking;

import io.micronaut.http.HttpStatus;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import to.parking.fixture.ParkingDataFixture;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ingestion Component Tests")
class IngestionComponentTest extends ComponentTest {

    @Test
    @DisplayName("Can successfully ingest a CSV file")
    void canSuccessfullyIngestACsvFile() {

        var fixture = ParkingDataFixture.standard();

        var httpResponse = httpClient.toBlocking().exchange(fixture.getIngestionRequest());

        assertThat((Object) httpResponse.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

        var searchHits = elasticsearchExtension.readAllFromIndex("to-parking");
        assertThat(searchHits)
            .extracting(SearchHit::getSourceAsMap)
            .asList()
            .containsExactlyElementsOf(fixture.getParkingSpotDocuments());
    }
}
