package platform.qa.registry.management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionCandidateInfoRebaseResponse extends VersionCandidateInfoResponse {
    private String latestUpdate;
}