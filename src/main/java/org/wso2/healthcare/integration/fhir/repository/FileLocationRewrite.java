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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Mediator to rewrite FHIR repository file server URL with server URL
 */
public class FileLocationRewrite extends AbstractMediator {

    private static final String hashSalt =
            HealthcareIntegratorConfig.getInstance().getFHIRServerConfig().getFhirRepositoryConfig().getRepoTokenHashSalt();

    @Override
    public boolean mediate(MessageContext messageContext) {

        if (JsonUtil.hasAJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext())) {
            String claimOne = (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_REPO_UNIQUE_ID_ONE);
            String claimTwo = (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_UNIQUE_ID_TWO);
            String fileApiContext = (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_REPO_FILE_API_CONTEXT);
            String requireClientToContentMapping =
                    (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_REPO_ACCESS_CONTROL);
            String fileServerUrl;
            String repoType = (String) messageContext.getProperty(Constants.FHIR_REPO_TYPE);
            // change here when we support more fhir repos
            switch (repoType) {
                case Constants.FhirRepositoryTypes.AZURE :
                    fileServerUrl = (String) messageContext.getProperty(Constants.PROPERTY_STORAGE_ACC_URL);
                    break;
                default :
                    fileServerUrl = (String) messageContext.getProperty(Constants.FHIR_REPO_BASE);
            }
            String serverUrl =
                    (String) messageContext.getProperty(org.wso2.healthcare.integration.common.Constants.OH_INTERNAL_FHIR_SERVER_URL);
            org.apache.axis2.context.MessageContext axisMsgCtx =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            String payload = JsonUtil.jsonPayloadToString(axisMsgCtx);
            JsonParser parser = new JsonParser();
            JsonElement statusResponse = parser.parse(payload);
            JsonObject jsonObject = statusResponse.getAsJsonObject();
            JsonArray outputArrayElements = jsonObject.getAsJsonArray(Constants.OUTPUT);
            for (JsonElement outputArrayElement : outputArrayElements) {
                String fileUrl = outputArrayElement.getAsJsonObject().get(Constants.URL).getAsString();
                String fileLocationOnServer = StringUtils.substringAfter(fileUrl, fileServerUrl);
                String rewrittenFileLocation = serverUrl + fileApiContext + fileLocationOnServer;

                // append token to the end of file url if the mapping is enabled
                if (Boolean.parseBoolean(requireClientToContentMapping)) {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(Constants.SHA_512);
                        String toHash = claimOne + claimTwo + fileLocationOnServer + hashSalt;
                        messageDigest.update(toHash.getBytes(StandardCharsets.UTF_8));
                        byte[] mdbytes = messageDigest.digest();
                        StringBuilder hashedString = new StringBuilder();
                        for (byte mdbyte : mdbytes) {
                            hashedString.append(Integer.toHexString(0xFF & mdbyte));
                        }
                        rewrittenFileLocation = rewrittenFileLocation + Constants.REPOTOKEN_QPARAM + hashedString;
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                outputArrayElement.getAsJsonObject().remove(Constants.URL);
                outputArrayElement.getAsJsonObject().add(Constants.URL, new JsonPrimitive(rewrittenFileLocation));
            }
            JsonUtil.removeJsonPayload(axisMsgCtx);
            try {
                JsonUtil.getNewJsonPayload(axisMsgCtx, jsonObject.toString(), true, true);
            } catch (AxisFault e) {
                MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "server error",
                                   "Internal Server Error", "There is an internal Server Error.", "Internal Server Error.");
            }
            return true;
        } else {
            MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "server error",
                               "Internal Server Error", "There is an internal Server Error.", "Internal Server Error.");
            return false;
        }
    }
}
