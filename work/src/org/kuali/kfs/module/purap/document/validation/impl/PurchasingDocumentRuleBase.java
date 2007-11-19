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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorHeader;
import org.kuali.module.vendor.service.VendorService;

/**
 * Business rule(s) applicable to Purchasing document.
 */
public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add validations for Payment Info and Delivery tabs.
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processPaymentInfoValidation((PurchasingDocument) purapDocument);
        valid &= processDeliveryValidation((PurchasingDocument) purapDocument);

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add the validations for the unit price, unit of measure,
     * item quantity (for above the line items), the validateBelowTheLineItemNoUnitcost, validateTotalCost and
     * validateContainsAtLeastOneItem.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processItemValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
        List<PurApItem> itemList = purapDocument.getItems();
        int i = 0;
        for (PurApItem item : itemList) {
            // refresh item type for validation
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            GlobalVariables.getErrorMap().addToErrorPath("document.item[" + i + "]");
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            valid &= validateItemUnitPrice(item);
            valid &= validateUnitOfMeasure(item, identifierString);
            // This validation is applicable to the above the line items only.
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                valid &= validateItemQuantity(item, identifierString);
            }
            else {
                // If the item is below the line, no accounts can be entered on below the line items
                // that have no unit cost
                valid &= validateBelowTheLineItemNoUnitCost(item, identifierString);
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
            i++;
        }
        valid &= validateTotalCost((PurchasingDocument) purapDocument);
        valid &= validateContainsAtLeastOneItem((PurchasingDocument) purapDocument);

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to also invoke the validateAccountNotExpired for each of
     * the accounts.
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processAccountValidation(org.kuali.kfs.document.AccountingDocument,
     *      java.util.List, java.lang.String)
     */
    @Override
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;
        for (PurApAccountingLine accountingLine : purAccounts) {
            boolean notExpired = this.validateAccountNotExpired(accountingLine);
            if (!notExpired) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, itemLineNumber + " has ", accountingLine.getAccount().getAccountNumber());
            }
        }
        valid &= super.processAccountValidation(accountingDocument, purAccounts, itemLineNumber);

        return valid;
    }

    /**
     * Validates that if the item unit price is null and the source accounting lines is not empty, add error message and return
     * false.
     * 
     * @param item the item to be validated
     * @param identifierString the identifierString of the item to be validated
     * @return boolean false if the item unit price is null and the source accounting lines is not empty.
     */
    private boolean validateBelowTheLineItemNoUnitCost(PurApItem item, String identifierString) {
        if (ObjectUtils.isNull(item.getItemUnitPrice()) && ObjectUtils.isNotNull(item.getSourceAccountingLines()) && !item.getSourceAccountingLines().isEmpty()) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE_NO_UNIT_COST, identifierString);

            return false;
        }

        return true;
    }

    /**
     * Validates that the document contains at least one item.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the document does not contain at least one item.
     */
    private boolean validateContainsAtLeastOneItem(PurchasingDocument purDocument) {
        boolean valid = false;
        for (PurApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase) item).isEmpty() && item.getItemType().isItemTypeAboveTheLineIndicator()) {

                return true;
            }
        }
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(purDocument.getDocumentHeader().getWorkflowDocument().getDocumentType()).getLabel();

        if (!valid) {
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        }

        return valid;
    }

    /**
     * Validates the unit price for all applicable item types. It also validates that the unit price and description fields were
     * entered for all above the line items.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there is any validation that fails.
     */
    private boolean validateItemUnitPrice(PurApItem item) {
        boolean valid = true;
        if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
            if (ObjectUtils.isNull(item.getItemUnitPrice())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, KFSKeyConstants.ERROR_REQUIRED, ItemFields.UNIT_COST + " in " + item.getItemIdentifierString());
            }
            if (StringUtils.isEmpty(item.getItemDescription())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, KFSKeyConstants.ERROR_REQUIRED, ItemFields.DESCRIPTION + " in " + item.getItemIdentifierString());
            }
        }
        if (ObjectUtils.isNotNull(item.getItemUnitPrice())) {
            if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) > 0) && ((!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) && (!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is not full order discount or trade in items, don't allow negative unit price.
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
            else if ((BigDecimal.ZERO.compareTo(item.getItemUnitPrice()) < 0) && ((item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is full order discount or trade in items, its unit price must be negative.
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO, ItemFields.UNIT_COST, item.getItemIdentifierString());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to add invocation to validateItemUnitPrice.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param item the item to be validated
     * @return boolean false if there is any fail validation
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processAddItemBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.module.purap.bo.PurApItem)
     */
    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processAddItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
        valid &= validateItemUnitPrice(item);
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);

        return valid;
    }

    /**
     * Validates that the total cost must be greater or equal to zero
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the total cost is less than zero.
     */
    private boolean validateTotalCost(PurchasingDocument purDocument) {
        boolean valid = true;
        if (purDocument.getTotalDollarAmount().isLessThan(new KualiDecimal(BigDecimal.ZERO))) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_TOTAL_NEGATIVE);
        }

        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the unit of measure is required.
     * 
     * @param item the item to be validated
     * @return boolean false if the item type is quantity based and the unit of measure is empty.
     */
    private boolean validateUnitOfMeasure(PurApItem item, String identifierString) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        // Validations for quantity based item type
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
            if (StringUtils.isEmpty(purItem.getItemUnitOfMeasureCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, KFSKeyConstants.ERROR_REQUIRED, ItemFields.UNIT_OF_MEASURE + " in " + identifierString);
            }
        }

        return valid;
    }

    /**
     * Validates that if the item type is quantity based, the item quantity is required and if the item type is amount based, the
     * quantity is not allowed.
     * 
     * @param item the item to be validated
     * @return boolean false if there's any validation that fails.
     */
    private boolean validateItemQuantity(PurApItem item, String identifierString) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && (ObjectUtils.isNull(purItem.getItemQuantity()))) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, KFSKeyConstants.ERROR_REQUIRED, ItemFields.QUANTITY + " in " + identifierString);
        }
        else if (purItem.getItemType().isAmountBasedGeneralLedgerIndicator() && ObjectUtils.isNotNull(purItem.getItemQuantity())) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, PurapKeyConstants.ERROR_ITEM_QUANTITY_NOT_ALLOWED, ItemFields.QUANTITY + " in " + identifierString);
        }

        return valid;
    }

    /**
     * Performs any validation for the Payment Info tab.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if there's any validation that fails.
     */
    public boolean processPaymentInfoValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);
        valid &= checkBeginDateBeforeEndDate(purDocument);
        if (valid && (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) || ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate()))) {
            if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNull(purDocument.getPurchaseOrderEndDate())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE);
                valid &= false;
            }
            else {
                if (ObjectUtils.isNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
                    valid &= false;
                }
            }
        }
        if (valid && ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) && ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
            if (ObjectUtils.isNull(purDocument.getRecurringPaymentTypeCode())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.RECURRING_PAYMENT_TYPE_CODE, PurapKeyConstants.ERROR_RECURRING_DATE_NO_TYPE);

                valid &= false;
            }
        }
        else if (valid && ObjectUtils.isNotNull(purDocument.getRecurringPaymentTypeCode())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
            valid &= false;
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapConstants.PAYMENT_INFO_ERRORS);

        return valid;
    }

    /**
     * Performs any validation for the Delivery tab.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean true (for now it will always return true; this might change someday in the future)
     */
    public boolean processDeliveryValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        // currently, there is no validation to force at the PUR level for this tab

        return valid;
    }

    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to do all of the vendor validations. The method in
     * PurchasingAccountsPayableDocumentBase currently does not do any validation (it only returns true all the time).
     * 
     * @param purapDocument the purchasing document to be validated
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processVendorValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processVendorValidation(purapDocument);
        PurchasingDocument purDocument = (PurchasingDocument) purapDocument;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            if (StringUtils.isNotBlank(purDocument.getVendorFaxNumber())) {
                PhoneNumberValidationPattern phonePattern = new PhoneNumberValidationPattern();
                if (!phonePattern.matches(purDocument.getVendorFaxNumber())) {
                    valid &= false;
                    errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
        }

        VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(purDocument.getVendorHeaderGeneratedIdentifier(), purDocument.getVendorDetailAssignedIdentifier());
        if (ObjectUtils.isNull(vendorDetail))
            return valid;
        VendorHeader vendorHeader = vendorDetail.getVendorHeader();

        // make sure that the vendor is not debarred
        if (vendorDetail.isVendorDebarred()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_DEBARRED_VENDOR);
        }

        // make sure that the vendor is of allowed type
        String allowedVendorType = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapRuleConstants.PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO);
        if (ObjectUtils.isNotNull(vendorHeader) && ObjectUtils.isNotNull(vendorHeader.getVendorTypeCode()) && !vendorHeader.getVendorTypeCode().equals(allowedVendorType)) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE);
        }

        // make sure that the vendor is active
        if (!vendorDetail.isActiveIndicator()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INACTIVE_VENDOR);
        }

        return valid;
    }


    /**
     * Implementation of the rule that if a document has a recurring payment begin date and end date, the begin date should come
     * before the end date. In EPIC, we needed to play around with this order if the fiscal year is the next fiscal year, since we
     * were dealing just with month and day, but we don't need to do that here; we're dealing with the whole Date object.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the begin date is not before the end date.
     */
    private boolean checkBeginDateBeforeEndDate(PurchasingDocument purDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date beginDate = purDocument.getPurchaseOrderBeginDate();
        Date endDate = purDocument.getPurchaseOrderEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff(beginDate, endDate, false) <= 0) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }

        return valid;
    }


    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to invoke the verifyAccountingLinePercent.
     * 
     * @param accountingDocument the purchasing document to be validated
     * @param originalAccountingLine the original accounting line
     * @param updatedAccountingLine the updated accounting line
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        if (!super.processCustomUpdateAccountingLineBusinessRules(accountingDocument, originalAccountingLine, updatedAccountingLine)) {

            return false;
        }

        return verifyAccountingLinePercent((PurApAccountingLine) updatedAccountingLine);
    }


    /**
     * Overrides the method in PurapAccountingDocumentRuleBase to also invoke the validateAccountNotExpired and
     * verifyAccountingLinePercent.
     * 
     * @param financialDocument the purchasing document to be validated
     * @param accountingLine the accounting line to be validated before being added
     * @return boolean false if there's any validation that fails.
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#processAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean valid = validateAccountNotExpired(accountingLine);
        if (!valid) {
            GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, KFSConstants.EMPTY_STRING, accountingLine.getAccountNumber());
        }
        valid &= super.processAddAccountingLineBusinessRules(financialDocument, accountingLine);
        if (!valid) {

            return false;
        }

        return verifyAccountingLinePercent((PurApAccountingLine) accountingLine);
    }

    /**
     * Validates that the account is not expired.
     * 
     * @param accountingLine The account to be validated.
     * @return boolean false if the account is expired.
     */
    private boolean validateAccountNotExpired(AccountingLine accountingLine) {
        accountingLine.refreshNonUpdateableReferences();
        if (accountingLine.getAccount() != null && accountingLine.getAccount().isExpired()) {

            return false;
        }

        return true;
    }

    /**
     * Verifies that the accounting line percent is a whole number.
     * 
     * @param purapAccountingLine the accounting line to be validated
     * @return boolean false if the accounting line percent is not a whole number.
     */
    private boolean verifyAccountingLinePercent(PurApAccountingLine purapAccountingLine) {
        // make sure it's a whole number
        if (purapAccountingLine.getAccountLinePercent().stripTrailingZeros().scale() > 0) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_PURCHASING_PERCENT_NOT_WHOLE, purapAccountingLine.getAccountLinePercent().toPlainString());

            return false;
        }

        return true;
    }

}