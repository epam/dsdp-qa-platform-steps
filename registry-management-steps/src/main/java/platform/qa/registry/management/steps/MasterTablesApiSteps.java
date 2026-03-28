package platform.qa.registry.management.steps;

import static platform.qa.registry.management.enumeration.Urls.GET_TABLES_LIST_FROM_MATER_VERSION;
import static platform.qa.registry.management.enumeration.Urls.GET_TABLE_FROM_MATER_VERSION;

import platform.qa.entities.Service;
import platform.qa.registry.management.dto.response.table.Table;
import platform.qa.registry.management.dto.response.table.TableData;
import platform.qa.registry.management.steps.api.BaseStep;
import platform.qa.rest.client.impl.RestClientProxy;

import java.util.List;
import org.apache.http.HttpStatus;
import com.fasterxml.jackson.core.type.TypeReference;

public class MasterTablesApiSteps extends BaseStep {

    public MasterTablesApiSteps(Service service) {
        super(service);
    }

    public List<Table> getTablesList() {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TABLES_LIST_FROM_MATER_VERSION.getUrl(),
                        null,
                        new TypeReference<List<Table>>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

    public TableData getSpecificTableFullDetails(String tableName) {
        return new RestClientProxy(service)
                .positiveRequest()
                .get(GET_TABLE_FROM_MATER_VERSION.getUrl().replace(TABLE_NAME, tableName),
                        null,
                        new TypeReference<TableData>() {
                        }.getType(),
                        HttpStatus.SC_OK
                );
    }

}
