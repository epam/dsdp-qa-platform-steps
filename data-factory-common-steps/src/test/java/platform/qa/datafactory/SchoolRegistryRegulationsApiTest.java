package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.school.SchoolRegistryRegulationsApi;
import platform.qa.pojo.school.EducationalType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class SchoolRegistryRegulationsApiTest {
    SchoolRegistryRegulationsApi schoolRegistryRegulationsApi = mock(SchoolRegistryRegulationsApi.class);
    List<EducationalType> educationalTypesResponse = List.of(
            EducationalType.builder().eduTypeId("id1").name("educationType1").build(),
            EducationalType.builder().eduTypeId("id2").name("educationType2").build(),
            EducationalType.builder().eduTypeId("id3").name("educationType3").build()
    );

    @Test
    public void getAllEducationalTypeTest() {
        AtomicInteger iterator = new AtomicInteger(0);

        when(schoolRegistryRegulationsApi.getAllEducationalType()).thenReturn(educationalTypesResponse);

        var allEducationTypes = schoolRegistryRegulationsApi.getAllEducationalType();

        assertThat(allEducationTypes.size()).isEqualTo(3);
        allEducationTypes.forEach(educationType->
                        assertThat(educationType).isEqualTo(educationalTypesResponse.get(iterator.getAndIncrement()))
        );
    }
}
