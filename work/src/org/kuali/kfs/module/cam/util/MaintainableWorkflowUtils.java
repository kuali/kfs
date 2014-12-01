/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
