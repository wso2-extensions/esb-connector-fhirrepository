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

package org.wso2.healthcare.integration.fhir.repository.azure;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.healthcare.integration.common.ehr.auth.Token;
import org.wso2.healthcare.integration.common.ehr.auth.TokenManager;
import org.wso2.healthcare.integration.fhir.repository.BaseAccessTokenHandler;
import org.wso2.healthcare.integration.fhir.repository.Constants;
import org.wso2.healthcare.integration.common.utils.HealthcareUtils;

import java.util.HashMap;
import java.util.Map;

public class AzureAccessTokenHandler extends BaseAccessTokenHandler {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        super.connect(messageContext);
        String clientId = (String) messageContext.getProperty(Constants.FHIR_REPO_CLIENT_ID);
        String clientSecret = (String) messageContext.getProperty(Constants.FHIR_REPO_CLIENT_SECRET);
        String tokenEndpoint = (String) messageContext.getProperty(Constants.FHIR_REPO_TOKEN_ENDPOINT);
        Map<String, String> payloadParametersMap = new HashMap<>();
        // retrieve the fhir service resource
        String resource = (String) getParameter(messageContext, Constants.RESOURCE);
        if (StringUtils.isEmpty(resource)) {
            resource = (String) messageContext.getProperty(Constants.FHIR_REPO_BASE);
        }
        // retrieve the storage service resource
        String storageResUrl = (String) messageContext.getProperty(Constants.PROPERTY_STORAGE_RES_URL);

        // generate token for fhir service
        payloadParametersMap.put(Constants.RESOURCE, resource);
        Token baseToken = TokenManager.getToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
        if (baseToken == null || !baseToken.isActive()) {
            if (baseToken != null && !baseToken.isActive()) {
                TokenManager.removeToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
            }
            baseToken = getAndAddNewToken(messageContext, clientId, clientSecret.toCharArray(), payloadParametersMap, tokenEndpoint);
        }
        messageContext.setProperty(Constants.FHIR_REPO_ACCESS_TOKEN, baseToken.getAccessToken());

        // generate token for storage service
        payloadParametersMap.put(Constants.RESOURCE, storageResUrl);
        Token storageToken = TokenManager.getToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
        if (storageToken == null || !storageToken.isActive()) {
            if (storageToken != null && !storageToken.isActive()) {
                TokenManager.removeToken(clientId, getTokenKey(tokenEndpoint, payloadParametersMap));
            }
            storageToken = getAndAddNewToken(messageContext, clientId, clientSecret.toCharArray(), payloadParametersMap, tokenEndpoint);
        }
        messageContext.setProperty(HealthcareUtils.createInternalSynapsePropertyName(Constants.AZURE_STORAGE_TOKEN), storageToken.getAccessToken());
    }
}
