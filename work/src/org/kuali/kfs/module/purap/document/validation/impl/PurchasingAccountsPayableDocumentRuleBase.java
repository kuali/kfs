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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.AddPurchasingAccountsPayableItemRule;

import edu.iu.uis.eden.exception.WorkflowException;

public class PurchasingAccountsPayableDocumentRuleBase extends AccountingDocumentRuleBase implements AddPurchasingAccountsPayableItemRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
        //TODO: Chris - look into this for KULPURAP-1191, this worked but caused unwanted errors about document.sourceAccountingLines
        //isValid &= super.processCustomRouteDocumentBusinessRules(purapDocument);
        return isValid &= processValidation(purapDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) approveEvent.getDocument();
        return isValid &= processValidation(purapDocument);
    }
    
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean isValid = true;
        
        return isValid;
    }
    
    @Override
    public boolean processDeleteAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        //I think PURAP's accounting line is a bit different than other documents. The source accounting line is per item, not per document.
        //Besides, we already have other item validations that determined whether the items contain at least one account wherever applicable.
        //So this will be redundant if we do another validation, therefore we'll return true here so that it would not give the error about
        //can't delete the last remaining accessible account anymore.
        return true;
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
        // currently, there is no validation to force at the PURAP level for this tab
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
        // currently, there is no validation to force at the PURAP level for this tab
        return valid;
    }

    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
        return true;
    }
    
    /**
     * This method performs any validation for the Item tab.
     * 
     * @param purapDocument
     * @param needAccountValidation boolean that indicates whether we need account validation.
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

        boolean requiresAccountValidationOnAllEnteredItems = requiresAccountValidationOnAllEnteredItems(purapDocument);
        int i=0;
        for (PurApItem item : purapDocument.getItems()) {
            
            //do the DD validation first, I wonder if the original one from DocumentRuleBase is broken ? 
            getDictionaryValidationService().validateBusinessObject(item);
            
            if (item.isConsideredEntered()) {
                GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
                //only do this check for below the line items
                if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                    valid &= valideBelowTheLineValues(documentType, null, item);
                }
                GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
                
                if (requiresAccountValidationOnAllEnteredItems || (!item.getSourceAccountingLines().isEmpty())) {
                    processAccountValidation(purapDocument, item.getSourceAccountingLines(),item.getItemIdentifierString());
                }
//                // only check active items
//                if(PurApItemUtils.checkItemActive(item)) {
//                    // check account validation if we require it on all items or if there is at least one account on the item
//                    if (requiresAccountValidationOnAllEnteredItems || (!item.getSourceAccountingLines().isEmpty())) {
//                        processAccountValidation(item.getSourceAccountingLines(),item.getItemIdentifierString());
//                    }
//                }
            }
            i++;
        }
        return valid;
    }

    protected boolean valideBelowTheLineValues(String documentType, String fromSourceDocument, PurApItem item) {
        boolean valid = true;
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String parameterDetailTypeCode = (String)PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType);
        Parameter allowsZeroRule = kualiConfigurationService.getParameter( KFSConstants.PURAP_NAMESPACE, parameterDetailTypeCode, PurapConstants.ITEM_ALLOWS_ZERO);
        Parameter allowsPositiveRule = kualiConfigurationService.getParameter( KFSConstants.PURAP_NAMESPACE, parameterDetailTypeCode, PurapConstants.ITEM_ALLOWS_POSITIVE);
        Parameter allowsNegativeRule = kualiConfigurationService.getParameter( KFSConstants.PURAP_NAMESPACE, parameterDetailTypeCode, PurapConstants.ITEM_ALLOWS_NEGATIVE);
        Parameter requiresDescriptionRule = kualiConfigurationService.getParameter( KFSConstants.PURAP_NAMESPACE, parameterDetailTypeCode, PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION);

        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) &&(new KualiDecimal(item.getItemUnitPrice())).isZero()) {
                        if (kualiConfigurationService.isUsable( allowsZeroRule ) &&
                                !kualiConfigurationService.getParameterValuesAsSet(allowsZeroRule).contains(item.getItemTypeCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "zero");
            }
        }
        else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isPositive()) {
                        if (kualiConfigurationService.isUsable( allowsPositiveRule ) &&
                                !kualiConfigurationService.getParameterValuesAsSet(allowsPositiveRule).contains(item.getItemTypeCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "positive");
            }
        }
        else if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNegative()) {
                        if (kualiConfigurationService.isUsable( allowsNegativeRule ) &&
                                !kualiConfigurationService.getParameterValuesAsSet(allowsNegativeRule).contains(item.getItemTypeCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, item.getItemType().getItemTypeDescription(), "negative");
            }
        }
        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (new KualiDecimal(item.getItemUnitPrice())).isNonZero() && StringUtils.isEmpty(item.getItemDescription())) {
                        if (kualiConfigurationService.isUsable( requiresDescriptionRule ) &&
                                kualiConfigurationService.getParameterValuesAsSet(requiresDescriptionRule).contains(item.getItemTypeCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, "The item description of " + item.getItemType().getItemTypeDescription(), "empty");
            }
        }
        return valid;
    }

    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        return getDictionaryValidationService().isBusinessObjectValid(item, PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
    }

    /**
     * A helper method for determining the route levels for a given document.
     * 
     * @param workflowDocument
     * @return List
     */
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return false;
    }
    
    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        return true;
    }


    /**
     * This method performs any additional document level validation for the accounts
     * 
     * @param purapDocument
     * @return
     */
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;
        valid = valid & verifyHasAccounts(purAccounts,itemLineNumber);
        // if we don't have any accounts... not need to run any further validation as it will all fail
        if (valid) {
            valid = valid & verifyAccountPercent(accountingDocument, purAccounts,itemLineNumber);
        }
        //We can't invoke the verifyUniqueAccountingStrings in here because otherwise it would be invoking it more than once, if we're also
        //calling it upon Save.
        valid &= verifyUniqueAccountingStrings(purAccounts, PurapConstants.ITEM_TAB_ERROR_PROPERTY, itemLineNumber);
        return valid;
    }

    protected boolean verifyHasAccounts(List<PurApAccountingLine>purAccounts,String itemLineNumber) {
        boolean valid = true;
        
        if(purAccounts.isEmpty()) {
            valid=false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_INCOMPLETE, itemLineNumber);
        }
        
        return valid;
    }
    
    /**
     * This method verifies account percent
     * @param purAccounts
     * @return
     */
    protected boolean verifyAccountPercent(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts,String itemLineNumber) {
        boolean valid = true;
        
        //validate that the percents total 100 for each item
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal desiredPercent = new BigDecimal("100");
        for (PurApAccountingLine account : purAccounts) {
            totalPercent = totalPercent.add(account.getAccountLinePercent());
        }
        
        if(desiredPercent.compareTo(totalPercent)!=0) {
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, itemLineNumber);
            valid = false;
        }
        return valid;
    }
    
    /**
     * 
     * This method verifies that the accounting strings entered are unique for each item. 
     * 
     * @param purAccounts
     * @param itemLineNumber
     * @return
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
    
    protected boolean verifyAccountingStringsBetween0And100Percent(PurApAccountingLine account, String errorPropertyName, String itemIdentifier) {
        double pct = account.getAccountLinePercent().doubleValue();
        if (pct <= 0 || pct > 100) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, PurapKeyConstants.ERROR_ITEM_PERCENT, "%", itemIdentifier);
            return false;
        }
        return true;
    }
}
