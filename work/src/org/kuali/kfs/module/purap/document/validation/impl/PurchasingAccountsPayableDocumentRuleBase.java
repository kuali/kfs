/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingAccountsPayableItemRule;
import org.kuali.kfs.module.purap.document.validation.ImportPurchasingAccountsPayableItemRule;
import org.kuali.kfs.module.purap.service.PurapAccountingLineRuleHelperService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Business rule(s) applicable to Purchasing Accounts Payable Documents.
 */
public class PurchasingAccountsPayableDocumentRuleBase implements AddPurchasingAccountsPayableItemRule, ImportPurchasingAccountsPayableItemRule {
    public static final Logger LOG = Logger.getLogger(PurchasingAccountsPayableDocumentRuleBase.class);

    public PurchasingAccountsPayableDocumentRuleBase() {
        super();
    }
    
    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to perform processValidation for PurchasingAccountsPayableDocument.
     * 
     * @param document The PurchasingAccountsPayableDocument to be validated
     * @return boolean true if it passes the validation
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */

    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;

        return isValid &= processValidation(purapDocument);
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to perform processValidation for PurchasingAccountsPayableDocument.
     * 
     * @param approveEvent The ApproveDocumentEvent instance that we can use to retrieve the document to be validated.
     * @return boolean true if it passes the validation.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.rule.event.ApproveDocumentEvent)
     */
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) approveEvent.getDocument();

        return isValid &= processValidation(purapDocument);
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to always return true.
     * 
     * @param financialDocument The PurchasingAccountsPayableDocument to be validated.
     * @param accountingLine The accounting line that is being added.
     * @return boolean true.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean isValid = true;

        return isValid;
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to always return true.
     * 
     * @param financialDocument The PurchasingAccountsPayableDocument to be validated.
     * @param accountingLine The accounting line to be deleted.
     * @param lineWasAlreadyDeletedFromDocument boolean true if the line was already deleted from document.
     * @return boolean true.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processDeleteAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, boolean)
     */
    public boolean processDeleteAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        // I think PURAP's accounting line is a bit different than other documents. The source accounting line is per item, not per
        // document.
        // Besides, we already have other item validations that determined whether the items contain at least one account wherever
        // applicable.
        // So this will be redundant if we do another validation, therefore we'll return true here so that it would not give the
        // error about
        // can't delete the last remaining accessible account anymore.
        return true;
    }


    /**
     * Calls each tab specific validation. Tabs included on all PURAP docs are: DocumentOverview, Vendor and Item
     * 
     * @param purapDocument The PurchasingAccountsPayableDocument to be validated.
     * @return boolean true if it passes all the validation.
     */
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        valid &= processDocumentOverviewValidation(purapDocument);
        valid &= processVendorValidation(purapDocument);
        valid &= processItemValidation(purapDocument);
        valid &= newProcessItemValidation(purapDocument);
        valid &= processAccountSummaryValidation(purapDocument);
        return valid;
    }

    public boolean processAccountSummaryValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        List<SummaryAccount> summaryAccounts = SpringContext.getBean(PurapAccountingService.class).generateSummaryAccounts(purapDocument);
        for (SummaryAccount summaryAccount : summaryAccounts) {
            //TODO: ctk - do we need all these null checks
            if(ObjectUtils.isNotNull(summaryAccount) && ObjectUtils.isNotNull(summaryAccount.getAccount()) && ObjectUtils.isNotNull(summaryAccount.getAccount().getAmount()) &&
                    summaryAccount.getAccount().getAmount().isNegative()) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapConstants.ACCOUNT_SUMMARY_TAB_ERRORS, PurapKeyConstants.ERROR_ITEM_ACCOUNT_NEGATIVE,summaryAccount.getAccount().getAccountNumber());
            }
        }
        return valid;
    }

    /**
     * Performs any validation for the Document Overview tab. Currently it will always return true.
     * 
     * @param purapDocument The PurchasingAccountsPayable document to be validated.
     * @return boolean true.
     */
    public boolean processDocumentOverviewValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // currently, there is no validation to force at the PURAP level for this tab

        return valid;
    }

    /**
     * Performs any validation for the Vendor tab. Currently it will always return true.
     * 
     * @param purapDocument The PurchasingAccountsPayable document to be validated.
     * @return boolean true.
     */
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // currently, there is no validation to force at the PURAP level for this tab

        return valid;
    }

    /**
     * Determines whether the document will require account validation to be done on all of its items.
     * 
     * @param document The PurchasingAccountsPayable document to be validated.
     * @return boolean true.
     */
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {

        return true;
    }

    /**
     * Performs any validation for the Item tab. For each item, it will invoke the data dictionary validations. If the item is
     * considered entered, if the item type is below the line item, then also invoke the validatBelowTheLineValues. If the document
     * requires account validation on all entered items or if the item contains accounting line, then call the
     * processAccountValidation for all of the item's accounting lines.
     * 
     * @param purapDocument The PurchasingAccountsPayable document to be validated.
     * @param needAccountValidation boolean that indicates whether we need account validation.
     * @return boolean true if it passes all of the validations.
     */
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;

        // Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];
        // If it's a credit memo, we'll have to append the source of the credit memo
        // whether it's created from a Vendor, a PO or a PREQ.
        if (documentType.equals("CreditMemoDocument")) {

        }

        boolean requiresAccountValidationOnAllEnteredItems = requiresAccountValidationOnAllEnteredItems(purapDocument);
        int i = 0;
        for (PurApItem item : purapDocument.getItems()) {
            SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(item);
            if (item.isConsideredEntered()) {
                GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
                // only do this check for below the line items
                if (item.getItemType().isAdditionalChargeIndicator()) {
                    valid &= validateBelowTheLineValues(documentType, item);
                }
                GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");

                if (requiresAccountValidationOnAllEnteredItems || (!item.getSourceAccountingLines().isEmpty())) {
                    processAccountValidation(purapDocument, item.getSourceAccountingLines(), item.getItemIdentifierString());
                }
            }
            i++;
        }

        return valid;
    }
    
    /**
     * Performs any validation for the Item tab. For each item, it will invoke the data dictionary validations. If the item is
     * considered entered, if the item type is below the line item, then also invoke the validatBelowTheLineValues. If the document
     * requires account validation on all entered items or if the item contains accounting line, then call the
     * processAccountValidation for all of the item's accounting lines.
     * 
     * @param purapDocument The PurchasingAccountsPayable document to be validated.
     * @return boolean true if it passes all of the validations.
     */
    public boolean newProcessItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;

        //Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];

        boolean requiresAccountValidationOnAllEnteredItems = requiresAccountValidationOnAllEnteredItems(purapDocument);
        
        //iterate over all the items
        int i = 0;
        for (PurApItem item : purapDocument.getItems()) {
        
            SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(item);
            
            //check to see if we need to call rules on a specific item (hook?)
            if (isItemConsideredEntered(item)) {
                GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
              //if true call hook to process item validation
                valid &= newIndividualItemValidation(purapDocument, documentType, item);
                GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
                //hook method to check if account validation is required(should this be set at top or checked per item)
                //if true call account validation
                if (requiresAccountValidationOnAllEnteredItems || (!item.getSourceAccountingLines().isEmpty())) {
                    processAccountValidation(purapDocument, item.getSourceAccountingLines(), item.getItemIdentifierString());
                }
            }
            
            i++;
        }

        return valid;
    }

    /**
     * This method does any document specific item checks.
     * 
     * @param valid
     * @param documentType
     * @param item
     * @param recurringPaymentType  Needed by overriding methods in child classes
     * @return
     */
    public boolean newIndividualItemValidation(PurchasingAccountsPayableDocument purapDocument, String documentType, PurApItem item) {
        boolean valid = true;
        if (item.getItemType().isAdditionalChargeIndicator()) {
            valid &= validateBelowTheLineValues(documentType, item);
            return valid;
        }
        return true;
    }

    /**
     * This method...
     * @param item
     * @return
     */
    protected boolean isItemConsideredEntered(PurApItem item) {
        return item.isConsideredEntered();
    }


    /**
     * Performs validations for below the line items. If the unit price is zero, and the system parameter indicates that the item
     * should not allow zero, then the validation fails. If the unit price is positive and the system parameter indicates that the
     * item should not allow positive values, then the validation fails. If the unit price is negative and the system parameter
     * indicates that the item should not allow negative values, then the validation fails. If the unit price is entered and is not
     * zero and the item description is empty and the system parameter indicates that the item requires user to enter description,
     * then the validation fails.
     * 
     * @param documentType The type of the PurchasingAccountsPayable document to be validated.
     * @param item The item to be validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean validateBelowTheLineValues(String documentType, PurApItem item) {
        boolean valid = true;
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        try {
            if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isZero()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "zero");
                }
            }
            else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isPositive()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "positive");
                }
            }
            else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNegative()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE) && !parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE, item.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "negative");
                }
            }
            if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && StringUtils.isEmpty(item.getItemDescription())) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION) && parameterService.getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION, item.getItemTypeCode()).evaluationSucceeds()) {
                    // if
                    // (parameterService.getIndicatorParameter(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)),
                    // PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION)) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, "The item description of " + item.getItemType().getItemTypeDescription(), "empty");
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The valideBelowTheLineValues of PurchasingAccountsPayableDocumentRuleBase was unable to resolve a document type class: " + PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType), e);
        }

        return valid;
    }

    /**
     * Performs the data dictionary validation to validate whether the item is a valid business object.
     * 
     * @param financialDocument The document containing the item to be validated.
     * @param item The item to be validated.
     * @return boolean true if it passes the validation.
     * @see org.kuali.kfs.module.purap.document.validation.AddPurchasingAccountsPayableItemRule#processAddItemBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {

        return SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(item, PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
    }


    /**
     * Performs the data dictionary validation to validate whether the item is a valid business object.
     * 
     * @param financialDocument The document containing the item to be validated.
     * @param item The item to be validated.
     * @return boolean true if it passes the validation.
     * @see org.kuali.kfs.module.purap.document.validation.ImportPurchasingAccountsPayableItemRule#processImportItemBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.module.purap.businessobject.PurApItem)
     */
    public boolean processImportItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {

        return SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(item, PurapConstants.ITEM_TAB_ERROR_PROPERTY);
    }
    
    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument The workflow document from which the current route levels are to be obtained.
     * @return List The List of current route levels of the given document.
     */
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Overrides the method in AccountingDocumentRuleBase to always return true.
     * 
     * @param document The document to be validated.
     * @param accountingLine The accounting line whose amount to be validated.
     * @return boolean true.
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {

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

    /**
     * Verifies that the accounting strings are between 0 and 100 percent.
     * 
     * @param account The account whose accounting string is to be validated.
     * @param errorPropertyName The name of the property on the page that we want the error to be displayed.
     * @param itemIdentifier The string representing the item whose account is being validated.
     * @return boolean true if it passes the validation.
     */
    protected boolean verifyAccountingStringsBetween0And100Percent(PurApAccountingLine account, String errorPropertyName, String itemIdentifier) {
        double pct = account.getAccountLinePercent().doubleValue();
        if (pct <= 0 || pct > 100) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, PurapKeyConstants.ERROR_ITEM_PERCENT, "%", itemIdentifier);

            return false;
        }

        return true;
    }

    protected AccountingLineRuleHelperService getAccountingLineRuleHelperService(AccountingDocument accountingDocument) {
        PurapAccountingLineRuleHelperService purapAccountingLineRuleHelperService = SpringContext.getBean(PurapAccountingLineRuleHelperService.class);
        purapAccountingLineRuleHelperService.setDocument((PurchasingAccountsPayableDocument)accountingDocument);
        return purapAccountingLineRuleHelperService;
    }
}
