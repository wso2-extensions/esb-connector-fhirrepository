# Configuring FHIR Repository Connector

[[Initializing the connector]](#initializing-the-connector)  [[Additional Information]](#additional-information)

**Prerequisite**

Please ensure that you have configured the base_url parameter in deployment.toml to point to the external-facing FHIR 
server URL of the FHIR server you are building with the WSO2 solution. This will be later used to perform the rewriting 
of the references to the FHIR server URL in the responses by the FHIR repository connector. Regardless of whether you 
need FHIR server URL rewriting or not, please configure this to a meaningful value. It does not have to be a valid domain 
if you are not planning to mask the FHIR server URL references of the third-party FHIR repository you are integrating with.

```editorconfig
[healthcare.fhir]
...
base_url = "https://my-fhir-server.com"
...
```

**Introduction**

Before performing any FHIR Repository Connector operations, make sure to include the <fhirrepository.init> element in your configuration.
The <fhirrepository.init> element authenticates the user and grants them access to the FHIR Server using OAuth2 authentication. If your FHIR server 
requires only passing an OAuth2 access token or requires no security, please configure the `accessToken` property to the proper token value or
to a dummy value, respectively. 

**Properties**

* repositoryType : The type of the FHIR Repository. ( Options: `Azure` - for Azure FHIR repository, use `Other` for any other repository. )
* baseUrl : The FHIR service Root URL of FHIR repository.
* clientId : Client ID of the registered application.
* clientSecret : Client Secret of the registered application.
* accessToken : (optional) Access Token can be given directly if clientId and clientSecret is not given.
* tokenEndpoint : FHIR Server token Endpoint

If you are integrating with a FHIR repository without any security or only with an access token, at minimum you need to 
have following in your sequence. 

```editorconfig
<fhirrepository.init>
  <repositoryType>Other</repositoryType>
  <base>https://hapi.fhir.org/baseR5</base>
  <accessToken>notneeded</accessToken>
</fhirrepository.init>
```

If you are integrating with Azure FHIR repository, your configurations may look like following;

```editorconfig
<fhirrepository.init>
  <repositoryType>Azure</repositoryType>
  <base>https://xxxxxxxxx.fhir.azurehealthcareapis.com</base>
  <clientId>xxxxxxxxx</clientId>
  <clientSecret>xxxxxxxxx</clientSecret>
  <tokenEndpoint>https://login.microsoftonline.com/xxxxxxxxx/oauth2/token</tokenEndpoint>
</fhirrepository.init>
```

Change the `repositoryType` to something other than `Azure` from the above configuration, if you are planning to integrate
with a FHIR repository that is secured by OAuth2.0 protocol. 

> Note : The repositoryType must have the exact same name as specified in the documentation.

* Optionally, you can add the parameter values in the deployment.toml file without adding them as the parameters of `fhirrepository.init` of the integration project. 
But, you still need to initialize the FHIR repository connector as follows;

```editorconfig
<fhirrepository.init/>
```
* Then you have to enter the value for repositoryType, base, clientId, clientSecret and tokenEndpoint under [healthcare.fhir.repository] in the deployment.toml file.

```editorconfig
[healthcare.fhir.repository]
repositoryType = "<Enter the FHIR Repository type>"
base = "<Enter the FHIR service Root URL of FHIR repository here>"
clientId = "<Enter the client Id here>"
clientSecret = "<Enter the client secret here>"
tokenEndpoint = "<Enter the token endpoint here>"
```

* It is necessary to have repositoryType, baseUrl, clientId, clientSecret, and tokenEndpoint or repositoryType, baseUrl, and accessToken in one of the above-mentioned ways.

## [Optional] Additional Information

* You have to add the following message formatters and messageBuilders into the deployment.toml configuration, if you are 
planning to use the `patch` operation. 

### Required message formatters

```editorconfig
[[custom_message_formatters]]
class = "org.apache.synapse.commons.json.JsonStreamFormatter"
content_type = "application/json-patch+json"
```

### Required messageBuilders

```editorconfig
[[custom_message_builders]]
class = "org.apache.synapse.commons.json.JsonStreamBuilder"
content_type = "application/json-patch+json"
```
After you've configured the FHIR Repository connection, see [Working with the FHIR Repository Connector](operations.md) for instructions on how to use it to perform various operations.
