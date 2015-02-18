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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.AccountsPayableStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

public class AccountsPayableProcessApprovalAtAccountsPayableReviewAllowedValidation extends GenericValidation {

    /**
     * If at the node accounts payable review, checks if approval at accounts payable review is allowed.
     * Returns true if it is, false otherwise.
     *
     * @param apDocument - accounts payable document
     * @return
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        if (((AccountsPayableDocument)event.getDocument()).isDocumentStoppedInRouteNode(AccountsPayableStatuses.NODE_ACCOUNT_PAYABLE_REVIEW)) {
            if (!((AccountsPayableDocument)event.getDocument()).approvalAtAccountsPayableReviewAllowed()) {
                valid &= false;
                WorkflowDocument workflowDoc = event.getDocument().getDocumentHeader().getWorkflowDocument();
                if(PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(workflowDoc.getDocumentTypeName())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRES_ATTACHMENT);
                }
                else {
                        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_AP_REQUIRES_ATTACHMENT);
                }


            }
        }
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

}
