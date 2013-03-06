/*
 * Copyright 2010 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.util;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * In situation where the Maintainable does not have access to the document,  this class is a utility which
 * will retrieve the workflow document
 *
 */
public final class MaintainableWorkflowUtils {

    private MaintainableWorkflowUtils() {
    }

    /**
     * This method checks if the Workflow document is in Saved or Enroute status
     *
     * @param documentNumber
     * @return
     */
    public static boolean isDocumentSavedOrEnroute(String documentNumber) {
        boolean isSaveOrEnroute = false;
        WorkflowDocument workflowDocument = getWorkflowDocument(documentNumber);

        if (workflowDocument != null) {
            isSaveOrEnroute = workflowDocument.isSaved() || workflowDocument.isEnroute();
        }
        return isSaveOrEnroute;
    }

    /**
     * Retrieve the KualiWorkflowDocument base on documentNumber
     *
     * @param documentNumber
     * @return
     */
    private static WorkflowDocument getWorkflowDocument(String documentNumber) {

        WorkflowDocument workflowDocument = null;
        WorkflowDocumentService workflowDocumentService = KewApiServiceLocator.getWorkflowDocumentService();
        try {
            String principalId = null;
            if(ObjectUtils.isNull(GlobalVariables.getUserSession())) {
                principalId = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER).getPrincipalId();
            }
            else {
               principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            }
	        workflowDocument = WorkflowDocumentFactory.loadDocument(principalId, documentNumber);
        }
        catch (Exception ex) {
            throw new RuntimeException("Error to retrieve workflow document: " + documentNumber, ex);
        }
         return workflowDocument ;
    }



}
