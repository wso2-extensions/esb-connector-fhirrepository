# Error Handling in the FHIR Repository Connector

* If there is an error occurring in the connector, it will throw a synapse exception, and it will trigger the fault sequence which is inside the integration project.

### Fault sequence

* The integration studio project contains a fault sequence to create the operation outcome and serialize the operation.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<sequence name="FhirRepositoryFaultSequence" trace="disable" xmlns="http://ws.apache.org/ns/synapse">
    <log level="custom">
        <property expression="$ctx:_OH_PROP_FHIR_ERROR_DETAIL_CODE" name="_OH_PROP_FHIR_ERROR_DETAIL_CODE"/>
        <property expression="$ctx:_OH_PROP_FHIR_ERROR_DETAIL_DISPLAY" name="_OH_PROP_FHIR_ERROR_DETAIL_DISPLAY"/>
    </log>
    <fhirOperationOutcome.create>
        <objectId>FAULT_RESPONSE_OBJ</objectId>
        <issue.severity>{$ctx:_OH_PROP_FHIR_ERROR_SEVERITY_}</issue.severity>
        <issue.details.coding.system>{$ctx:_OH_PROP_FHIR_ERROR_DETAIL_SYSTEM_}</issue.details.coding.system>
        <issue.details.coding.code>{$ctx:_OH_PROP_FHIR_ERROR_DETAIL_CODE_}</issue.details.coding.code>
        <issue.details.coding.display>{$ctx:_OH_PROP_FHIR_ERROR_DETAIL_DISPLAY_}</issue.details.coding.display>
        <issue.diagnostics>{$ctx:_OH_PROP_FHIR_ERROR_DIAGNOSTICS_}</issue.diagnostics>
        <issue.code>{$ctx:_OH_PROP_FHIR_ERROR_CODE_}</issue.code>
    </fhirOperationOutcome.create>
    <fhirBase.serialize>
        <objectId>FAULT_RESPONSE_OBJ</objectId>
    </fhirBase.serialize>
    <respond/>
</sequence>

```
>Note : The fault sequence must be defined when creating a new API resource in the integration project.

##### Error properties

The following error properties will be populated according to the error that occurs in the connector.
* [_OH_PROP_FHIR_ERROR_SEVERITY](#_OH_PROP_FHIR_ERROR_SEVERITY)
* [_OH_PROP_FHIR_ERROR_CODE](#_OH_PROP_FHIR_ERROR_CODE)
* [_OH_PROP_FHIR_ERROR_DETAIL_CODE](#_OH_PROP_FHIR_ERROR_DETAIL_CODE)
* [_OH_PROP_FHIR_ERROR_DETAIL_SYSTEM](#_OH_PROP_FHIR_ERROR_DETAIL_SYSTEM)
* [_OH_PROP_FHIR_ERROR_DETAIL_DISPLAY](#_OH_PROP_FHIR_ERROR_DETAIL_DISPLAY)
* [_OH_PROP_FHIR_ERROR_DIAGNOSTICS](#_OH_PROP_FHIR_ERROR_DIAGNOSTICS)

##### _OH_PROP_FHIR_ERROR_SEVERITY

* This indicates whether the issue indicates a variation from successful processing.

* Following values can be assigned as OH_PROP_FHIR_ERROR_SEVERITY.

| Code        | Display     | Definition                                                                                                                                      |
|-------------|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| fatal       | Fatal       | The issue caused the action to fail and no further checking could be performed.                                                                 |
| error       | Error       | The issue is sufficiently important to cause the action to fail.                                                                                |
| warning     | Warning     | The issue is not important enough to cause the action to fail but may cause it to be performed suboptimally or in a way that is not as desired. |
| information | Information | The issue has no relation to the degree of success of the action                                                                                |

##### _OH_PROP_FHIR_ERROR_CODE

* This describes the type of the issue. The system that creates an OperationOutcome SHALL choose the most applicable code from the IssueType value set, and may additional provide its own code for the error in the details element.

* Following values can get as OH_PROP_FHIR_ERROR_CODE. 

| Code    | Display                  | Definition                                                                                                                                                                                                  |
|---------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| invalid | Invalid Content          | Content invalid against the specification or a profile                                                                                                                                                      ||
| login   | Login Required           | The client needs to initiate an authentication process.                                                                                                                                                     |

##### _OH_PROP_FHIR_ERROR_DETAIL_CODE

* This is a symbol in syntax defined by the system. The symbol may be a predefined code or an expression in a syntax defined by the coding system.

* Ex: If the Code is 'invalid' you will get the OH_PROP_FHIR_ERROR_DETAIL_CODE as 'Invalid Content'

##### _OH_PROP_FHIR_ERROR_DETAIL_SYSTEM

* This the identification of the code system that defines the meaning of the symbol in the code.

* Ex: https://healthcare.wso2.org/CodeSystem/operation-outcome will be the value of this property.

##### _OH_PROP_FHIR_ERROR_DETAIL_DISPLAY

* This is a representation of the meaning of the code in the system, following the rules of the system.

* Ex : If the resource ID is missing in the integration project it will give 'Invalid resource Id' as the value of this property. 

##### _OH_PROP_FHIR_ERROR_DIAGNOSTICS

* This is additional diagnostic information about the issue.

* Ex : If the resource ID is missing in the integration project it will give 'Operation was not attempted because the resource Id is not presented' as the value of this property.


>Note : Visit https://www.hl7.org/fhir/operationoutcome.html#resource for further details.