package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.common.SubjectSearchConditions;
import platform.qa.pojo.common.SubjectSettings;

import java.util.List;
import org.junit.jupiter.api.Test;

public class SubjectSearchConditionsTest {
    SubjectSearchConditions subjectSearchConditions = mock(SubjectSearchConditions.class);
    List<SubjectSettings> subjectSettingsResponse = List.of(
            SubjectSettings.builder().subjectId("subjectId1").settingsId("settingsId1").build()
    );

    @Test
    public void subjectSettingsTest() {
        when(subjectSearchConditions.subjectSettings("settingsId1")).thenReturn(subjectSettingsResponse);

        var subjectSettings =subjectSearchConditions.subjectSettings("settingsId1");

        assertThat(subjectSettings.size()).isEqualTo(1);
        assertThat(subjectSettings.get(0)).isEqualTo(subjectSettingsResponse.get(0));
    }
}
