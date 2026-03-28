package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.labs.CertifiedLabsApi;
import platform.qa.pojo.labs.Laboratory;

import org.junit.jupiter.api.Test;

public class CertifiedLabsApiTest {
    CertifiedLabsApi certifiedLabsApi = mock(CertifiedLabsApi.class);
    Laboratory laboratoryResponse = Laboratory.builder().laboratoryId("id").subjectId("subjectId").build();

    @Test
    public void getToLaboratoryTest() {
        when(certifiedLabsApi.getToLaboratory("labId")).thenReturn(laboratoryResponse);

        var laboratory = certifiedLabsApi.getToLaboratory("labId");

        assertThat(laboratory).isEqualTo(laboratoryResponse);
    }
}
