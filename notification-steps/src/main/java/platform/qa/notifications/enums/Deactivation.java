package platform.qa.notifications.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Deactivation {
    USER_DEACTIVATED("USER_DEACTIVATED");
    private final String reason;
}
