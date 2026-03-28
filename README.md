# platform-qa-steps


### Overview

* The main purpose of the platform-qa-steps library is to provide aggregated steps to work with clients and platform api;
* bpms-steps implemented steps to work with bpms service;
* ceph-client-steps implemented steps to work with ceph storage;
* data-factory-common-steps implemented steps to work with data factory service;
* document-service-steps implemented steps to work with document service;
* form-management-steps implemented steps to work with form management service;
* operation-steps implemented steps to work with form management service;
* process-history-steps implemented steps to work with process history service;
* redash-steps implemented steps to work with redash service.

### Usage
Use this steps in platform or registry tests when integration with appropriate
third-party is needed. Steps is combined api or clients actions to use in tests.

### Test execution

* Tests could be run via maven command:
    * `mvn verify` OR using appropriate functions of your IDE.

### License

The platform-qa-steps is Open Source software released under
the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).