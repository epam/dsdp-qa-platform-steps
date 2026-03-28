package platform.qa.data.weapon;

import lombok.extern.log4j.Log4j2;
import platform.qa.entities.Service;
import platform.qa.pojo.weapon.sc.AllOwnersByManufactureYear;
import platform.qa.rest.RestApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;

@Log4j2
public class WeaponSC {

    private final Service service;

    private final String ALL_OWNERS_BY_MANUFACTURE_YEAR = "find-all-owners-search-manufacture-year";

    public WeaponSC(Service service) {
        this.service = service;
    }

    public List<AllOwnersByManufactureYear> getOwnersBySingleManufactureYear(int manufactureYear) {
        log.info("Запит до all_owners_by_manufacture_year для року: {}", manufactureYear);

        Map<String, String> body = new HashMap<>();
        body.put("manufactureYear", String.valueOf(manufactureYear));

        return new RestApiClient(service)
                .sendGetWithParams(ALL_OWNERS_BY_MANUFACTURE_YEAR, body)
                .extract()
                .as(new TypeToken<List<AllOwnersByManufactureYear>>() {}.getType());
    }

    public List<AllOwnersByManufactureYear> getOwnersBySeveralManufactureYears(List<Integer> manufactureYears) {
        log.info("Запит до all_owners_by_manufacture_year");

        String query = String.valueOf(manufactureYears.stream()
                .map(year -> "manufactureYear=" + year)
                .collect(Collectors.joining("&")));

        return new RestApiClient(service)
                .get(ALL_OWNERS_BY_MANUFACTURE_YEAR.concat("?").concat(query))
                .then()
                .extract()
                .as(new TypeToken<List<AllOwnersByManufactureYear>>() {}.getType());
    }
}
