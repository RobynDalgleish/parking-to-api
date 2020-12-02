package to.parking.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import to.parking.app.Indexer;
import to.parking.core.ParkingData;
import to.parking.core.ParkingSpot;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

@Singleton
public class ElasticsearchIndexer implements Indexer {

    private final RestHighLevelClient restHighLevelClient;
    private final IndexManager indexManager;
    private final ObjectMapper objectMapper;

    @Inject
    public ElasticsearchIndexer(
        RestHighLevelClient restHighLevelClient,
        IndexManager indexManager,
        ObjectMapper objectMapper
    ) {
        this.restHighLevelClient = restHighLevelClient;
        this.indexManager = indexManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void index(ParkingData parkingData) {
        try {
            var indexName = indexManager.createNewIndex();
            BulkProcessor bulkProcessor = createBulkProcessor();

            parkingData.read()
                .map(parkingSpot -> convertToIndexRequest(indexName, parkingSpot))
                .forEach(bulkProcessor::add);
            bulkProcessor.awaitClose(1, TimeUnit.MINUTES);

            indexManager.refreshAndPublishIndex(indexName);
        } catch (Exception e) {
            // TODO: create custom exception
            throw new RuntimeException("Failed to bulk index", e);
        }
    }

    private IndexRequest convertToIndexRequest(String indexName, ParkingSpot parkingSpot) {
        // TODO: add validation maybe for null ids
        try {
            return new IndexRequest(indexName)
                .id(String.valueOf(parkingSpot.getId()))
                .source(objectMapper.writeValueAsBytes(parkingSpot), XContentType.JSON);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BulkProcessor createBulkProcessor() {
        return BulkProcessor.builder(
            (bulkRequest, bulkResponseActionListener) -> restHighLevelClient.bulkAsync(
                bulkRequest,
                RequestOptions.DEFAULT,
                bulkResponseActionListener
            ),
            new LoggingBulkProcessorListener()
        ).build();
    }
}
