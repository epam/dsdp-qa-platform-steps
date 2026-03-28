package platform.qa.pojo.weapon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import platform.qa.entities.IEntity;
import platform.qa.pojo.common.FileData;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Weapon implements IEntity {

    private String weaponId;
    private String type;
    private String brand;
    private String model;
    private int manufactureYear;
    private String registrationDate;
    private String ballisticTest;
    private FileData medicalCertificate;
    private String name;
    private String contacts;
    private String rnokpp;

}
