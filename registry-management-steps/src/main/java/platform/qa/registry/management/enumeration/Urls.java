package platform.qa.registry.management.enumeration;

public enum Urls {

    //Master forms
    GET_MASTER_VERSIONS("/versions/master/"),
    GET_FORM_LIST_FOR_MASTER_VERSION("/versions/master/forms"),
    GET_FORM_CONTENT_FOR_MASTER_VERSION("/versions/master/forms/{formName}"),
    CREATE_FORM_FOR_MASTER_VERSION("/versions/master/forms/{formName}"),
    UPDATE_FORM_FOR_MASTER_VERSION("/versions/master/forms/{formName}"),
    DELETE_FORM_FOR_MASTER_VERSION("/versions/master/forms/{formName}"),

    //Version forms
    GET_VERSION_CANDIDATES("/versions/candidates"),
    CREATE_VERSION_CANDIDATE("/versions/candidates"),
    GET_VERSION_CANDIDATES_BY_ID("/versions/candidates/{id}"),
    GET_FORM_LIST_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/forms"),
    GET_FORM_CONTENT_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/forms/{formName}"),
    CREATE_FORM_CONTENT_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/forms/{formName}"),
    UPDATE_FORM_FOR_SPECIFIC_VERSION("versions/candidates/{id}/forms/{formName}"),
    DELETE_FORM_FOR_SPECIFIC_VERSION("versions/candidates/{id}/forms/{formName}"),
    SUBMIT_VERSION_CANDIDATE("/versions/candidates/{id}/submit"),
    GET_CHANGES_VERSION_CANDIDATE("/versions/candidates/{id}/changes"),

    //Rebase
    REBASE_VERSION_CANDIDATE("/versions/candidates/{id}/rebase"),

    //Master business processes
    GET_BP_LIST_FROM_MATER_VERSION("/versions/master/business-processes"),
    GET_BP_CONTENT_FROM_MASTER_VERSION("/versions/master/business-processes/{bpName}"),
    CREATE_BP_FOR_MASTER_VERSION("/versions/master/business-processes/{bpName}"),
    UPDATE_BP_FOR_MASTER_VERSION("/versions/master/business-processes/{bpName}"),
    DELETE_BP_FOR_MASTER_VERSION("/versions/master/business-processes/{bpName}"),

    //Version candidate business processes
    GET_BP_LIST_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-processes"),
    GET_BP_CONTENT_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-processes/{bpName}"),
    CREATE_BP_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-processes/{bpName}"),
    UPDATE_BP_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-processes/{bpName}"),
    DELETE_BP_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-processes/{bpName}"),

    //Master settings
    GET_GLOBAL_SETTINGS("/versions/master/settings"),

    //Version settings
    GET_GLOBAL_SETTINGS_VERSION_CANDIDATE("/versions/candidates/{id}/settings"),
    UPDATE_GLOBAL_SETTINGS_VERSION_CANDIDATE("/versions/candidates/{id}/settings"),

    //Rollback
    BP_ROLLBACK_FOR_VERSION_CANDIDATE("/versions/candidates/{id}/business-processes/{bpName}/rollback"),
    FORM_ROLLBACK_FOR_VERSION_CANDIDATE("/versions/candidates/{id}/forms/{formName}/rollback"),
    BP_GROUPING_ROLLBACK_FOR_VERSION_CANDIDATE("/versions/candidates/{id}/business-process-groups/rollback"),
    TABLES_ROLLBACK_FOR_VERSION_CANDIDATE("/versions/candidates/{id}/data-model/tables/rollback"),
    TRANSLATION_ROLLBACK_FOR_VERSION_CANDIDATE("/versions/candidates/{id}/i18n/{languageName}/rollback"),

    //Master tables
    GET_TABLES_LIST_FROM_MATER_VERSION("/versions/master/tables"),
    GET_TABLE_FROM_MATER_VERSION("/versions/master/tables/{tableName}"),

    //Version tables
    GET_TABLES_LIST_VERSION_CANDIDATE("/versions/candidates/{id}/tables"),
    GET_TABLE_VERSION_CANDIDATE("/versions/candidates/{id}/tables/{tableName}"),
    UPDATE_DATA_MODEL_CONTENT_VERSION_CANDIDATE("/versions/candidates/{id}/data-model/tables"),

    //Master business process groups
    GET_BP_GROUPS_FROM_MASTER_VERSION("/versions/master/business-process-groups"),

    //Version business process groups
    GET_BP_GROUPS_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-process-groups"),
    CREATE_BP_GROUPS_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/business-process-groups"),

    //Master registry translations
    GET_TRANSLATIONS_LIST_MASTER_VERSION("/versions/master/i18n"),
    GET_TRANSLATION_MASTER_VERSION("/versions/master/i18n/{languageName}"),
    CREATE_TRANSLATION_MASTER_VERSION("/versions/master/i18n/{languageName}"),
    UPDATE_TRANSLATION_MASTER_VERSION("/versions/master/i18n/{languageName}"),
    DELETE_TRANSLATION_MASTER_VERSION("/versions/master/i18n/{languageName}"),

    //Version registry translations
    GET_TRANSLATIONS_LIST_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/i18n"),
    GET_TRANSLATION_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/i18n/{languageName}"),
    CREATE_TRANSLATION_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/i18n/{languageName}"),
    UPDATE_TRANSLATION_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/i18n/{languageName}"),
    DELETE_TRANSLATION_FOR_SPECIFIC_VERSION("/versions/candidates/{id}/i18n/{languageName}");

    private String url;

    Urls(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
