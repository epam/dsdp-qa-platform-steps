package platform.qa.settings.pojo.request;

import lombok.Data;
import lombok.ToString;
import lombok.Value;

@Data
@ToString
@Value(staticConstructor = "of")
public class Address {
    String address;
}