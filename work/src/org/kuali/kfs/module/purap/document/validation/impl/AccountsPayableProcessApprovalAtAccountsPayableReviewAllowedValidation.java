/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class AccountsPayableProcessApprovalAtAccountsPayableReviewAllowedValidation extends GenericValidation {

    /**
     * If at the node accounts payable review, checks if approval at accounts payable review is allowed.
     * Returns true if it is, false otherwise.
     * 
     * @param apDocument - accounts payable document
     * @return
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        if (((AccountsPayableDocument)event.getDocument()).isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW)) {
            if (!((AccountsPayableDocument)event.getDocument()).approvalAtAccountsPayableReviewAllowed()) {
                valid &= false;
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_AP_REQUIRES_ATTACHMENT);
            }
        }
        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

}
