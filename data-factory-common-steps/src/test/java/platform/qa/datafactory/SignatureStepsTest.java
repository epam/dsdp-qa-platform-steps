package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import platform.qa.data.common.SignatureSteps;
import platform.qa.entities.Redis;
import platform.qa.entities.Service;
import platform.qa.redis.JedisClient;
import platform.qa.rest.RestApiClient;

class SignatureStepsTest {

  @Mock private Service digitalOps;
  @Mock private Response responseMock;
  @Mock private ResponseBody bodyMock;
  @Mock private JsonPath jsonPathMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(digitalOps.getUrl()).thenReturn("http://mock-url");
  }

  @Test
  void testSignRequest_ShouldWriteToRedisAndReturnKey() {
    try (MockedConstruction<JedisClient> mockedRedis =
            mockConstruction(
                JedisClient.class,
                (mock, ctx) -> {
                  when(mock.hset(anyString(), anyString(), anyString())).thenReturn(1L);
                  doNothing().when(mock).close();
                });
        MockedConstruction<RestApiClient> mockedRest =
            mockConstruction(
                RestApiClient.class,
                (mock, ctx) ->
                    when(mock.postNegative(any(), anyString())).thenReturn(responseMock))) {

      // === mock RestAssured ===
      when(responseMock.body()).thenReturn(bodyMock);
      when(bodyMock.asString()).thenReturn("{\"signature\":\"mocked-signature\"}");
      when(responseMock.jsonPath()).thenReturn(jsonPathMock);
      when(jsonPathMock.getString(anyString())).thenReturn("mocked-signature");
      // ===============================

      SignatureSteps steps =
          new SignatureSteps(digitalOps, List.of(new Redis("redis://fake", "fake-p")));

      String key = steps.signRequest(new DummyPayload("test"));
      assertThat(key).isNotNull();

      JedisClient client = mockedRedis.constructed().get(0);
      verify(client, atLeastOnce())
          .hset(startsWith(SignatureSteps.BPM_FORM_SUBMISSIONS), anyString(), anyString());
      verify(client).close();
    }
  }

  @Test
  void testSignRequestPayloads_ShouldUseDifferentPrefix() {
    try (MockedConstruction<JedisClient> mockedRedis =
            mockConstruction(
                JedisClient.class,
                (mock, ctx) -> {
                  when(mock.hset(anyString(), anyString(), anyString())).thenReturn(1L);
                  doNothing().when(mock).close();
                });
        MockedConstruction<RestApiClient> mockedRest =
            mockConstruction(
                RestApiClient.class,
                (mock, ctx) ->
                    when(mock.postNegative(any(), anyString())).thenReturn(responseMock))) {

      when(responseMock.body()).thenReturn(bodyMock);
      when(bodyMock.asString()).thenReturn("{\"signature\":\"mocked-signature\"}");
      when(responseMock.jsonPath()).thenReturn(jsonPathMock);
      when(jsonPathMock.getString(anyString())).thenReturn("mocked-signature");

      SignatureSteps steps =
          new SignatureSteps(digitalOps, List.of(new Redis("redis://fake", "fake-p")));

      String key = steps.signRequestPayloads(new DummyPayload("data"));
      assertThat(key).isNotNull();

      JedisClient client = mockedRedis.constructed().get(0);
      verify(client, atLeastOnce())
          .hset(startsWith("bpm-form-payloads:"), anyString(), anyString());
      verify(client).close();
    }
  }

  @Test
  void testSignDeleteRequest_ShouldSignDeletePayload() {
    try (MockedConstruction<JedisClient> mockedRedis =
            mockConstruction(
                JedisClient.class,
                (mock, ctx) -> {
                  when(mock.hset(anyString(), anyString(), anyString())).thenReturn(1L);
                  doNothing().when(mock).close();
                });
        MockedConstruction<RestApiClient> mockedRest =
            mockConstruction(
                RestApiClient.class,
                (mock, ctx) ->
                    when(mock.postNegative(any(), anyString())).thenReturn(responseMock))) {

      when(responseMock.body()).thenReturn(bodyMock);
      when(bodyMock.asString()).thenReturn("{\"signature\":\"mocked-signature\"}");
      when(responseMock.jsonPath()).thenReturn(jsonPathMock);
      when(jsonPathMock.getString(anyString())).thenReturn("mocked-signature");

      SignatureSteps steps =
          new SignatureSteps(digitalOps, List.of(new Redis("redis://fake", "fake-p")));

      String key = steps.signDeleteRequest("123");
      assertThat(key).isNotEmpty();

      JedisClient client = mockedRedis.constructed().get(0);
      verify(client, atLeastOnce())
          .hset(startsWith(SignatureSteps.BPM_FORM_SUBMISSIONS), anyString(), anyString());
      verify(client).close();
    }
  }

  private record DummyPayload(String value) {}
}
