package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.labs.CertifiedLabsSearchConditions;

import org.junit.jupiter.api.Test;

public class CertifiedLabsSearchConditionsTest {
    CertifiedLabsSearchConditions certifiedLabsSearchConditions = mock(CertifiedLabsSearchConditions.class);

    @Test
    public void laboratoryEqualEdrpouNameCountTest() {
        when(certifiedLabsSearchConditions.laboratoryEqualEdrpouNameCount("labName", "labEdrpou"))
                .thenReturn("1");

        var laboratoryCount = certifiedLabsSearchConditions
                .laboratoryEqualEdrpouNameCount("labName", "labEdrpou");

        assertThat(laboratoryCount).isEqualTo("1");
    }
}
