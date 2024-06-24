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
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The class that maps the content location to the client id and generates a hash
 */
public class ClientToContentLocationMapper extends AbstractMediator {

    private static final String hashSalt =
            HealthcareIntegratorConfig.getInstance().getFHIRServerConfig().getFhirRepositoryConfig().getRepoTokenHashSalt();

    @Override
    public boolean mediate(MessageContext messageContext) {

        String contentLocation = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_REPO_CONTENT_LOCATION);
        String claimOne = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_REPO_UNIQUE_ID_ONE);
        String claimTwo = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_UNIQUE_ID_TWO);
        if (StringUtils.isNotBlank(contentLocation)) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(org.wso2.healthcare.integration.fhir.repository.Constants.SHA_512);
                String toHash = claimOne + claimTwo + contentLocation + hashSalt;
                messageDigest.update(toHash.getBytes(StandardCharsets.UTF_8));
                byte[] mdbytes = messageDigest.digest();
                StringBuilder hashedString = new StringBuilder();
                for (byte mdbyte : mdbytes) {
                    hashedString.append(Integer.toHexString(0xFF & mdbyte));
                }
                messageContext.setProperty(Constants.OH_INTERNAL_FHIR_REPO_TOKEN, hashedString.toString());
                return true;
            } catch (NoSuchAlgorithmException e) {
                MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "export", "Bulk Export",
                                   "Error occured while mapping client to content location", "Export failed.");
                return false;
            }
        } else {
            MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "export", "Bulk Export",
                               "Missing require parameters for mapping client to content location", "Export failed.");
            return false;
        }
    }
}
