package platform.qa.notifications.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.qa.entities.IEntity;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpData implements Serializable, IEntity {
    private String address;
    private String verificationCode;
}
