package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.consent.PersonalProfileApi;
import platform.qa.entities.Subject;

import org.junit.jupiter.api.Test;

public class PersonalProfileApiTest {

    PersonalProfileApi personalProfileApi = mock(PersonalProfileApi.class);
    Subject subjectResponse = Subject.builder().subjectId("id").subjectCode("code").subjectName("name").build();

    @Test
    public void getPersonByIdTest() {
        when(personalProfileApi.getPersonById("personalProfileId")).thenReturn(subjectResponse);

        var person = personalProfileApi.getPersonById("personalProfileId");

        assertThat(person).isEqualTo(subjectResponse);
    }
}
