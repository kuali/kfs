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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;

/**
 * Contracts & Grants Letter Of Credit Review Document Presentation Controller.
 */
public class ContractsGrantsLetterOfCreditReviewDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        ContractsGrantsLetterOfCreditReviewDocument contractsGrantsLOCReviewDocument = (ContractsGrantsLetterOfCreditReviewDocument) document;

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated()) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        if (workflowDocument.isCanceled() || workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal()) {
            //To remove recalculate button and make amount to draw field readonly.
            editModes.add(ArAuthorizationConstants.ContractsGrantsLetterOfCreditReviewDocumentEditMode.HIDE_RECALCULATE_BUTTON);
            editModes.add(ArAuthorizationConstants.ContractsGrantsLetterOfCreditReviewDocumentEditMode.DISABLE_AMT_TO_DRAW);
        }

        return editModes;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (canEdit(document) && !workflowDocument.isInitiated());
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (canEdit(document) && !workflowDocument.isInitiated());
    }

}
