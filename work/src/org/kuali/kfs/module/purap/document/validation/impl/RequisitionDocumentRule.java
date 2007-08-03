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

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.datadictionary.validation.fieldlevel.ZipcodeValidationPattern;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapConstants.WorkflowConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

public class RequisitionDocumentRule extends PurchasingDocumentRuleBase {
   
    /**
     * Tabs included on Purchasing Documents are: Payment Info Delivery Additional
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processAdditionalValidation((PurchasingDocument) purapDocument);
        return valid;
    }
    
    /**
     * Validation for the items. The difference between this method and the processItemValidation in the
     * PurchasingAccountsPayableDocumentRuleBase is that we don't require the items to have accounts in
     * Requisition, therefore we don't validate accounts.
     * 
     * @see org.kuali.module.purap.rules.PurchasingDocumentRuleBase#processItemValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];

        for (PurchasingApItem item : purapDocument.getItems()) {
            item.refreshNonUpdateableReferences();

            // do the DD validation first, I wonder if the original one from DocumentRuleBase is broken ? 
            getDictionaryValidationService().validateBusinessObject(item);
        }

        if (SpringServiceLocator.getPurapService().isDocumentStoppingAtRouteLevel(purapDocument, PurapConstants.WorkflowConstants.RequisitionDocument.NodeDetails.ORDERED_NODE_NAME_LIST, PurapConstants.WorkflowConstants.RequisitionDocument.NodeDetails.CONTENT_REVIEW)) {
            for (PurchasingApItem item : purapDocument.getItems()) {
                item.refreshNonUpdateableReferences();

                // only do these check for below the line items
                if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                    if (item.getSourceAccountingLines().size() == 0) {
                        GlobalVariables.getErrorMap().putError(KFSConstants.ITEM_LINE_ERRORS, PurapKeyConstants.ERROR_NO_ACCOUNTS);
                        valid = false;
                        break;
                    }

                    BigDecimal totalPercentage = new BigDecimal(0);
                    for (PurApAccountingLine accountingLine : item.getSourceAccountingLines()) {
                        totalPercentage = totalPercentage.add(accountingLine.getAccountLinePercent());
                    }
                    if (!totalPercentage.equals(new BigDecimal(100))) {
                        GlobalVariables.getErrorMap().putError(KFSConstants.ITEM_LINE_ERRORS, PurapKeyConstants.ERROR_NOT_100_PERCENT);
                        valid = false;
                        break;
                    }
                }
            }
        }

        List<PurchasingApItem> itemList = purapDocument.getItems();
        if (itemList.size() <= purapDocument.getBelowTheLineTypes().length) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ITEM_LINE_ERRORS, PurapKeyConstants.ERROR_NO_ITEMS);
            valid = false;
        }

        String securityGroup = (String)PurapConstants.ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP.get(documentType);
        KualiParameterRule allowsZeroRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_ZERO);
        KualiParameterRule allowsPositiveRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_POSITIVE);
        KualiParameterRule allowsNegativeRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        KualiParameterRule requiresDescriptionRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);

        for (PurchasingApItem item : purapDocument.getItems()) {
            item.refreshNonUpdateableReferences();

            //only do this check for below the line items
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (ObjectUtils.isNotNull(item.getItemUnitPrice()) &&(new KualiDecimal(item.getItemUnitPrice())).isZero()) {
                    if (allowsZeroRule.getRuleActiveIndicator() &&
                        !allowsZeroRule.getParameterValueSet().contains(item.getItemTypeCode())) {
                        valid = false;
                        GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "zero");
                    }
                }
                else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isPositive()) {
                    if (allowsPositiveRule.getRuleActiveIndicator() &&
                        !allowsPositiveRule.getParameterValueSet().contains(item.getItemTypeCode())) {
                        valid = false;
                        GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "positive");
                    }
                }
                else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNegative()) {
                    if (allowsNegativeRule.getRuleActiveIndicator() &&
                        !allowsNegativeRule.getParameterValueSet().contains(item.getItemTypeCode())) {
                        valid = false;
                        GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "negative");
                    }
                }
                if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && StringUtils.isEmpty(item.getItemDescription())) {
                    if (requiresDescriptionRule.getRuleActiveIndicator() &&
                        requiresDescriptionRule.getParameterValueSet().contains(item.getItemTypeCode())) {
                        valid = false;
                        GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, "The item description of " + item.getItemType().getItemTypeDescription(), "empty");
                    }
                }
            }
        }
        return valid;

    }
    
    /**
     * This method performs any validation for the Additional tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processAdditionalValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        valid = validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(purDocument);
        return valid;
    }
    
    //TODO check this: (hjs) 
    //- just curious: what is a valid US zip code?
    //- isn't city required if country is US? NO, ONLY FOR PO
    //- comment list fax number, but code isn't here
    //Can this be combined with PO?
    /**
     * 
     * This method performs validations for the fields in vendor tab.
     * The business rules to be validated are:
     * 1.  If this is a standard order requisition (not B2B), then if Country is United 
     *     States and the postal code is required and if zip code is entered, it should 
     *     be a valid US Zip code. (format)
     * 2.  If this is a standard order requisition (not a B2B requisition), then if 
     *     the fax number is entered, it should be a valid fax number. (format) 
     *     
     * @param purapDocument The requisition document object whose vendor tab is to be validated
     * 
     * @return true if it passes vendor validation and false otherwise.
     */
    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(RicePropertyConstants.DOCUMENT);
        boolean valid = super.processVendorValidation(purapDocument);
        RequisitionDocument reqDocument = (RequisitionDocument)purapDocument;
        if (reqDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.STANDARD_ORDER)) { 
            if (!StringUtils.isBlank(reqDocument.getVendorCountryCode()) &&
                    reqDocument.getVendorCountryCode().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES) && 
                !StringUtils.isBlank(reqDocument.getVendorPostalCode())) {
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
     * @return True if the TotalDollarAmount is less than the PurchaseOrderTotalLimit. False otherwise.
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

    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        List currentRouteLevels = getCurrentRouteLevels(workflowDocument);

        if (currentRouteLevels.contains(WorkflowConstants.RequisitionDocument.NodeDetails.CONTENT_REVIEW) && workflowDocument.isApprovalRequested()) {
            // DO NOTHING: do not check that user owns acct lines; at this level, approvers can edit all detail on REQ
            return true;
        }
        
        else {
            return super.checkAccountingLineAccountAccessibility(financialDocument, accountingLine, action);
        }
    }
    
    /**
     * @see org.kuali.kfs.rules.GeneralLedgerPostingDocumentRuleBase#populateOffsetGeneralLedgerPendingEntry(java.lang.Integer,
     *      org.kuali.kfs.bo.GeneralLedgerPendingEntry, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper,
     *      org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected boolean populateOffsetGeneralLedgerPendingEntry(Integer universityFiscalYear, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry offsetEntry) {
        //Requisition doesn't generate GL entries
        return true;
    } 

}
