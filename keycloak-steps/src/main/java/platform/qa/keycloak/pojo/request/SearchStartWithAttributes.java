package platform.qa.keycloak.pojo.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchStartWithAttributes {
    private List<String> edrpou;
    private List<String> drfo;
    private List<String> fullName;
    @JsonProperty("KATOTTG")
    private List<String> katottg;
}
