package to.parking.utils;

import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElasticsearchExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    private final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer(
        DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch").withTag("7.9.3")
    ).withExposedPorts(9200);

    private RestHighLevelClient restHighLevelClient;

    @Override
    public void beforeAll(ExtensionContext context) {
        elasticsearchContainer.start();
        restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(elasticsearchContainer.getHost(), elasticsearchContainer.getMappedPort(9200))
            )
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        restHighLevelClient.indices().delete(new DeleteIndexRequest("*"), RequestOptions.DEFAULT);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        restHighLevelClient.close();
        elasticsearchContainer.stop();
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    @SneakyThrows(IOException.class)
    public List<SearchHit> readAllFromIndex(String index) {

        var readAll = new ArrayList<SearchHit>();

        final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(5L));
        var searchRequest = new SearchRequest(index)
            .source(SearchSourceBuilder.searchSource().size(100))
            .scroll(scroll);
        String scrollId = null;

        try {

            var searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            var searchHits = searchResponse.getHits().getHits();
            while (searchHits != null && searchHits.length > 0) {

                readAll.addAll(Arrays.asList(searchHits));

                var searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
            }

        } catch (IOException e) {

            throw new UncheckedIOException("Failed to process scroll.", e);
        } finally {

            if (scrollId != null) {
                var clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }
        }

        return readAll;
    }
}
