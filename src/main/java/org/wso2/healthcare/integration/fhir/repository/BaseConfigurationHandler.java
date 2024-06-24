/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.healthcare.integration.fhir.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.model.FHIRRepositoryConfig;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

/**
 * The class that handles the connector config. Configs can be passed via connector init operation.
 * Else, it will be retrieved from the deployment.toml
 */
public class BaseConfigurationHandler extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        HealthcareIntegratorEnvironment integratorEnvironment =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        HealthcareIntegratorConfig config = integratorEnvironment.getHealthcareIntegratorConfig();
        FHIRRepositoryConfig fhirRepositoryConfig = config.getFHIRServerConfig().getFhirRepositoryConfig();

        String fhirServerUrl = config.getFHIRServerConfig().getBaseUrl();
        if (StringUtils.isNotBlank(fhirServerUrl)) {
            if (StringUtils.endsWith(fhirServerUrl, "/")) {
                fhirServerUrl = StringUtils.removeEnd(fhirServerUrl, "/");
            }
            messageContext.setProperty(org.wso2.healthcare.integration.common.Constants.OH_INTERNAL_FHIR_SERVER_URL,
                                       fhirServerUrl);
        } else {
            MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "config", "ServerUrl Required",
                               "Server URL is missing.", "Missing configuration.");
        }

        String repositoryTypeParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.REPOSITORY_TYPE);
        String baseParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.BASE);
        String clientIdParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.CLIENT_ID);
        String clientSecretParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.CLIENT_SECRET);
        String tokenEndpointParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.TOKEN_ENDPOINT);
        String accessTokenParam = (String) getParameter(messageContext, org.wso2.healthcare.integration.fhir.repository.Constants.ACCESS_TOKEN);

        String repositoryType = StringUtils.isNotBlank(repositoryTypeParam) ? repositoryTypeParam
                : fhirRepositoryConfig.getRepositoryType();
        String base = StringUtils.isNotBlank(baseParam) ? baseParam : fhirRepositoryConfig.getBase();
        String clientId = StringUtils.isNotBlank(clientIdParam) ? clientIdParam : fhirRepositoryConfig.getClientId();
        String clientSecret =
                StringUtils.isNotBlank(clientSecretParam) ? clientSecretParam : fhirRepositoryConfig.getClientSecret();
        String tokenEndpoint = StringUtils.isNotBlank(tokenEndpointParam) ? tokenEndpointParam
                : fhirRepositoryConfig.getTokenEndpoint();

        if (StringUtils.isNoneBlank(repositoryType, base)) {
            messageContext.setProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_TYPE, repositoryType);
            if (StringUtils.endsWith(base, "/")) {
                base = StringUtils.removeEnd(base, "/");
            }
            messageContext.setProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_BASE, base);
            if (StringUtils.isNotBlank(accessTokenParam)) {
                messageContext.setProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_ACCESS_TOKEN, accessTokenParam);
            } else if (StringUtils.isNoneBlank(clientId, clientSecret, tokenEndpoint)) {
                messageContext.setProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_CLIENT_ID, clientId);
                messageContext.setProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_CLIENT_SECRET, clientSecret);
                messageContext.setProperty(Constants.FHIR_REPO_TOKEN_ENDPOINT, tokenEndpoint);
            } else {
                handleMissingParams(messageContext);
            }

        } else {
            handleMissingParams(messageContext);
        }
    }

    private void handleMissingParams(MessageContext messageContext) {

        MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "login", "Login Required",
                            "Some required parameters for the connector init are missing.", "Authentication failed.");
    }
}
