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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.AddPurchasingAccountsPayableItemRule;

public class PurchasingAccountsPayableDocumentRuleBase extends AccountingDocumentRuleBase implements AddPurchasingAccountsPayableItemRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
        return isValid &= processValidation(purapDocument);
    }

    //TODO should we call our validation here?
//    @Override
//    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
//        boolean isValid = true;
//        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
//        return isValid &= processValidation(purapDocument);
//    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) approveEvent.getDocument();
        return isValid &= processValidation(purapDocument);
    }

    /**
     * This method calls each tab specific validation.  Tabs included on all PURAP docs are:
     *   DocumentOverview
     *   Vendor
     *   Item
     * 
     * @param purapDocument
     * @return
     */
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        valid &= processDocumentOverviewValidation(purapDocument);
        valid &= processVendorValidation(purapDocument);
        valid &= processItemValidation(purapDocument);
        return valid;
    }

    /**
     * This method performs any validation for the Document Overview tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processDocumentOverviewValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method performs any validation for the Vendor tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method performs any validation for the Item tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];
        //If it's a credit memo, we'll have to append the source of the credit memo
        //whether it's created from a Vendor, a PO or a PREQ.
        if (documentType.equals("CreditMemoDocument")) {
           
        }
        String securityGroup = (String)PurapConstants.ITEM_TYPE_SYSTEM_PARAMETERS_SECURITY_MAP.get(documentType);
        KualiParameterRule allowsZeroRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_ZERO);
        KualiParameterRule allowsPositiveRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_POSITIVE);
        KualiParameterRule allowsNegativeRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        KualiParameterRule requiresDescriptionRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(securityGroup, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);

        for (PurchasingApItem item : purapDocument.getItems()) {
            //only do this check for below the line items
            item.refreshNonUpdateableReferences();
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

    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurchasingApItem item) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        // TODO Auto-generated method stub
        return true;
    }

}
