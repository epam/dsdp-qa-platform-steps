package platform.qa.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.operators.steps.OperatorSteps;
import platform.qa.operators.steps.entities.FilterGerritChanges;

import java.util.List;
import org.junit.jupiter.api.Test;

public class OperatorStepsTest {
    OperatorSteps operatorSteps = mock(OperatorSteps.class);
    List<String> podsToBeReady = List.of(
            "pod1", "pod2", "pod3"
    );
    FilterGerritChanges filterGerritChanges = FilterGerritChanges.hiddenBuilder().project("project").build();
    String changeIdByFilterResponse = "changeId";

    @Test
    public void waitForPodsToBeReadyTest() {
        doNothing().when(operatorSteps).waitForPodsToBeReady(podsToBeReady);

        operatorSteps.waitForPodsToBeReady(podsToBeReady);
    }

    @Test
    public void getChangeIdByFilterTest() {
        when(operatorSteps.getChangeIdByFilter(filterGerritChanges)).thenReturn(changeIdByFilterResponse);

        var changeIdByFilter = operatorSteps.getChangeIdByFilter(filterGerritChanges);

        assertThat(changeIdByFilter).isEqualTo(changeIdByFilterResponse);
    }
}
