/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants LOC Review Document Presentation Controller.
 */
public class ContractsGrantsLOCReviewDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {

        Set<String> editModes = super.getEditModes(document);
        ContractsGrantsLOCReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLOCReviewDocument) document;

        if (StringUtils.equals(contractsGrantsLOCReviewDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON);
        }
        else {
            if (editModes.contains(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON)) {
                editModes.remove(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON);
            }
        }
        return editModes;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            return false;
        }
        else{
        return !isDocStatusCodeInitiated(document);
        }
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            return false;
        }
        else{
        return !isDocStatusCodeInitiated(document);
        }
    }

    /**
     * Returns true if the document passed in is in initiated status.
     */
    protected boolean isDocStatusCodeInitiated(Document document) {
        ContractsGrantsLOCReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLOCReviewDocument) document;
        return (StringUtils.equals(contractsGrantsLOCReviewDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE));
    }

}