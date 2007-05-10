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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.vendor.VendorPropertyConstants;

public class PurchasingDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase {

    BigDecimal zero = new BigDecimal("0.00");

    /**
     * Tabs included on Purchasing Documents are: Payment Info Delivery Additional
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processItemValidation((PurchasingDocument) purapDocument);
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
    public boolean processItemValidation(PurchasingDocument purDocument) {
        boolean valid = true;
        List<PurchasingApItem> itemList = purDocument.getItems();
        for (PurchasingApItem item : itemList) {
            SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(item);
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ?  "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            valid &= validateItemUnitPrice(item, identifierString);
            valid &= validateUniqueAccountingString(item, identifierString);
            valid &= validateUnitOfMeasureUnitPriceAndDescription(item, identifierString);
            //This validation is applicable to the above the line items only.
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {                
                valid &= validateItemQuantity(item, identifierString);
            }
        }
        valid &= validateTotalCost(purDocument);
        valid &= validateContainsAtLeastOneItem(purDocument);
        return valid;
    }

    /**
     * 
     * This method validates that the document contains at least one item.
     * 
     * @param purDocument
     * @return
     */
    private boolean validateContainsAtLeastOneItem(PurchasingDocument purDocument) {
        boolean valid = false;
        for (PurchasingApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase)item).isEmpty() && item.getItemType().isItemTypeAboveTheLineIndicator()) {
                return true;
            }
        }
        if (! valid) {
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_REQUIRED);
        }
        return valid;
    }
    
    /**
     * This method validates the unit price for all applicable item types
     * 
     * @param purDocument
     * @return
     */
    private boolean validateItemUnitPrice(PurchasingApItem item, String identifierString) {
        boolean valid = true;
        if (ObjectUtils.isNotNull(item.getItemUnitPrice())) {
            if ((zero.compareTo(item.getItemUnitPrice()) > 0) && ((!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) && (!item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {
                // If the item type is not full order discount or trade in items, don't allow negative unit price.
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine.itemUnitPrice", PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, "Unit Cost", identifierString);
                valid = false;
            }
            else if ((zero.compareTo(item.getItemUnitPrice()) < 0) && ((item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) || (item.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)))) {

                // If the item type is full order discount or trade in items, its unit price must be negative.
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine.itemUnitPrice", PurapKeyConstants.ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO, "Unit Cost", identifierString);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * This method validates that all the accounting strings in all of the items must be unique
     * 
     * @param itemList
     * @return
     */
    private boolean validateUniqueAccountingString(PurchasingApItem item, String identifierString) {
        boolean valid = true;
        Set<String> accountingStringSet = new HashSet();
        List<PurApAccountingLine> accountingLines = item.getSourceAccountingLines();
        for (PurApAccountingLine accountingLine : accountingLines) {
            if (accountingStringSet.contains(accountingLine.toString())) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_ACCOUNTING_NOT_UNIQUE, identifierString);
            }
            else {
                accountingStringSet.add(accountingLine.toString());
            }
        }
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
        if (purDocument.getTotalDollarAmount().isLessThan(new KualiDecimal(zero))) {
            valid = false;
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_TOTAL_NEGATIVE);
        }
        return valid;
    }

    /**
     * 
     * This method validates that if the item type is ITEM and the item quantity is not null, the unit of measure
     * is required. The description and unit price are also required field for item type ITEM.
     * If the item type is not ITEM, and the unit price is entered, the description is required; likewise
     * if the description is entered, the unit price is required.
     * 
     * @param item
     * @return
     */
    private boolean validateUnitOfMeasureUnitPriceAndDescription(PurchasingApItem item, String identifierString) {
        boolean valid = true;
        PurchasingItemBase purItem = (PurchasingItemBase) item;
        // Validations for item type "ITEM"
        if (purItem.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) {
            if (ObjectUtils.isNotNull(purItem.getItemQuantity())) {
                if (zero.compareTo(purItem.getItemQuantity().bigDecimalValue()) < 0 && StringUtils.isEmpty(purItem.getItemUnitOfMeasureCode())) {

                    valid = false;
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_QUANTITY_NOT_ZERO, "UOM", identifierString);
                }
            }
            if (StringUtils.isEmpty(item.getItemDescription())) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_EMPTY, "Description", identifierString);
            }
            if (ObjectUtils.isNull(item.getItemUnitPrice())) {
                valid = false;
                GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_EMPTY, "Unit Cost", identifierString);
            }
        }
        // Validations for non "ITEM" item type
        else {
            if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && (zero.compareTo(item.getItemUnitPrice()) != 0)) {
                // if unit price is entered (non-zero), desc is required
                if (StringUtils.isEmpty(item.getItemDescription())) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_DESCRIPTION_EMPTY, "Description", identifierString);
                }
            }
            else if (StringUtils.isNotEmpty(item.getItemDescription())) {
                // if description is entered, unit price is required and cannot be zero
                if (ObjectUtils.isNull(item.getItemUnitPrice()) || (zero.compareTo(item.getItemUnitPrice()) == 0)) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_UNIT_PRICE_EMPTY, "Unit Cost", identifierString);
                }
            }
        }
        return valid;
    }

    /**
     * 
     * This method validates that if the item type is ITEM, the item quantity is required.
     * @param item
     * @return
     */
    private boolean validateItemQuantity(PurchasingApItem item, String identifierString) {
        boolean valid =  true;
        PurchasingItemBase purItem = (PurchasingItemBase)item;
        if ( purItem.getItemTypeCode().equals(ItemTypeCodes.ITEM_TYPE_ITEM_CODE) &&
             ( ObjectUtils.isNull(purItem.getItemQuantity()) || 
               ( ObjectUtils.isNotNull(purItem.getItemQuantity()) && purItem.getItemQuantity().isZero())) )   {
            valid = false;
            GlobalVariables.getErrorMap().putError("newPurchasingItemLine", PurapKeyConstants.ERROR_ITEM_QUANTITY, "Quantity", identifierString);
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
        valid &= checkBeginDateBeforeEndDate(purDocument);

        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderBeginDate()) || ObjectUtils.isNotNull(purDocument.getPurchaseOrderEndDate())) {
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
        else if (ObjectUtils.isNotNull(purDocument.getRecurringPaymentTypeCode())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_RECURRING_TYPE_NO_DATE);
            valid &= false;
        }
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
        // TODO code validation
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
                    errorMap.putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + VendorPropertyConstants.VENDOR_FAX_NUMBER, PurapKeyConstants.ERROR_FAX_NUMBER_INVALID);
                }
            }
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
        DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();

        Date beginDate = purDocument.getPurchaseOrderBeginDate();
        Date endDate = purDocument.getPurchaseOrderEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff( beginDate, endDate, false ) <= 0 ) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_END_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END);
            }
        }
        return valid;
    }


}