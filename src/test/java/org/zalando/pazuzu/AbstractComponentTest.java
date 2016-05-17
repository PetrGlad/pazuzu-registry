package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractComponentTest {

    @Value("${local.server.port}")
    private int port;

    protected final String featuresUrl = "/api/features";

    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    protected ResponseEntity<FeatureFullDto> createFeature(String name, String dockerData, String testInstruction, String... dependencies) throws JsonProcessingException {
        final ResponseEntity<FeatureFullDto> response = createFeatureUnchecked(FeatureFullDto.class, name, dockerData, testInstruction, dependencies);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected <T> ResponseEntity<T> createFeatureUnchecked(Class<T> clazz, String name, String dockerData, String testInstruction, String... dependencies) throws JsonProcessingException {
        Map<String, Object> map = getFeaturePropertiesMap(name, dockerData, testInstruction, dependencies);

        return template.postForEntity(url(featuresUrl), new HttpEntity<>(mapper.writeValueAsString(map),
                contentType(MediaType.APPLICATION_JSON)), clazz);
    }

    protected HttpHeaders contentType(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    protected Map<String, Object> getFeaturePropertiesMap(String name, String dockerData, String testInstruction, String ... dependencies) {
        Map<String, Object> map = new HashMap<>();
        if (null != name) {
            map.put("name", name);
        }
        if (null != dockerData) {
            map.put("docker_data", dockerData);
        }
        if (null != testInstruction) {
            map.put("test_instruction", testInstruction);
        }
        map.put("dependencies", Arrays.asList(dependencies));

        return map;
    }
}
