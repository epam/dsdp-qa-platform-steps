package platform.qa.keycloak.pojo.request;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchEqualsAttributes {
    private List<String> edrpou;
    private List<String> drfo;
    private List<String> fullName;
    @JsonProperty("KATOTTG")
    private List<String> katottg;
}
