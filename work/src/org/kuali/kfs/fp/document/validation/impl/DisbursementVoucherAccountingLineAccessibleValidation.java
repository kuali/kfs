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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class DisbursementVoucherAccountingLineAccessibleValidation extends AccountingLineAccessibleValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineAccessibleValidation.class);

    private AccountService accountService;

    /**
     * Validates that the given accounting line is accessible for editing by the current user. <strong>This method expects a
     * document as the first parameter and an accounting line as the second</strong>
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        AccountingDocument accountingDocumentForValidation = this.getAccountingDocumentForValidation();
        AccountingLine accountingLineForValidation = this.getAccountingLineForValidation();

        boolean isAccessible = accountService.accountIsAccessible(accountingDocumentForValidation, accountingLineForValidation, financialSystemUser);

        // get the authorizer class to check for special conditions routing and if the user is part of a particular workgroup
        // but only if the document is enroute
        KualiWorkflowDocument workflowDocument = accountingDocumentForValidation.getDocumentHeader().getWorkflowDocument();
        if (!isAccessible && workflowDocument.stateIsEnroute()) {

            // if approval is requested and it is special conditions routing and the user is in a special conditions routing
            // workgroup then the line is accessible
            // TODO fix for kim
            DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) this.getAccountingDocumentForValidation();
//            if (workflowDocument.isApprovalRequested() && dvDocument.isSpecialRouting() && this.isUserInDisbursementVouchWorkGroups(financialSystemUser)) {
//                isAccessible = true;
//            }
        }

        // report errors if the current user can have no access to the account
        if (!isAccessible) {
            String accountNumber = accountingLineForValidation.getAccountNumber();
            String principalName = GlobalVariables.getUserSession().getPerson().getPrincipalName();
            String errorKey = this.convertEventToMessage(event);

            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, errorKey, accountNumber, principalName);
        }

        return isAccessible;
    }

    /**
     * Sets the accountService attribute value.
     * 
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
