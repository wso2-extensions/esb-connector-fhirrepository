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
 * The class that verifies that the client is authorized to access the content location
 * by checking the hashed token available in the content location url.
 */
public class ClientToContentVerifier extends AbstractMediator {

    private static final String hashSalt =
            HealthcareIntegratorConfig.getInstance().getFHIRServerConfig().getFhirRepositoryConfig().getRepoTokenHashSalt();

    @Override
    public boolean mediate(MessageContext messageContext) {

        String contentLocation = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_REPO_CONTENT_LOCATION);
        String claimOne = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_REPO_UNIQUE_ID_ONE);
        String claimTwo = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_UNIQUE_ID_TWO);
        String fhirRepoToken = (String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.OH_INTERNAL_FHIR_REPO_TOKEN);
        if (StringUtils.isNoneBlank(contentLocation, fhirRepoToken)) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(Constants.SHA_512);
                String toHash = claimOne + claimTwo + contentLocation + hashSalt;
                messageDigest.update(toHash.getBytes(StandardCharsets.UTF_8));
                byte[] mdbytes = messageDigest.digest();
                StringBuilder hashedString = new StringBuilder();
                for (byte mdbyte : mdbytes) {
                    hashedString.append(Integer.toHexString(0xFF & mdbyte));
                }
                if (!(StringUtils.isNotBlank(fhirRepoToken) && fhirRepoToken.equalsIgnoreCase(hashedString.toString()))) {
                    failVerification(messageContext);
                }
                return true;
            } catch (NoSuchAlgorithmException e) {
                failVerification(messageContext);
                return false;
            }
        } else {
            MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "verification", "Bulk Export",
                               "Missing required parameters for verifying client to exported content", "Client verification failed.");
            return false;
        }
    }

    private void failVerification(MessageContext messageContext) {

        MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "verification", "Bulk Export",
                           "Error occured while verifying client with bulk content", "Client verification failed.");
    }
}
