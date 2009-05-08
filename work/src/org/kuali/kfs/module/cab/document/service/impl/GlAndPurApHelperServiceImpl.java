/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cab.document.service.impl;

import org.kuali.kfs.module.cab.document.service.GlAndPurApHelperService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

public class GlAndPurApHelperServiceImpl implements GlAndPurApHelperService {

    public String getDocHandlerUrl(String documentNumber, String docTypeName) {
        KualiWorkflowInfo kualiWorkflowInfo = SpringContext.getBean(KualiWorkflowInfo.class);
        try {
            DocumentTypeDTO docType = kualiWorkflowInfo.getDocType(docTypeName);
            String docHandlerUrl = docType.getDocTypeHandlerUrl();
            if (docHandlerUrl.indexOf("?") == -1) {
                docHandlerUrl += "?";
            }
            else {
                docHandlerUrl += "&";
            }

            docHandlerUrl += KNSConstants.PARAMETER_DOC_ID + "=" + documentNumber + "&" + KNSConstants.PARAMETER_COMMAND + "=" + KEWConstants.DOCSEARCH_COMMAND;
            return docHandlerUrl;
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught WorkflowException trying to get document handler URL from Workflow", e);
        }
    }
}
