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

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.healthcare.integration.common.Constants;
import org.wso2.healthcare.integration.common.utils.MiscellaneousUtils;

/**
 * Mediator to rewrite FHIR repository base URL with server URL
 */
public class ServerUrlRewrite extends AbstractMediator {

    private static final Log log = LogFactory.getLog(ServerUrlRewrite.class);

    @Override
    public boolean mediate(MessageContext messageContext) {

        if (JsonUtil.hasAJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext())) {
            String serverUrl =
                    (String) messageContext.getProperty(Constants.OH_INTERNAL_FHIR_SERVER_URL);
            //Replace the base URL with server URL
            org.apache.axis2.context.MessageContext axisMsgCtx =
                    ((Axis2MessageContext) messageContext).getAxis2MessageContext();
            try {
                MiscellaneousUtils.rewriteInMsgCtx(axisMsgCtx,
                         ((String) messageContext.getProperty(org.wso2.healthcare.integration.fhir.repository.Constants.FHIR_REPO_BASE)),
                            serverUrl);
            } catch (AxisFault e) {
                String errorMessage = "Error occurred while rewriting server URL.";
                log.error(errorMessage, e);
                return false;
            }
            return true;
        } else {
            MiscellaneousUtils.populateAndThrowSynapseException(messageContext, "error", "server error",
                                "Internal Server Error", "There is an internal Server Error.", "Internal Server Error.");
            return false;
        }
    }
}
