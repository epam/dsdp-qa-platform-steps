package platform.qa.datafactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import platform.qa.data.common.ExcerptApi;

import org.junit.jupiter.api.Test;

public class ExcerptApiTest {
    ExcerptApi excerptApi = mock(ExcerptApi.class);

    @Test
    public void getStatusOfExportTest() {
        when(excerptApi.getStatusOfExport("id")).thenReturn("status");

        var statusOfExport = excerptApi.getStatusOfExport("id");

        assertThat(statusOfExport).isEqualTo("status");
    }

}
