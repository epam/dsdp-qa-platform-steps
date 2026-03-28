package platform.qa.data.common;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import platform.qa.entities.IEntity;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.redis.JedisClient;

class SignatureStepsTest {

  MockedConstruction<JedisClient> jedisMockConstruction;
  private WireMockServer wireMock;
  private Service dataFactory;
  private Service digitalOps;

  @BeforeEach
  void setup() {
    jedisMockConstruction =
        mockConstruction(
            JedisClient.class,
            (mock, context) -> {
              try {
                when(mock.hset(anyString(), anyString(), anyString())).thenReturn(0L);
              } catch (Throwable ignored) {
                doNothing().when(mock).hset(anyString(), anyString(), anyString());
              }

              doNothing().when(mock).close();
            });

    wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");

    dataFactory = new Service("http://localhost:" + wireMock.port(), user);
    digitalOps = new Service("http://localhost:" + wireMock.port(), user);

    wireMock.stubFor(
        post(urlEqualTo("/api/eseal/sign"))
            .willReturn(okJson("{\"signature\": \"signed-value\"}")));
  }

  @AfterEach
  void tearDown() {
    jedisMockConstruction.close();
    wireMock.stop();
  }

  @Test
  void testSignRequest_success() {
    SignatureSteps steps =
        new SignatureSteps(dataFactory, digitalOps, List.of(new Redis("host", "pwd")));

    String key = steps.signRequest(new TestEntity("test"));

    assertThat(key).isNotNull();

    JedisClient jedis = jedisMockConstruction.constructed().get(0);
    verify(jedis, atLeastOnce()).hset(anyString(), anyString(), anyString());

    wireMock.verify(postRequestedFor(urlEqualTo("/api/eseal/sign")));
  }

  @Test
  void testSignDeleteRequest_success() {
    SignatureSteps steps =
        new SignatureSteps(dataFactory, digitalOps, List.of(new Redis("host", "pwd")));

    String key = steps.signDeleteRequest("123");

    assertThat(key).isNotNull();

    JedisClient jedis = jedisMockConstruction.constructed().get(0);
    verify(jedis, atLeastOnce()).hset(anyString(), anyString(), anyString());

    wireMock.verify(postRequestedFor(urlEqualTo("/api/eseal/sign")));
  }

  @Test
  void testSignRequestPayloads_success() {
    SignatureSteps steps =
        new SignatureSteps(dataFactory, digitalOps, List.of(new Redis("host", "pwd")));

    String key = steps.signRequestPayloads(new TestEntity("test"));

    assertThat(key).isNotNull();

    JedisClient jedis = jedisMockConstruction.constructed().get(0);
    verify(jedis, atLeastOnce()).hset(anyString(), anyString(), anyString());

    wireMock.verify(postRequestedFor(urlEqualTo("/api/eseal/sign")));
  }

  record TestEntity(String value) implements IEntity {}
}
