package platform.qa.keycloak.enums;

public enum UserAttributes {
    DRFO("drfo"),
    EDRPOU("edrpou"),
    FULL_NAME("fullName"),
    KATOTTG("KATOTTG"),
    SUBJECT_TYPE("subjectType");

    private String attributeName;

    UserAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
