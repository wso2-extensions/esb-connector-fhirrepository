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

import org.wso2.healthcare.integration.common.utils.HealthcareUtils;

public class Constants {

    public static final String SHA_512 = "SHA-512";

    public static final String AZP = "azp";

    public static final String ENDUSER = "http://wso2.org/claims/enduser";

    public static final String REPOTOKEN_QPARAM = "?token=";

    public static final String OUTPUT = "output";

    public static final String URL = "url";

    public static final String REPOSITORY_TYPE = "repositoryType";

    public static final String RESOURCE = "resource";

    public static final String AZURE_STORAGE_TOKEN = "AZURE_STORAGE_TOKEN";

    public static final String FHIR_REPO_CLIENT_ID = HealthcareUtils.createInternalSynapsePropertyName("CLIENT_ID");

    public static final String FHIR_REPO_CLIENT_SECRET = HealthcareUtils.createInternalSynapsePropertyName(
            "CLIENT_SECRET");

    public static final String FHIR_REPO_TOKEN_ENDPOINT = HealthcareUtils.createInternalSynapsePropertyName(
            "FHIR_REPO_TOKEN_ENDPOINT");

    public static final String FHIR_REPO_ACCESS_TOKEN = HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_ACCESS_TOKEN");

    public static final String FHIR_REPO_TYPE = HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_TYPE");

    public static final String FHIR_REPO_BASE = "uri.var._OH_INTERNAL_FHIR_REPO_BASE_";

    public static final String BASE = "base";

    public static final String CLIENT_ID = "clientId";

    public static final String CLIENT_SECRET = "clientSecret";

    public static final String TOKEN_ENDPOINT = "tokenEndpoint";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String STORAGE_RESOURCE_URL = "storageResourceUrl";

    public static final String AZURE_STORAGE_RESOURCE_URL = "https://storage.azure.com/";

    public static final String AZURE_STORAGE_API_VERSION = "2017-11-09";

    public static final String PROPERTY_STORAGE_RES_URL = "_OH_INTERNAL_STORAGE_RES_URL_";

    public static final String PROPERTY_STORAGE_ACC_URL = "_OH_INTERNAL_STORAGE_ACC_URL_";

    public static final String PROPERTY_STORAGE_API_VERSION = "_OH_INTERNAL_STORAGE_API_VERSION_";

    public static final String OH_INTERNAL_FHIR_REPO_CONTENT_LOCATION =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_CONTENT_LOCATION");

    public static final String OH_INTERNAL_FHIR_REPO_UNIQUE_ID_ONE =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_UNIQUE_ID_ONE");

    public static final String OH_INTERNAL_FHIR_UNIQUE_ID_TWO =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_UNIQUE_ID_TWO");

    public static final String OH_INTERNAL_FHIR_REPO_TOKEN =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_TOKEN");

    public static final String OH_INTERNAL_FHIR_REPO_FILE_API_CONTEXT =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_FILE_API_CONTEXT");

    public static final String OH_INTERNAL_FHIR_REPO_ACCESS_CONTROL =
            HealthcareUtils.createInternalSynapsePropertyName("FHIR_REPO_ACCESS_CONTROL");

    public static class FhirRepositoryTypes {
        public static final String AZURE = "Azure";
    }
}
