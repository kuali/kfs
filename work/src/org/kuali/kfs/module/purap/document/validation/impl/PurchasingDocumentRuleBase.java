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

public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {

    /**
     * Tabs included on Purchasing Documents are: Payment Info Delivery Additional
     * 
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
     * This method performs any validation for the Item tab
     * 
     * @param purDocument
     * @return
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
        List<PurApItem> itemList = purapDocument.getItems();
        int i = 0;
        for (PurApItem item : itemList) {
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
                // that have no unit cost (KULPURAP-1234)
                valid &= validateBelowTheLineItemNoUnitCost(item, identifierString);
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("document.item[" + i + "]");
            i++;
        }
        valid &= validateTotalCost((PurchasingDocument) purapDocument);
        valid &= validateContainsAtLeastOneItem((PurchasingDocument) purapDocument);
        return valid;
    }

    private boolean validateBelowTheLineItemNoUnitCost(PurApItem item, String identifierString) {

        if (ObjectUtils.isNull(item.getItemUnitPrice()) && ObjectUtils.isNotNull(item.getSourceAccountingLines()) && !item.getSourceAccountingLines().isEmpty()) {

            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE_NO_UNIT_COST, identifierString);
            return false;
        }
        return true;
    }

    /**
     * This method validates that the document contains at least one item.
     * 
     * @param purDocument
     * @return
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
     * This method validates the unit price for all applicable item types. It also validates that the unit price and description
     * fields were entered for all above the line items.
     * 
     * @param purDocument
     * @return
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

    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurApItem item) {
        boolean valid = super.processAddItemBusinessRules(financialDocument, item);
        GlobalVariables.getErrorMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
        valid &= validateItemUnitPrice(item);
        GlobalVariables.getErrorMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_ITEM_LINE);
        return valid;
    }

    /**
     * This method validates that the total cost must be greater or equal to zero
     * 
     * @param purDocument
     * @return
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
     * This method validates that if the item type is ITEM, the unit of measure is required.
     * 
     * @param item
     * @return
     */
    private boolean validateUnitOfMeasure(PurApItem item, String identifierString) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        // Validations for item type "ITEM"
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
            if (StringUtils.isEmpty(purItem.getItemUnitOfMeasureCode())) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, PurapKeyConstants.ERROR_ITEM_QUANTITY_NOT_ZERO, ItemFields.UNIT_OF_MEASURE + " in ", identifierString);
            }
        }
        return valid;
    }

    /**
     * This method validates that if the item type is ITEM, the item quantity is required.
     * 
     * @param item
     * @return
     */
    private boolean validateItemQuantity(PurApItem item, String identifierString) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && (ObjectUtils.isNull(purItem.getItemQuantity()))) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, KFSKeyConstants.ERROR_REQUIRED, ItemFields.QUANTITY + " in " + identifierString);
        }
        else if (purItem.getItemType().isQuantityBasedGeneralLedgerIndicator() && (ObjectUtils.isNull(purItem.getItemQuantity()) || (ObjectUtils.isNotNull(purItem.getItemQuantity()) && purItem.getItemQuantity().isLessEqual(KualiDecimal.ZERO)))) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, KFSKeyConstants.ERROR_EXCLUSIVE_MIN, ItemFields.QUANTITY + " in " + identifierString, KualiDecimal.ZERO.toString() );
        }
        else if (purItem.getItemType().isAmountBasedGeneralLedgerIndicator() && ObjectUtils.isNotNull(purItem.getItemQuantity())) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUANTITY, PurapKeyConstants.ERROR_ITEM_QUANTITY_NOT_ALLOWED);
        }
        return valid;
    }

    /**
     * This method performs any validation for the Payment Info tab.
     * 
     * @param purDocument
     * @return
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
     * This method performs any validation for the Delivery tab.
     * 
     * @param purDocument
     * @return
     */
    public boolean processDeliveryValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        // currently, there is no validation to force at the PUR level for this tab
        return valid;
    }

    @Override
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processVendorValidation(purapDocument);
        PurchasingDocument purDocument = (PurchasingDocument) purapDocument;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {
            // TODO check this; I think we're only supposed to be validation the fax number format if the transmission type is FAX
            // and the vendor ids are null (hjs)
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
     * This method is the implementation of the rule that if a document has a recurring payment begin date and end date, the begin
     * date should come before the end date. In EPIC, we needed to play around with this order if the fiscal year is the next fiscal
     * year, since we were dealing just with month and day, but we don't need to do that here; we're dealing with the whole Date
     * object.
     * 
     * @param purDocument
     * @return
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


    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        if (!verifyAccountingLinePercent((PurApAccountingLine) updatedAccountingLine)) {
            return false;
        }
        return super.processCustomUpdateAccountingLineBusinessRules(accountingDocument, originalAccountingLine, updatedAccountingLine);
    }

    @Override
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        if (!verifyAccountingLinePercent((PurApAccountingLine) accountingLine)) {
            return false;
        }
        return super.processAddAccountingLineBusinessRules(financialDocument, accountingLine);
    }

    private boolean verifyAccountingLinePercent(PurApAccountingLine purapAccountingLine) {
        // make sure it's a whole number
        if (purapAccountingLine.getAccountLinePercent().stripTrailingZeros().scale() > 0) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.ACCOUNTS, PurapKeyConstants.ERROR_PURCHASING_PERCENT_NOT_WHOLE, purapAccountingLine.getAccountLinePercent().toPlainString());
            return false;
        }
        return true;
    }

}