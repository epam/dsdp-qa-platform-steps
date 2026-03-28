package platform.qa.registry.management.steps.api;

import platform.qa.entities.Service;

public abstract class BaseStep {
    protected final Service service;

    protected static final String FORM_NAME = "{formName}";
    protected static final String BP_NAME = "{bpName}";
    protected static final String ID = "{id}";
    protected static final String TABLE_NAME = "{tableName}";
    protected static final String LANGUAGE_NAME = "{languageName}";

    public BaseStep(Service service) {
        this.service = service;
    }
}
