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
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class that constructs the query parameters that can be appended with the export operation.
 */
public class ExportQueryBuilder extends AbstractConnector {

    private static final String encoding = "UTF-8";
    private static final String URL_QUERY = "uri.var.urlQuery";
    private String parameterNames = "";

    public String getParameterNames() {

        return parameterNames;
    }

    public void setParameterNames(String parameterNames) {

        this.parameterNames = parameterNames;
    }

    @Override
    public void connect(MessageContext messageContext) {

        String[] parameterList = {"_outputFormat" ,"_since", "_type" ,"_typeFilter"};
        StringBuilder urlQueryBuilder = new StringBuilder();
        for (String paramName : parameterList) {
            String paramValue = (String) getParameter(messageContext, paramName);
            if (StringUtils.isNotEmpty(paramValue)) {
                String encodedParamValue = null;
                try {
                    encodedParamValue = URLEncoder.encode(paramValue, encoding);
                } catch (UnsupportedEncodingException e) {
                    MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "export", "Bulk Export",
                                                                        "Error occured while preparing the query parameters for the export", "Export failed.");
                }
                if (urlQueryBuilder.length() == 0) {
                    urlQueryBuilder.append('?').append(paramName).append('=').append(encodedParamValue);
                } else {
                    urlQueryBuilder.append('&').append(paramName).append('=').append(encodedParamValue);
                }
            }
        }
        messageContext.setProperty(URL_QUERY, urlQueryBuilder.toString());
    }
}
