package to.parking.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;

@Slf4j
public class LoggingBulkProcessorListener implements BulkProcessor.Listener {

    @Override
    public void beforeBulk(long executionId, BulkRequest bulkRequest) {
        log.info("Sending bulk request {} with {} number of items", executionId, bulkRequest.numberOfActions());
    }

    @Override
    public void afterBulk(long executionId, BulkRequest bulkRequest, BulkResponse bulkResponse) {
        log.info("Bulk request {} completed in {} millis", executionId, bulkResponse.getTook().getMillis());
        if (bulkResponse.hasFailures()) {
            log.error(bulkResponse.buildFailureMessage());
        }
    }

    @Override
    public void afterBulk(long executionId, BulkRequest bulkRequest, Throwable throwable) {
        log.error("Bulk request {} FAILED", executionId, throwable);
    }
}