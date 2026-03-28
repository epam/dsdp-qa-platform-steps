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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "error",
  "traceId",
  "code",
  "message",
  "messageKey",
  "messageParameters",
  "type",
  "title",
  "detail",
  "instance"
})
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
  // Nested error object (for wrapped responses)
  @JsonProperty("error")
  private ErrorDetails error;

  // Direct fields (for flat responses)
  @JsonProperty("traceId")
  private String traceId;

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("messageKey")
  private String messageKey;

  @JsonProperty("messageParameters")
  private MessageParameters messageParameters;

  @JsonProperty("path")
  private String path;

  @JsonProperty("details")
  private Details details;

  // RFC 7807 Problem Details fields
  @JsonProperty("type")
  private String type;

  @JsonProperty("title")
  private String title;

  @JsonProperty("detail")
  private String detail;

  @JsonProperty("instance")
  private String instance;

  // Convenience methods that work with both flat and nested structures
  public String getTraceId() {
    return error != null ? error.getTraceId() : traceId;
  }

  public String getCode() {
    return error != null ? error.getCode() : code;
  }

  public String getMessage() {
    return error != null ? error.getMessage() : message;
  }

  public String getMessageKey() {
    return error != null ? error.getMessageKey() : messageKey;
  }

  // Inner class for nested error structure
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ErrorDetails {
    @JsonProperty("traceId")
    private String traceId;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("messageKey")
    private String messageKey;

    @JsonProperty("localizedMessage")
    private String localizedMessage;
  }
}
