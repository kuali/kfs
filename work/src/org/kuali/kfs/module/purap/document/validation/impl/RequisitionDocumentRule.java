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

import java.util.List;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

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
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
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
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {

//FIXME hjs-finish cleanup
//        if (SpringContext.getBean(PurApWorkflowIntegrationService.class).willDocumentStopAtGivenFutureRouteNode(document, NodeDetailEnum.CONTENT_REVIEW)) {
//            return false;
//        }

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
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @param purDocument the requisition document to be validated
     * @return boolean true if the TotalDollarAmount is less than the PurchaseOrderTotalLimit and false otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
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
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase.AccountingLineAction)
     */
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (((RequisitionDocument) financialDocument).isDocumentStoppedInRouteNode(NodeDetailEnum.CONTENT_REVIEW)) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on REQ

            return true;
        }
        else {

            return false;
        }
    }

    /**
     * Overrides the method in PurchasingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param accountingLine the accounting line to be validated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processAccAccountingLineBusinessRules in PurchasingDocumentRuleBase.
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, String collectionName) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return super.processAddAccountingLineBusinessRules(financialDocument, accountingLine, collectionName);
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param accountingLine the accounting line to be validated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processReviewAccountingLineBusinessRules in PurapAccountingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processReviewAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean processReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return true;
    }

    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to return false if the account is closed.
     * 
     * @param financialDocument the requisition document to be validated
     * @param updatedAccountingLine the accounting line that was updated
     * @return boolean false if the account is closed, otherwise it will return the result of the
     *         processUpdateAccountingLineBusinessRules in PurapAccountingDocumentRuleBase.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processUpdateAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        // make sure it's active for usage
        if (isAccountClosed(accountingLine)) {

            return false;
        }

        return true;
    }

    /**
     * Checks whether the account is closed.
     * 
     * @param accountingLine the accounting line to be validated
     * @return boolean true if the account's closed indicator is true and false otherwise.
     */
    private boolean isAccountClosed(AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (accountingLine.getAccount() != null && !accountingLine.getAccount().isActive()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ACCOUNT_NUMBER, PurapKeyConstants.ERROR_REQUISITION_ACCOUNT_CLOSED, accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber());

            return true;
        }

        return false;
    }
        
    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#validateItemCapitalAssetWithErrors(org.kuali.kfs.module.purap.businessobject.PurchasingItemBase, org.kuali.kfs.module.purap.businessobject.RecurringPaymentType, java.lang.String)
     */
 // TODO: The method has been moved from PurchasingDocumentRuleBase to CapitalAssetBuilderModuleServiceImpl, need to determine what to do about this.    
//    @Override
//    public boolean validateItemCapitalAssetWithErrors(PurchasingAccountsPayableDocument purapDocument, PurApItem item, boolean apoCheck) {
//        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, PurapParameterConstants.CapitalAsset.OVERRIDE_CAPITAL_ASSET_WARNINGS_IND) ||
//                apoCheck) {
//            return super.validateItemCapitalAssetWithErrors(purapDocument, item, apoCheck);
//        }
//        else {
//            return true;
//        }
//    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#commodityCodeIsRequired()
     */
    @Override
    protected boolean commodityCodeIsRequired() {
        return SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND);
    }
}
