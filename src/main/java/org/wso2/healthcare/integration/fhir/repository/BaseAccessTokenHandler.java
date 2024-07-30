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
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.healthcare.integration.common.ehr.auth.ClientCredentialsAccessTokenHandler;
import org.wso2.healthcare.integration.common.ehr.auth.Token;
import org.wso2.healthcare.integration.common.ehr.auth.TokenManager;

import java.util.HashMap;
import java.util.Map;

public class BaseAccessTokenHandler extends ClientCredentialsAccessTokenHandler {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        String propertyAccessToken = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_ACCESS_TOKEN);
        if (StringUtils.isEmpty(propertyAccessToken)) {
            // If the access token is not available in the message context, retrieve from the token endpoint.
            String clientId = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_CLIENT_ID);
            String clientSecret = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_CLIENT_SECRET);
            String tokenEndpoint = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_TOKEN_ENDPOINT);
            // to keep any other parameters need for the token generation
            Map<String, String> payloadParametersMap = new HashMap<>();
            Token token = TokenManager.getToken(clientId, tokenEndpoint);
            if (token == null || !token.isActive()) {
                if (token != null && !token.isActive()) {
                    TokenManager.removeToken(clientId, tokenEndpoint);
                }
                token = getAndAddNewToken(messageContext, clientId, clientSecret.toCharArray(), payloadParametersMap, tokenEndpoint);
            }
            messageContext.setProperty(Constants.FHIR_REPO_ACCESS_TOKEN, token.getAccessToken());
        }
    }
}
