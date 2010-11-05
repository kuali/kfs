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

import java.beans.PropertyDescriptor;

import javax.swing.Spring;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

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
        KualiWorkflowDocument workflowDocument = getKualiWorkflowDocument(documentNumber);
        
        if (workflowDocument != null) {
            isSaveOrEnroute = workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute();
        }
        return isSaveOrEnroute;
    }
    
    /**
     * Retrieve the KualiWorkflowDocument base on documentNumber
     * 
     * @param documentNumber
     * @return
     */
    private static KualiWorkflowDocument getKualiWorkflowDocument(String documentNumber) {

        KualiWorkflowDocument workflowDocument = null;
        WorkflowDocumentService workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        try {
            workflowDocument = workflowDocumentService.createWorkflowDocument(NumberUtils.createLong(documentNumber), GlobalVariables.getUserSession().getPerson());
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("Error to retrieve workflow document: " + documentNumber, ex);
        }
        return workflowDocument ;
    }



}
