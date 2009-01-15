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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

public class PurchasingAccountsPayableNewProcessItemValidation extends GenericValidation {

    //private PurchasingAccountsPayableDocument purapDocument;
    private PurapService purapService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) event.getDocument();
        //Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];

        boolean requiresAccountValidationOnAllEnteredItems = requiresAccountValidationOnAllEnteredItems(purapDocument);
        
        return success;
    }
    
    protected String getDocumentTypeLabel(String documentTypeName) {
        try {
            return KNSServiceLocator.getWorkflowInfoService().getDocType(documentTypeName).getDocTypeLabel();
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught Exception trying to get Workflow Document Type", e);
        }
    }

    /**
     * Determines whether the document will require account validation to be done on all of its items.
     * 
     * @param document The PurchasingAccountsPayable document to be validated.
     * @return boolean true.
     */
    protected boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {

        return true;
    }
 
    /**
     * Performs any additional document level validation for the accounts which consists of validating that the item has accounts,
     * the account percent is valid and the accounting strings are unique.
     * 
     * @param purapDocument The document containing the accounts to be validated.
     * @param purAccounts The List of accounts to be validated.
     * @param itemLineNumber The string representing the item line number of the item whose accounts are to be validated.
     * @return boolean true if it passes the validation.
     */
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;
        valid = valid & verifyHasAccounts(purAccounts, itemLineNumber);
        // if we don't have any accounts... not need to run any further validation as it will all fail
        if (valid) {
            valid = valid & verifyAccountPercent(accountingDocument, purAccounts, itemLineNumber);
        }
        // We can't invoke the verifyUniqueAccountingStrings in here because otherwise it would be invoking it more than once, if
        // we're also
        // calling it upon Save.
        valid &= verifyUniqueAccountingStrings(purAccounts, PurapConstants.ITEM_TAB_ERROR_PROPERTY, itemLineNumber);

        return valid;
    }
    
    /**
     * Verifies that the item has accounts.
     * 
     * @param purAccounts The List of accounts to be validated.
     * @param itemLineNumber The string representing the item line number of the item whose accounts are to be validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean verifyHasAccounts(List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;

        if (purAccounts.isEmpty()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, itemLineNumber);
        }

        return valid;
    }

    /**
     * Verifies account percent. If the total percent does not equal 100, the validation fails.
     * 
     * @param accountingDocument The document containing the accounts to be validated.
     * @param purAccounts The List of accounts to be validated.
     * @param itemLineNumber The string representing the item line number of the item whose accounts are to be validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean verifyAccountPercent(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;

        // validate that the percents total 100 for each item
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal desiredPercent = new BigDecimal("100");
        for (PurApAccountingLine account : purAccounts) {
            if (account.getAccountLinePercent() != null) {
                totalPercent = totalPercent.add(account.getAccountLinePercent());
            }
            else {
                totalPercent = totalPercent.add(BigDecimal.ZERO);
            }
        }
        if (desiredPercent.compareTo(totalPercent) != 0) {
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, itemLineNumber);
            valid = false;
        }

        return valid;
    }

    /**
     * Verifies that the accounting strings entered are unique for each item.
     * 
     * @param purAccounts The List of accounts to be validated.
     * @param errorPropertyName This is not currently being used in this method.
     * @param itemLineNumber The string representing the item line number of the item whose accounts are to be validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean verifyUniqueAccountingStrings(List<PurApAccountingLine> purAccounts, String errorPropertyName, String itemLineNumber) {
        Set existingAccounts = new HashSet();
        for (PurApAccountingLine acct : purAccounts) {
            if (!existingAccounts.contains(acct.toString())) {
                existingAccounts.add(acct.toString());
            }
            else {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_UNIQUE, itemLineNumber);
                return false;
            }
        }

        return true;
    }
    
    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }    
     

}
