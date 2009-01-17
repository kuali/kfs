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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class VendorCreditMemoProcessItemValidation extends PurchasingAccountsPayableNewIndividualItemValidation {
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument)event.getDocument();
        CreditMemoItem cmItem = (CreditMemoItem)getItemForValidation();
        
        cmItem.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
        if (cmItem.getItemType().isLineItemIndicator()) {
            String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (cmItem.getItemLineNumber() - 1) + "].";

            valid &= validateItemQuantity(cmDocument, cmItem, errorKeyPrefix + PurapPropertyConstants.QUANTITY);
            valid &= validateItemUnitPrice(cmDocument, cmItem, errorKeyPrefix + PurapPropertyConstants.ITEM_UNIT_PRICE);
            valid &= validateItemExtendedPrice(cmDocument, cmItem, errorKeyPrefix + PurapPropertyConstants.EXTENDED_PRICE);

            if (cmItem.getExtendedPrice() != null && cmItem.getExtendedPrice().isNonZero()) {
                valid &= processAccountValidation(cmDocument, cmItem.getSourceAccountingLines(), errorKeyPrefix);
            }
        }
        else {
            String documentTypeClassName = cmDocument.getClass().getName();
            String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
            String documentType = documentTypeArray[documentTypeArray.length - 1];
            valid &= validateBelowTheLineValues(documentType, cmItem);

            if (cmItem.getExtendedPrice() != null && cmItem.getExtendedPrice().isNonZero()) {
                valid &= processAccountValidation(cmDocument, cmItem.getSourceAccountingLines(), cmItem.getItemIdentifierString());
            }
        }

        return valid;
    }

    /**
     * Validates the credit memo quantity for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    private boolean validateItemQuantity(VendorCreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getItemQuantity() != null) {
            if (item.getItemQuantity().isNegative()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }

            // check cm quantity is not greater than invoiced quantity
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, item);
            if (item.getItemQuantity().isGreaterThan(invoicedQuantity)) {
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_QUANTITY_TOOMUCH);
                valid = false;
            }
        }
        else {
            // check if quantity should be required
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, item);
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (invoicedQuantity != null && invoicedQuantity.isGreaterThan(KualiDecimal.ZERO)) && (item.getExtendedPrice() != null && item.getExtendedPrice().isGreaterThan(KualiDecimal.ZERO))) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getErrorMap().putError(errorKey, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the credit memo unit price for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    private boolean validateItemUnitPrice(VendorCreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getItemUnitPrice() != null) {
            // verify unit price is not negative
            if (item.getItemUnitPrice().signum() == -1) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.ITEM_UNIT_PRICE);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the credit memo extended price for an item line.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item
     * @param errorKey - key to associate any generated errors with
     * @return boolean - true if quantity is valid, false if invalid
     */
    private boolean validateItemExtendedPrice(VendorCreditMemoDocument cmDocument, CreditMemoItem item, String errorKey) {
        boolean valid = true;

        if (item.getExtendedPrice() != null) {
            if (item.getExtendedPrice().isNegative()) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.EXTENDED_PRICE);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }
            if (!cmDocument.isSourceVendor()) {
                // check cm extended price is not greater than total invoiced amount
                KualiDecimal invoicedAmount = null;
                if (cmDocument.isSourceDocumentPurchaseOrder()) {
                    invoicedAmount = item.getPoTotalAmount();
                }
                else {
                    invoicedAmount = item.getPreqTotalAmount();
                }

                if (invoicedAmount == null) {
                    invoicedAmount = KualiDecimal.ZERO;
                }

                if (item.getTotalAmount().isGreaterThan(invoicedAmount)) {
                    GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_EXTENDEDPRICE_TOOMUCH);
                    valid = false;
                }
            }

        }

        return valid;
    }

    /**
     * Returns the total invoiced quantity for the item line based on the type of credit memo.
     * 
     * @param cmDocument - credit memo document
     * @param item - credit memo item line to return total invoice quantity
     * @return KualiDecimal - total invoiced quantity
     */
    private KualiDecimal getSourceTotalInvoiceQuantity(VendorCreditMemoDocument cmDocument, CreditMemoItem item) {
        KualiDecimal invoicedQuantity = null;

        if (cmDocument.isSourceDocumentPurchaseOrder()) {
            invoicedQuantity = item.getPoInvoicedTotalQuantity();
        }
        else {
            invoicedQuantity = item.getPreqInvoicedTotalQuantity();
        }

        return invoicedQuantity;
    }

}
