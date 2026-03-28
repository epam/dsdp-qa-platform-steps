package platform.qa.constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Constants Tests")
public class ConstantsTest {

  @Nested
  @DisplayName("Constant Values Tests")
  class ConstantValuesTests {

    @Test
    @DisplayName("Should have correct COOKIE_HEADER_NAME value")
    public void shouldHaveCorrectCookieHeaderNameValue() {
      assertThat(Constants.COOKIE_HEADER_NAME).isEqualTo("Cookie");
    }

    @Test
    @DisplayName("Should have correct X_ACCESS_TOKEN_HEADER value")
    public void shouldHaveCorrectXAccessTokenHeaderValue() {
      assertThat(Constants.X_ACCESS_TOKEN_HEADER).isEqualTo("X-Access-Token");
    }

    @Test
    @DisplayName("Should have correct COOKIE_HEADER_VALUE value")
    public void shouldHaveCorrectCookieHeaderValueValue() {
      assertThat(Constants.COOKIE_HEADER_VALUE).isEqualTo("XSRF-TOKEN=Token");
    }

    @Test
    @DisplayName("Should have correct XSRF_HEADER_NAME value")
    public void shouldHaveCorrectXsrfHeaderNameValue() {
      assertThat(Constants.XSRF_HEADER_NAME).isEqualTo("X-XSRF-TOKEN");
    }

    @Test
    @DisplayName("Should have correct XSRF_HEADER_VALUE value")
    public void shouldHaveCorrectXsrfHeaderValueValue() {
      assertThat(Constants.XSRF_HEADER_VALUE).isEqualTo("Token");
    }

    @Test
    @DisplayName("Should have correct CONTENT_TYPE_HEADER value")
    public void shouldHaveCorrectContentTypeHeaderValue() {
      assertThat(Constants.CONTENT_TYPE_HEADER).isEqualTo("Content-Type");
    }

    @Test
    @DisplayName("Should have correct CONTENT_TYPE_VALUE value")
    public void shouldHaveCorrectContentTypeValueValue() {
      assertThat(Constants.CONTENT_TYPE_VALUE).isEqualTo("application/json");
    }

    @Test
    @DisplayName("Should have correct ID value")
    public void shouldHaveCorrectIdValue() {
      assertThat(Constants.ID).isEqualTo("/{id}");
    }
  }

  @Nested
  @DisplayName("Utility Class Tests")
  class UtilityClassTests {

    @Test
    @DisplayName("Should throw IllegalStateException when trying to instantiate Constants class")
    public void shouldThrowIllegalStateExceptionWhenTryingToInstantiateConstantsClass()
        throws NoSuchMethodException {
      Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
      constructor.setAccessible(true);

      assertThatThrownBy(constructor::newInstance)
          .isInstanceOf(InvocationTargetException.class)
          .hasCauseInstanceOf(IllegalStateException.class)
          .hasRootCauseMessage("This is utility class!");
    }

    @Test
    @DisplayName("Should have private constructor")
    public void shouldHavePrivateConstructor() throws NoSuchMethodException {
      Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
      assertThat(constructor.getModifiers()).isEqualTo(2); // 2 = private modifier
    }

    @Test
    @DisplayName("Should be final class")
    public void shouldBeFinalClass() {
      assertThat(Constants.class.getModifiers()).isEqualTo(17); // 17 = public + final
    }
  }

  @Nested
  @DisplayName("Constants Immutability Tests")
  class ConstantsImmutabilityTests {

    @Test
    @DisplayName("Should have all constants as static final")
    public void shouldHaveAllConstantsAsStaticFinal() throws NoSuchFieldException {
      // Test COOKIE_HEADER_NAME
      var cookieHeaderNameField = Constants.class.getField("COOKIE_HEADER_NAME");
      assertThat(cookieHeaderNameField.getModifiers())
          .isEqualTo(25); // 25 = public + static + final

      // Test X_ACCESS_TOKEN_HEADER
      var xAccessTokenHeaderField = Constants.class.getField("X_ACCESS_TOKEN_HEADER");
      assertThat(xAccessTokenHeaderField.getModifiers()).isEqualTo(25);

      // Test COOKIE_HEADER_VALUE
      var cookieHeaderValueField = Constants.class.getField("COOKIE_HEADER_VALUE");
      assertThat(cookieHeaderValueField.getModifiers()).isEqualTo(25);

      // Test XSRF_HEADER_NAME
      var xsrfHeaderNameField = Constants.class.getField("XSRF_HEADER_NAME");
      assertThat(xsrfHeaderNameField.getModifiers()).isEqualTo(25);

      // Test XSRF_HEADER_VALUE
      var xsrfHeaderValueField = Constants.class.getField("XSRF_HEADER_VALUE");
      assertThat(xsrfHeaderValueField.getModifiers()).isEqualTo(25);

      // Test CONTENT_TYPE_HEADER
      var contentTypeHeaderField = Constants.class.getField("CONTENT_TYPE_HEADER");
      assertThat(contentTypeHeaderField.getModifiers()).isEqualTo(25);

      // Test CONTENT_TYPE_VALUE
      var contentTypeValueField = Constants.class.getField("CONTENT_TYPE_VALUE");
      assertThat(contentTypeValueField.getModifiers()).isEqualTo(25);

      // Test ID
      var idField = Constants.class.getField("ID");
      assertThat(idField.getModifiers()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should have String type for all constants")
    public void shouldHaveStringTypeForAllConstants() throws NoSuchFieldException {
      assertThat(Constants.class.getField("COOKIE_HEADER_NAME").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("X_ACCESS_TOKEN_HEADER").getType())
          .isEqualTo(String.class);
      assertThat(Constants.class.getField("COOKIE_HEADER_VALUE").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("XSRF_HEADER_NAME").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("XSRF_HEADER_VALUE").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("CONTENT_TYPE_HEADER").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("CONTENT_TYPE_VALUE").getType()).isEqualTo(String.class);
      assertThat(Constants.class.getField("ID").getType()).isEqualTo(String.class);
    }
  }

  @Nested
  @DisplayName("Constants Usage Tests")
  class ConstantsUsageTests {

    @Test
    @DisplayName("Should be able to use constants in concatenation")
    public void shouldBeAbleToUseConstantsInConcatenation() {
      String fullCookieHeader = Constants.COOKIE_HEADER_NAME + ": " + Constants.COOKIE_HEADER_VALUE;
      assertThat(fullCookieHeader).isEqualTo("Cookie: XSRF-TOKEN=Token");

      String fullXsrfHeader = Constants.XSRF_HEADER_NAME + ": " + Constants.XSRF_HEADER_VALUE;
      assertThat(fullXsrfHeader).isEqualTo("X-XSRF-TOKEN: Token");

      String fullContentTypeHeader =
          Constants.CONTENT_TYPE_HEADER + ": " + Constants.CONTENT_TYPE_VALUE;
      assertThat(fullContentTypeHeader).isEqualTo("Content-Type: application/json");
    }

    @Test
    @DisplayName("Should be able to use ID constant for path building")
    public void shouldBeAbleToUseIdConstantForPathBuilding() {
      String basePath = "/api/excerpts";
      String fullPath = basePath + Constants.ID;
      assertThat(fullPath).isEqualTo("/api/excerpts/{id}");
    }

    @Test
    @DisplayName("Should have non-null constant values")
    public void shouldHaveNonNullConstantValues() {
      assertThat(Constants.COOKIE_HEADER_NAME).isNotNull();
      assertThat(Constants.X_ACCESS_TOKEN_HEADER).isNotNull();
      assertThat(Constants.COOKIE_HEADER_VALUE).isNotNull();
      assertThat(Constants.XSRF_HEADER_NAME).isNotNull();
      assertThat(Constants.XSRF_HEADER_VALUE).isNotNull();
      assertThat(Constants.CONTENT_TYPE_HEADER).isNotNull();
      assertThat(Constants.CONTENT_TYPE_VALUE).isNotNull();
      assertThat(Constants.ID).isNotNull();
    }

    @Test
    @DisplayName("Should have non-empty constant values")
    public void shouldHaveNonEmptyConstantValues() {
      assertThat(Constants.COOKIE_HEADER_NAME).isNotEmpty();
      assertThat(Constants.X_ACCESS_TOKEN_HEADER).isNotEmpty();
      assertThat(Constants.COOKIE_HEADER_VALUE).isNotEmpty();
      assertThat(Constants.XSRF_HEADER_NAME).isNotEmpty();
      assertThat(Constants.XSRF_HEADER_VALUE).isNotEmpty();
      assertThat(Constants.CONTENT_TYPE_HEADER).isNotEmpty();
      assertThat(Constants.CONTENT_TYPE_VALUE).isNotEmpty();
      assertThat(Constants.ID).isNotEmpty();
    }
  }
}
