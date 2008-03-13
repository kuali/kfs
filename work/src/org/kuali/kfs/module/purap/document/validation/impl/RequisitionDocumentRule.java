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
package org.kuali.module.purap.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;

/**
 * Business rule(s) applicable to Requisition document.
 */
public class RequisitionDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * Overrides the method in PurchasingDocumentRuleBase class in order to add validation for the Additional tab. Tabs included on
     * Purchasing Documents are: Payment Info, Delivery, and Additional
     * 
     * @param purapDocument the requisition document to be validated
     * @return boolean false when an error is found in any validation
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processAdditionalValidation((PurchasingDocument) purapDocument);
        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase class to check to see if the Requisition is going to stop
     * at content review route level. If so, then this method returns false, otherwise it will call the
     * requiresAccountValidationOnAllEnteredItems of the superclass, which returns true.
     * 
     * @param document the requisition document to be validated
     * @return boolean false when the Requisition is going to stop at content review route level.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
        if (SpringContext.getBean(PurApWorkflowIntegrationService.class).willDocumentStopAtGivenFutureRouteNode(document, NodeDetailEnum.CONTENT_REVIEW)) {

            return false;
        }

        return super.requiresAccountValidationOnAllEnteredItems(document);
    }

    /**
     * Performs any validation for the Additional tab.
     * 
     * @param purDocument the requisition document to be validated
     * @return boolean false when the validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit returns false.
     */
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        valid = validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(purDocument);

        return valid;
    }

    /**
     * Performs validations for the fields in vendor tab. The business rules to be validated is: If this is a standard
     * order requisition (not B2B), then if Country is United States and the postal code is required and if zip code is entered, it
     * should be a valid US Zip code. (format)
     * 
     * @param purapDocument The requisition document object whose vendor tab is to be validated
     * @return boolean true if it passes vendor validation, otherwise it will return false.
     */
    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(RicePropertyConstants.DOCUMENT);
        boolean valid = super.processVendorValidation(purapDocument);
        RequisitionDocument reqDocument = (RequisitionDocument) purapDocument;
        if (reqDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.STANDARD_ORDER)) {
            if (!StringUtils.isBlank(reqDocument.getVendorCountryCode()) && reqDocument.getVendorCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES) && !StringUtils.isBlank(reqDocument.getVendorPostalCode())) {
                ZipcodeValidationPattern zipPattern = new ZipcodeValidationPattern();
                if (!zipPattern.matches(reqDocument.getVendorPostalCode())) {
                    valid = false;
                    errorMap.putError(PurapPropertyConstants.VENDOR_POSTAL_CODE, PurapKeyConstants.ERROR_POSTAL_CODE_INVALID);
                }
            }
        }
        errorMap.clearErrorPath();

        return valid;
    }

    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @param purDocument the requisition document to be validated
     * @return boolean true if the TotalDollarAmount is less than the PurchaseOrderTotalLimit and false otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && ObjectUtils.isNotNull(((AmountTotaling) purDocument).getTotalDollarAmount())) {
            if (((AmountTotaling) purDocument).getTotalDollarAmount().isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, PurapKeyConstants.ERROR_PURCHASE_ORDER_EXCEEDING_TOTAL_LIMIT);
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();

        return valid;
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase class in order to return true when the Requisition is going to stop
     * at Content Review level.
     * 
     * @param financialDocument the requisition document to be validated
     * @param accountingLine the accounting line to be validated
     * @param action the AccountingLineAction enum that indicates what is being done to an accounting line
     * @return boolean true if the Requisition is going to stop at Content Review Level.
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.rules.AccountingDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (((RequisitionDocument) financialDocument).isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on REQ

            return true;
        }
        else {

            return super.checkAccountingLineAccountAccessibility(financialDocument, accountingLine, action);
        }
    }

    /**
     * Overrides the method in PurchasingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param accountingLine the accounting line to be validated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processAccAccountingLineBusinessRules in PurchasingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return super.processAddAccountingLineBusinessRules(financialDocument, accountingLine);
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param accountingLine the accounting line to be validated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processReviewAccountingLineBusinessRules in PurapAccountingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processReviewAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return super.processReviewAccountingLineBusinessRules(financialDocument, accountingLine);
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param updatedAccountingLine the accounting line that was updated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processUpdateAccountingLineBusinessRules in PurapAccountingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return super.processUpdateAccountingLineBusinessRules(financialDocument, accountingLine, updatedAccountingLine);
    }

    /**
     * Checks whether the account is closed.
     * 
     * @param accountingLine the accounting line to be validated
     * @return boolean true if the account's closed indicator is true and false otherwise.
     */
    private boolean isAccountClosed(AccountingLine accountingLine) {
        accountingLine.refreshNonUpdateableReferences();
        if (accountingLine.getAccount() != null && accountingLine.getAccount().isAccountClosedIndicator()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_REQUISITION_ACCOUNT_CLOSED, accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber());

            return true;
        }

        return false;
    }
        
    /**
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#validateItemCapitalAssetWithErrors(org.kuali.module.purap.bo.PurchasingItemBase, org.kuali.module.purap.bo.RecurringPaymentType, java.lang.String)
     */
    @Override
    public boolean validateItemCapitalAssetWithErrors(PurchasingAccountsPayableDocument purapDocument, PurApItem item, boolean apoCheck) {
        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, PurapParameterConstants.CapitalAsset.OVERRIDE_CAPITAL_ASSET_WARNINGS_IND) ||
                apoCheck) {
            return super.validateItemCapitalAssetWithErrors(purapDocument, item, apoCheck);
        }
        else {
            return true;
        }
    }
}
