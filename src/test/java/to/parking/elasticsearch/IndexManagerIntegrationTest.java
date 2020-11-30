package to.parking.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import to.parking.app.Clock;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.client.RequestOptions.DEFAULT;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
@TestInstance(PER_CLASS)
class IndexManagerIntegrationTest {

    IndexManager indexManager;
    RestHighLevelClient restHighLevelClient;

    @Container
    // TODO: expose a random port; right now its set to a 9200/9300 default
    private static final ElasticsearchContainer esContainer = new ElasticsearchContainer(
        DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch").withTag("7.9.3")
    ).withExposedPorts(9200);

    @BeforeEach
    public void setUp() {
        var clock = mock(Clock.class);
        when(clock.time()).thenReturn(OffsetDateTime.parse("2020-11-30T22:11:08Z"));

        restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(esContainer.getHost(), esContainer.getMappedPort(9200))));
        indexManager = new IndexManager(restHighLevelClient, clock);
    }

    @Test
    @DisplayName("A new index is created with appropriate settings")
    public void indexIsCreatedWithAppropriateSettings() throws Exception {
        var indexName = indexManager.prepareNewIndex();
        var settingRequest = new GetSettingsRequest().indices(indexName);
        Settings settings = restHighLevelClient.indices().getSettings(settingRequest, DEFAULT).getIndexToSettings().get(indexName);
        assertThat(settings.get("index.number_of_shards")).isEqualTo("1");
        assertThat(settings.get("index.number_of_replicas")).isEqualTo("0");
    }

    @Test
    @DisplayName("Creates predictable unique index name")
    public void createsPredictableUniqueIndexName() throws Exception {
        var indexName = indexManager.prepareNewIndex();
        assertThat(indexName).isEqualTo("to-parking-20201130221108");
    }
}