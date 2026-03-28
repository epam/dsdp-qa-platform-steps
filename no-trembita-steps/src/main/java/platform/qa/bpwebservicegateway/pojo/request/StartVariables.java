package platform.qa.bpwebservicegateway.pojo.request;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartVariables{
	private String eduname;
	private String edrpou;
	private String json;
}