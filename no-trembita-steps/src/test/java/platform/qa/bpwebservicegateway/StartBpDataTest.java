package platform.qa.bpwebservicegateway;

import static org.assertj.core.api.Assertions.assertThat;

import platform.qa.bpwebservicegateway.pojo.request.StartBpData;
import platform.qa.bpwebservicegateway.pojo.request.StartVariables;

import org.junit.jupiter.api.Test;

public class StartBpDataTest {

    @Test
    public void startBpDataBuilderTest() {
        String processKey = "test-business-process";
        StartVariables startVariables = StartVariables.builder().build();

        StartBpData startBpData = StartBpData.builder()
                .businessProcessDefinitionKey(processKey)
                .startVariables(startVariables)
                .build();

        assertThat(startBpData).isNotNull();
        assertThat(startBpData.getBusinessProcessDefinitionKey()).isEqualTo(processKey);
        assertThat(startBpData.getStartVariables()).isEqualTo(startVariables);
    }

    @Test
    public void startBpDataEqualsAndHashCodeTest() {
        String processKey = "test-process";
        StartVariables startVariables = StartVariables.builder().build();

        StartBpData startBpData1 = StartBpData.builder()
                .businessProcessDefinitionKey(processKey)
                .startVariables(startVariables)
                .build();

        StartBpData startBpData2 = StartBpData.builder()
                .businessProcessDefinitionKey(processKey)
                .startVariables(startVariables)
                .build();

        assertThat(startBpData1).isEqualTo(startBpData2);
        assertThat(startBpData1.hashCode()).isEqualTo(startBpData2.hashCode());
        assertThat(startBpData1.toString()).contains(processKey);
    }
}
