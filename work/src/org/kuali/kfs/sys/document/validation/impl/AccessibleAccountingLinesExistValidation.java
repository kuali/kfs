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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE;

import java.util.Iterator;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * A validation that checks that once a given accounting line is deleted, there will still exist
 * accessible accounting lines on the document
 */
public class AccessibleAccountingLinesExistValidation extends GenericValidation {
    private AccountService accountService;
    private AccountingDocument accountingDocumentForValidation;
    private boolean lineWasAlreadyDeletedFromDocumentForValidation;

    /**
     * <strong>Expects an accounting document as the first parameter and a boolean, signifying whether the line was already deleted or not from the document, as the second</param>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        // verify that other accountingLines will exist after the deletion which are accessible to this user
        int minimumRemainingAccessibleLines = 1 + (lineWasAlreadyDeletedFromDocumentForValidation ? 0 : 1);
        boolean sufficientLines = hasAccessibleAccountingLines(accountingDocumentForValidation, minimumRemainingAccessibleLines);
        if (!sufficientLines) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE);
        }
        return sufficientLines;
    }

    /**
     * @param financialDocument
     * @param min
     * @return true if the document has n (or more) accessible accountingLines
     */
    protected boolean hasAccessibleAccountingLines(AccountingDocument financialDocument, int min) {
        boolean hasLines = false;

        // only count if the doc is enroute
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (workflowDocument.stateIsEnroute()) {            
            AccountingLineAuthorizer accountingLineAuthorizer = new AccountingLineAuthorizerBase();

            int accessibleLines = 0;
            for (Iterator i = financialDocument.getSourceAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                
                boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(financialDocument, line, KFSPropertyConstants.ACCOUNT_NUMBER, currentUser);
                             
                if (isAccessible) {
                    accessibleLines += 1;
                }
            }
            for (Iterator i = financialDocument.getTargetAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(financialDocument, line, KFSPropertyConstants.ACCOUNT_NUMBER, currentUser);
                
                if (isAccessible) {
                    accessibleLines += 1;
                }
            }

            hasLines = (accessibleLines >= min);
        }
        else {
            if (workflowDocument.stateIsException() && KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(currentUser.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, org.kuali.rice.kns.service.KNSServiceLocator.getKualiConfigurationService().getParameterValue(org.kuali.rice.kns.util.KNSConstants.KNS_NAMESPACE, org.kuali.rice.kns.util.KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, org.kuali.rice.kns.util.KNSConstants.CoreApcParms.WORKFLOW_EXCEPTION_WORKGROUP))) {
                hasLines = true;
            }
            else {
                if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                    hasLines = true;
                }
                else {
                    hasLines = false;
                }
            }
        }

        return hasLines;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the lineWasAlreadyDeletedFromDocumentForValidation attribute. 
     * @return Returns the lineWasAlreadyDeletedFromDocumentForValidation.
     */
    public boolean isLineWasAlreadyDeletedFromDocumentForValidation() {
        return lineWasAlreadyDeletedFromDocumentForValidation;
    }

    /**
     * Sets the lineWasAlreadyDeletedFromDocumentForValidation attribute value.
     * @param lineWasAlreadyDeletedFromDocumentForValidation The lineWasAlreadyDeletedFromDocumentForValidation to set.
     */
    public void setLineWasAlreadyDeletedFromDocumentForValidation(boolean lineWasAlreadyDeletedFromDocumentForValidation) {
        this.lineWasAlreadyDeletedFromDocumentForValidation = lineWasAlreadyDeletedFromDocumentForValidation;
    }
}

