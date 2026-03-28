package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.common.SubjectApi;
import platform.qa.entities.Subject;

import org.junit.jupiter.api.Test;

public class SubjectApiTest {
    SubjectApi subjectApi = mock(SubjectApi.class);
    Subject subjectResponse = Subject
            .builder()
            .subjectId("id")
            .subjectCode("code")
            .build();

    @Test
    public void getUserSubjectTest() {
        when(subjectApi.getUserSubject("id")).thenReturn(subjectResponse);

        var userSubject = subjectApi.getUserSubject("id");

        assertThat(userSubject.getSubjectId()).isEqualTo("id");
        assertThat(userSubject.getSubjectCode()).isEqualTo("code");
    }
}
