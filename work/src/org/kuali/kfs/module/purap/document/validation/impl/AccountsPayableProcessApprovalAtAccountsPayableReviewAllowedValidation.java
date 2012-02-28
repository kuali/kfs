/*
 * Copyright 2008-2009 The Kuali Foundation
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
