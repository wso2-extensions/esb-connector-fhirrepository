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
import org.wso2.healthcare.integration.common.HealthcareIntegratorEnvironment;
import org.wso2.healthcare.integration.common.OHServerCommonDataHolder;
import org.wso2.healthcare.integration.common.config.model.FHIRRepositoryConfig;
import org.wso2.healthcare.integration.common.config.model.HealthcareIntegratorConfig;
import org.wso2.healthcare.integration.fhir.repository.BaseConfigurationHandler;
import org.wso2.healthcare.integration.fhir.repository.Constants;

public class AzureConfigurationHandler extends BaseConfigurationHandler {

    @Override
    public void connect(MessageContext messageContext) {

        super.connect(messageContext);
        HealthcareIntegratorEnvironment integratorEnvironment =
                OHServerCommonDataHolder.getInstance().getHealthcareIntegratorEnvironment();
        HealthcareIntegratorConfig config = integratorEnvironment.getHealthcareIntegratorConfig();
        FHIRRepositoryConfig fhirRepositoryConfig = config.getFHIRServerConfig().getFhirRepositoryConfig();
        String storageAccountUrlParam = (String) getParameter(messageContext, "storageAccountUrl");
        String storageResourceUrlParam = (String) getParameter(messageContext, Constants.STORAGE_RESOURCE_URL);
        String storageApiVersionParam = (String) getParameter(messageContext, "xmsVersion");
        String storageAccountUrl = StringUtils.isNotBlank(storageAccountUrlParam) ? storageAccountUrlParam
                : fhirRepositoryConfig.getStorageAccountUrl();
        String storageResourceUrl = StringUtils.isNotBlank(storageResourceUrlParam) ? storageResourceUrlParam
                : fhirRepositoryConfig.getStorageResourceUrl();
        String storageApiVersion = StringUtils.isNotBlank(storageApiVersionParam) ? storageApiVersionParam
                : fhirRepositoryConfig.getStorageApiVersion();
        if (StringUtils.isBlank(storageResourceUrl)) {
            storageResourceUrl = Constants.AZURE_STORAGE_RESOURCE_URL;
        }
        // storageResourceUrl can never be null
        messageContext.setProperty(Constants.PROPERTY_STORAGE_RES_URL, storageResourceUrl);
        if (StringUtils.isNotBlank(storageAccountUrl)) {
            if (StringUtils.endsWith(storageAccountUrl, "/")) {
                storageAccountUrl = StringUtils.removeEnd(storageAccountUrl, "/");
            }
            messageContext.setProperty(Constants.PROPERTY_STORAGE_ACC_URL, storageAccountUrl);

        }
        if (StringUtils.isBlank(storageApiVersion)) {
            storageApiVersion = Constants.AZURE_STORAGE_API_VERSION;
        }
        messageContext.setProperty(Constants.PROPERTY_STORAGE_API_VERSION, storageApiVersion);
    }
}
