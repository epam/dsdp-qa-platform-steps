/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("ErrorResponse Tests")
public class ErrorResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("Flat Structure Tests")
    class FlatStructureTests {

        @Test
        @DisplayName("Should set and get flat traceId field")
        public void shouldSetAndGetFlatTraceId() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTraceId("trace-123");

            assertThat(errorResponse.getTraceId()).isEqualTo("trace-123");
        }

        @Test
        @DisplayName("Should set and get flat code field")
        public void shouldSetAndGetFlatCode() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode("ERR-001");

            assertThat(errorResponse.getCode()).isEqualTo("ERR-001");
        }

        @Test
        @DisplayName("Should set and get flat message field")
        public void shouldSetAndGetFlatMessage() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Error occurred");

            assertThat(errorResponse.getMessage()).isEqualTo("Error occurred");
        }

        @Test
        @DisplayName("Should set and get flat messageKey field")
        public void shouldSetAndGetFlatMessageKey() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessageKey("error.key");

            assertThat(errorResponse.getMessageKey()).isEqualTo("error.key");
        }

        @Test
        @DisplayName("Should set and get path field")
        public void shouldSetAndGetPath() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setPath("/api/documents");

            assertThat(errorResponse.getPath()).isEqualTo("/api/documents");
        }

        @Test
        @DisplayName("Should set and get messageParameters field")
        public void shouldSetAndGetMessageParameters() {
            ErrorResponse errorResponse = new ErrorResponse();
            MessageParameters params = new MessageParameters();
            errorResponse.setMessageParameters(params);

            assertThat(errorResponse.getMessageParameters()).isEqualTo(params);
        }

        @Test
        @DisplayName("Should set and get details field")
        public void shouldSetAndGetDetails() {
            ErrorResponse errorResponse = new ErrorResponse();
            Details details = new Details();
            errorResponse.setDetails(details);

            assertThat(errorResponse.getDetails()).isEqualTo(details);
        }

        @Test
        @DisplayName("Should deserialize flat JSON structure")
        public void shouldDeserializeFlatJsonStructure() throws JsonProcessingException {
            String json = """
                {
                    "traceId": "trace-456",
                    "code": "ERR-002",
                    "message": "Validation failed",
                    "messageKey": "validation.error",
                    "path": "/api/upload"
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            assertThat(errorResponse.getTraceId()).isEqualTo("trace-456");
            assertThat(errorResponse.getCode()).isEqualTo("ERR-002");
            assertThat(errorResponse.getMessage()).isEqualTo("Validation failed");
            assertThat(errorResponse.getMessageKey()).isEqualTo("validation.error");
            assertThat(errorResponse.getPath()).isEqualTo("/api/upload");
        }
    }

    @Nested
    @DisplayName("Nested Structure Tests")
    class NestedStructureTests {

        @Test
        @DisplayName("Should set and get nested error object")
        public void shouldSetAndGetNestedError() {
            ErrorResponse errorResponse = new ErrorResponse();
            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setTraceId("nested-trace");
            errorDetails.setCode("NESTED-001");
            errorDetails.setMessage("Nested error");
            errorDetails.setMessageKey("nested.error.key");
            errorDetails.setLocalizedMessage("Error localizado");

            errorResponse.setError(errorDetails);

            assertThat(errorResponse.getError()).isNotNull();
            assertThat(errorResponse.getError().getTraceId()).isEqualTo("nested-trace");
            assertThat(errorResponse.getError().getCode()).isEqualTo("NESTED-001");
            assertThat(errorResponse.getError().getMessage()).isEqualTo("Nested error");
            assertThat(errorResponse.getError().getMessageKey()).isEqualTo("nested.error.key");
            assertThat(errorResponse.getError().getLocalizedMessage()).isEqualTo("Error localizado");
        }

        @Test
        @DisplayName("Should deserialize nested JSON structure")
        public void shouldDeserializeNestedJsonStructure() throws JsonProcessingException {
            String json = """
                {
                    "error": {
                        "traceId": "nested-trace-789",
                        "code": "NESTED-003",
                        "message": "Nested validation failed",
                        "messageKey": "nested.validation.error",
                        "localizedMessage": "Validación fallida"
                    }
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            assertThat(errorResponse.getError()).isNotNull();
            assertThat(errorResponse.getError().getTraceId()).isEqualTo("nested-trace-789");
            assertThat(errorResponse.getError().getCode()).isEqualTo("NESTED-003");
            assertThat(errorResponse.getError().getMessage()).isEqualTo("Nested validation failed");
            assertThat(errorResponse.getError().getMessageKey()).isEqualTo("nested.validation.error");
            assertThat(errorResponse.getError().getLocalizedMessage()).isEqualTo("Validación fallida");
        }
    }

    @Nested
    @DisplayName("Convenience Methods Tests")
    class ConvenienceMethodsTests {

        @Test
        @DisplayName("Should return nested traceId when error object is present")
        public void shouldReturnNestedTraceIdWhenErrorPresent() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTraceId("flat-trace");

            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setTraceId("nested-trace");
            errorResponse.setError(errorDetails);

            assertThat(errorResponse.getTraceId()).isEqualTo("nested-trace");
        }

        @Test
        @DisplayName("Should return flat traceId when error object is null")
        public void shouldReturnFlatTraceIdWhenErrorNull() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTraceId("flat-trace");

            assertThat(errorResponse.getTraceId()).isEqualTo("flat-trace");
        }

        @Test
        @DisplayName("Should return nested code when error object is present")
        public void shouldReturnNestedCodeWhenErrorPresent() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode("FLAT-001");

            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setCode("NESTED-001");
            errorResponse.setError(errorDetails);

            assertThat(errorResponse.getCode()).isEqualTo("NESTED-001");
        }

        @Test
        @DisplayName("Should return flat code when error object is null")
        public void shouldReturnFlatCodeWhenErrorNull() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode("FLAT-001");

            assertThat(errorResponse.getCode()).isEqualTo("FLAT-001");
        }

        @Test
        @DisplayName("Should return nested message when error object is present")
        public void shouldReturnNestedMessageWhenErrorPresent() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Flat message");

            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setMessage("Nested message");
            errorResponse.setError(errorDetails);

            assertThat(errorResponse.getMessage()).isEqualTo("Nested message");
        }

        @Test
        @DisplayName("Should return flat message when error object is null")
        public void shouldReturnFlatMessageWhenErrorNull() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Flat message");

            assertThat(errorResponse.getMessage()).isEqualTo("Flat message");
        }

        @Test
        @DisplayName("Should return nested messageKey when error object is present")
        public void shouldReturnNestedMessageKeyWhenErrorPresent() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessageKey("flat.key");

            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setMessageKey("nested.key");
            errorResponse.setError(errorDetails);

            assertThat(errorResponse.getMessageKey()).isEqualTo("nested.key");
        }

        @Test
        @DisplayName("Should return flat messageKey when error object is null")
        public void shouldReturnFlatMessageKeyWhenErrorNull() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessageKey("flat.key");

            assertThat(errorResponse.getMessageKey()).isEqualTo("flat.key");
        }

        @Test
        @DisplayName("Should return null when both nested and flat values are null")
        public void shouldReturnNullWhenBothValuesNull() {
            ErrorResponse errorResponse = new ErrorResponse();

            assertThat(errorResponse.getTraceId()).isNull();
            assertThat(errorResponse.getCode()).isNull();
            assertThat(errorResponse.getMessage()).isNull();
            assertThat(errorResponse.getMessageKey()).isNull();
        }
    }

    @Nested
    @DisplayName("RFC 7807 Problem Details Tests")
    class Rfc7807Tests {

        @Test
        @DisplayName("Should set and get type field")
        public void shouldSetAndGetType() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setType("https://example.com/probs/out-of-credit");

            assertThat(errorResponse.getType()).isEqualTo("https://example.com/probs/out-of-credit");
        }

        @Test
        @DisplayName("Should set and get title field")
        public void shouldSetAndGetTitle() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTitle("You do not have enough credit");

            assertThat(errorResponse.getTitle()).isEqualTo("You do not have enough credit");
        }

        @Test
        @DisplayName("Should set and get detail field")
        public void shouldSetAndGetDetail() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setDetail("Your current balance is 30, but that costs 50");

            assertThat(errorResponse.getDetail()).isEqualTo("Your current balance is 30, but that costs 50");
        }

        @Test
        @DisplayName("Should set and get instance field")
        public void shouldSetAndGetInstance() {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setInstance("/account/12345/msgs/abc");

            assertThat(errorResponse.getInstance()).isEqualTo("/account/12345/msgs/abc");
        }

        @Test
        @DisplayName("Should deserialize RFC 7807 compliant JSON")
        public void shouldDeserializeRfc7807Json() throws JsonProcessingException {
            String json = """
                {
                    "type": "https://example.com/probs/validation-error",
                    "title": "Validation Error",
                    "detail": "The request body contains invalid data",
                    "instance": "/api/documents/123"
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            assertThat(errorResponse.getType()).isEqualTo("https://example.com/probs/validation-error");
            assertThat(errorResponse.getTitle()).isEqualTo("Validation Error");
            assertThat(errorResponse.getDetail()).isEqualTo("The request body contains invalid data");
            assertThat(errorResponse.getInstance()).isEqualTo("/api/documents/123");
        }
    }

    @Nested
    @DisplayName("ErrorDetails Inner Class Tests")
    class ErrorDetailsTests {

        @Test
        @DisplayName("Should create ErrorDetails instance")
        public void shouldCreateErrorDetailsInstance() {
            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();

            assertThat(errorDetails).isNotNull();
        }

        @Test
        @DisplayName("Should set and get all ErrorDetails fields")
        public void shouldSetAndGetAllErrorDetailsFields() {
            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setTraceId("detail-trace-001");
            errorDetails.setCode("DETAIL-CODE-001");
            errorDetails.setMessage("Detail message");
            errorDetails.setMessageKey("detail.message.key");
            errorDetails.setLocalizedMessage("Mensaje detallado");

            assertThat(errorDetails.getTraceId()).isEqualTo("detail-trace-001");
            assertThat(errorDetails.getCode()).isEqualTo("DETAIL-CODE-001");
            assertThat(errorDetails.getMessage()).isEqualTo("Detail message");
            assertThat(errorDetails.getMessageKey()).isEqualTo("detail.message.key");
            assertThat(errorDetails.getLocalizedMessage()).isEqualTo("Mensaje detallado");
        }
    }

    @Nested
    @DisplayName("JSON Serialization Tests")
    class JsonSerializationTests {

        @Test
        @DisplayName("Should serialize ErrorResponse with flat structure")
        public void shouldSerializeErrorResponseWithFlatStructure() throws JsonProcessingException {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTraceId("serialize-trace-001");
            errorResponse.setCode("SERIALIZE-001");
            errorResponse.setMessage("Serialization test");
            errorResponse.setMessageKey("serialize.key");

            String json = objectMapper.writeValueAsString(errorResponse);

            assertThat(json).contains("serialize-trace-001");
            assertThat(json).contains("SERIALIZE-001");
            assertThat(json).contains("Serialization test");
            assertThat(json).contains("serialize.key");
        }

        @Test
        @DisplayName("Should serialize ErrorResponse with nested structure")
        public void shouldSerializeErrorResponseWithNestedStructure() throws JsonProcessingException {
            ErrorResponse errorResponse = new ErrorResponse();
            ErrorResponse.ErrorDetails errorDetails = new ErrorResponse.ErrorDetails();
            errorDetails.setTraceId("nested-serialize-trace");
            errorDetails.setCode("NESTED-SERIALIZE-001");
            errorDetails.setMessage("Nested serialization test");
            errorDetails.setMessageKey("nested.serialize.key");
            errorResponse.setError(errorDetails);

            String json = objectMapper.writeValueAsString(errorResponse);

            assertThat(json).contains("error");
            assertThat(json).contains("nested-serialize-trace");
            assertThat(json).contains("NESTED-SERIALIZE-001");
            assertThat(json).contains("Nested serialization test");
            assertThat(json).contains("nested.serialize.key");
        }

        @Test
        @DisplayName("Should not include null fields in serialization")
        public void shouldNotIncludeNullFieldsInSerialization() throws JsonProcessingException {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setCode("MINIMAL-001");

            String json = objectMapper.writeValueAsString(errorResponse);

            assertThat(json).contains("MINIMAL-001");
            assertThat(json).doesNotContain("traceId");
            assertThat(json).doesNotContain("message");
            assertThat(json).doesNotContain("messageKey");
        }

        @Test
        @DisplayName("Should ignore unknown properties during deserialization")
        public void shouldIgnoreUnknownPropertiesDuringDeserialization() throws JsonProcessingException {
            String json = """
                {
                    "code": "TEST-001",
                    "message": "Test message",
                    "unknownField1": "should be ignored",
                    "unknownField2": 12345
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            assertThat(errorResponse.getCode()).isEqualTo("TEST-001");
            assertThat(errorResponse.getMessage()).isEqualTo("Test message");
        }
    }

    @Nested
    @DisplayName("Complex Scenario Tests")
    class ComplexScenarioTests {

        @Test
        @DisplayName("Should handle mixed flat and nested structure")
        public void shouldHandleMixedFlatAndNestedStructure() throws JsonProcessingException {
            String json = """
                {
                    "error": {
                        "traceId": "nested-mixed-trace",
                        "code": "NESTED-MIXED-001",
                        "message": "Nested mixed message",
                        "messageKey": "nested.mixed.key"
                    },
                    "type": "https://example.com/probs/mixed-error",
                    "title": "Mixed Error",
                    "path": "/api/mixed"
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            // Convenience methods should return nested values
            assertThat(errorResponse.getTraceId()).isEqualTo("nested-mixed-trace");
            assertThat(errorResponse.getCode()).isEqualTo("NESTED-MIXED-001");
            assertThat(errorResponse.getMessage()).isEqualTo("Nested mixed message");
            assertThat(errorResponse.getMessageKey()).isEqualTo("nested.mixed.key");

            // RFC 7807 fields should be accessible
            assertThat(errorResponse.getType()).isEqualTo("https://example.com/probs/mixed-error");
            assertThat(errorResponse.getTitle()).isEqualTo("Mixed Error");
            assertThat(errorResponse.getPath()).isEqualTo("/api/mixed");
        }

        @Test
        @DisplayName("Should handle complete error response with all fields")
        public void shouldHandleCompleteErrorResponseWithAllFields() throws JsonProcessingException {
            String json = """
                {
                    "error": {
                        "traceId": "complete-nested-trace",
                        "code": "COMPLETE-NESTED-001",
                        "message": "Complete nested message",
                        "messageKey": "complete.nested.key",
                        "localizedMessage": "Mensaje completo anidado"
                    },
                    "traceId": "complete-flat-trace",
                    "code": "COMPLETE-FLAT-001",
                    "message": "Complete flat message",
                    "messageKey": "complete.flat.key",
                    "path": "/api/complete",
                    "type": "https://example.com/probs/complete-error",
                    "title": "Complete Error",
                    "detail": "This is a complete error response with all fields",
                    "instance": "/api/complete/123"
                }
                """;

            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

            // Nested values should take precedence
            assertThat(errorResponse.getTraceId()).isEqualTo("complete-nested-trace");
            assertThat(errorResponse.getCode()).isEqualTo("COMPLETE-NESTED-001");
            assertThat(errorResponse.getMessage()).isEqualTo("Complete nested message");
            assertThat(errorResponse.getMessageKey()).isEqualTo("complete.nested.key");

            // All other fields should be accessible
            assertThat(errorResponse.getPath()).isEqualTo("/api/complete");
            assertThat(errorResponse.getType()).isEqualTo("https://example.com/probs/complete-error");
            assertThat(errorResponse.getTitle()).isEqualTo("Complete Error");
            assertThat(errorResponse.getDetail()).isEqualTo("This is a complete error response with all fields");
            assertThat(errorResponse.getInstance()).isEqualTo("/api/complete/123");
            assertThat(errorResponse.getError().getLocalizedMessage()).isEqualTo("Mensaje completo anidado");
        }
    }
}
