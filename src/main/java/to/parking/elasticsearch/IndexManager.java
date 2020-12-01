package to.parking.elasticsearch;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import to.parking.app.Clock;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions.add;
import static org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions.removeIndex;
import static org.elasticsearch.client.RequestOptions.DEFAULT;

@Singleton
class IndexManager {

    private static final String INDEX_ALIAS = "to-parking";
    private static final String INDEX_NAME_PREFIX = INDEX_ALIAS + "-";
    private static final int NUMBER_OF_SHARDS = 1;
    // Set to 0 for now to reduce cost. This will mean we do not have a backup if the primary goes down, but will cost half because we will only
    // have one node. This will also make health of the index yellow.
    private static final int NUMBER_OF_REPLICAS = 0;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final RestHighLevelClient esClient;
    private final Clock clock;

    @Inject
    IndexManager(RestHighLevelClient esClient, Clock clock) {
        this.esClient = esClient;
        this.clock = clock;
    }

    String createNewIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME_PREFIX + clock.time().format(dateFormatter));
        request.settings(Settings.builder()
            .put("index.number_of_shards", NUMBER_OF_SHARDS)
            .put("index.number_of_replicas", NUMBER_OF_REPLICAS)
            .put("index.refresh_interval", -1)
            .build()
        );
        return esClient.indices().create(request, DEFAULT).index();
    }

    void refreshAndPublishIndex(String indexName) throws IOException {

        // Refresh the index to make all documents searchable
        esClient.indices().refresh(new RefreshRequest(indexName), DEFAULT);

        var switchoverOperation = new IndicesAliasesRequest();

        // Remove existing associations
        esClient.indices()
            .getAlias(new GetAliasesRequest(INDEX_ALIAS), DEFAULT)
            .getAliases().keySet()
            .stream()
            .map(aliasedIndex -> removeIndex().index(aliasedIndex))
            .forEach(switchoverOperation::addAliasAction);

        // Associate new index
        switchoverOperation.addAliasAction(
            add().alias(INDEX_ALIAS).index(indexName)
        );

        esClient.indices().updateAliases(switchoverOperation, DEFAULT);
    }

}
