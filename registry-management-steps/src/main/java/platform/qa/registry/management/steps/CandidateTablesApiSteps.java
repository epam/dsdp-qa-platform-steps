package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_TABLES_LIST_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.GET_TABLE_VERSION_CANDIDATE;
import static platform.qa.registry.management.enumeration.Urls.UPDATE_DATA_MODEL_CONTENT_VERSION_CANDIDATE;

import io.restassured.http.ContentType;
import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.table.Table;
import platform.qa.registry.management.dto.response.table.TableData;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class CandidateTablesApiSteps extends BaseStep {

    public CandidateTablesApiSteps(Service service) {
        super(service);
    }

    public List<Table> getTablesList(String version) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TABLES_LIST_VERSION_CANDIDATE.getUrl().replace(ID, version),
                        null,
                        new TypeReference<List<Table>>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public TableData getSpecificTableFullDetails(String version, String tableName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TABLE_VERSION_CANDIDATE.getUrl().replace(ID, version).replace(TABLE_NAME, tableName),
                        null,
                        new TypeReference<TableData>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public String updateTables(String version, String content) {
        return new RestClientProxy(service)
                .positiveRequest(ContentType.XML)
                .put(UPDATE_DATA_MODEL_CONTENT_VERSION_CANDIDATE.getUrl().replace(ID, version),
                        null,
                        content,
                        new TypeReference<String>() {}.getType(),
                        HttpStatus.SC_OK,
                        null
                );
    }

}
