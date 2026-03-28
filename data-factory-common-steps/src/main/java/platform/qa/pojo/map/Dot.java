package platform.qa.pojo.map;

import lombok.*;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Dot {
	private Double longitude;
	private Double latitude;
}
