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

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedUpdateAccountingLineEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class AccountingLineAccessibleValidation extends GenericValidation {
    private AccountService accountService;
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    
    /**
     * Indicates what is being done to an accounting line. This allows the same method to be used for different actions.
     */
    public enum AccountingLineAction {
        ADD(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD), DELETE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE), UPDATE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);

        public final String accessibilityErrorKey;

        AccountingLineAction(String accessabilityErrorKey) {
            this.accessibilityErrorKey = accessabilityErrorKey;
        }
    }

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {        
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        AccountingLineAuthorizer accountingLineAuthorizer = new AccountingLineAuthorizerBase();
        
        boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocumentForValidation, accountingLineForValidation, KFSPropertyConstants.ACCOUNT_NUMBER, currentUser);

        // report errors
        if (!isAccessible) {
            String principalName = currentUser.getPrincipalName();
            
            String[] chartErrorParams = new String[] { accountingLineForValidation.getChartOfAccountsCode(),  principalName};
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, convertEventToMessage(event), chartErrorParams);
            
            String[] accountErrorParams = new String[] { accountingLineForValidation.getAccountNumber(), principalName };
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, convertEventToMessage(event), accountErrorParams);
        }

        return isAccessible;
    }
    
    /**
     * Determines what error message should be shown based on the event that required this validation
     * @param event the event to use to determine the error message
     * @return the key of the error message to display
     */
    protected String convertEventToMessage(KualiDocumentEvent event) {
        if (event instanceof AttributedAddAccountingLineEvent) {
            return AccountingLineAction.ADD.accessibilityErrorKey;
        } else if (event instanceof AttributedUpdateAccountingLineEvent) {
            return AccountingLineAction.UPDATE.accessibilityErrorKey;
        } else if (event instanceof AttributedDeleteAccountingLineEvent) {
            return AccountingLineAction.DELETE.accessibilityErrorKey;
        } else {
            return "";
        }
    }

    /**
     * Gets the accountService attribute. 
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
        return accountService;
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
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}

