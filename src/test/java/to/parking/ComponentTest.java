package to.parking;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import to.parking.utils.ElasticsearchExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
abstract class ComponentTest {

    @RegisterExtension
    final ElasticsearchExtension elasticsearchExtension = new ElasticsearchExtension();

    private ApplicationContext applicationContext;
    protected EmbeddedServer embeddedServer;
    protected HttpClient httpClient;

    @BeforeAll
    void setUp() {
        applicationContext = ApplicationContext.builder()
            .properties(getProperties())
            .build();
        getSingletons().forEach(applicationContext::registerSingleton);
        embeddedServer = applicationContext.start()
            .getBean(EmbeddedServer.class)
            .start();
        httpClient = HttpClient.create(embeddedServer.getURL());
    }

    @AfterAll
    void afterAll() {
        httpClient.close();
        embeddedServer.stop();
    }

    protected Map<String, Object> getProperties() {
        return new HashMap<>();
    }

    protected List<Object> getSingletons() {
        var singletons = new ArrayList<>();
        singletons.add(elasticsearchExtension.getRestHighLevelClient());
        return singletons;
    }
}
