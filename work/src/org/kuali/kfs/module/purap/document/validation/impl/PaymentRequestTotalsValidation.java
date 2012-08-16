/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestTotalsValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        String[] excludeArray = { PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE };

        // if NO invoice amount
        if (ObjectUtils.isNull(document.getVendorInvoiceAmount())) {
            if (!KNSGlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID)) {
                KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID);                
            }
        }
        //  if UseTax is included, then the invoiceInitialAmount should be compared against the
        // total amount NOT INCLUDING tax
        else if (document.isUseTaxIndicator()) {
            if (document.getTotalPreTaxDollarAmountAllItems(excludeArray).compareTo(document.getVendorInvoiceAmount()) != 0 && !document.isUnmatchedOverride()) {
                if (!KNSGlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID)) {
                    KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID);                
                }
            }
        }
        //  if NO UseTax, then the invoiceInitialAmount should be compared against the 
        // total amount INCLUDING sales tax (since if the vendor invoices with sales tax, then we pay it)
        else {
            if (document.getTotalDollarAmountAllItems(excludeArray).compareTo(document.getVendorInvoiceAmount()) != 0 && !document.isUnmatchedOverride()) {
                if (!KNSGlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID)) {
                    KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID);                
                }
            }
        }
        
        flagLineItemTotals(document.getItems());
        
        GlobalVariables.getMessageMap().clearErrorPath();
        
        //always returns true, as this is a warning, not an error
        return true;
    }

    /**
     * Calculates a total but excludes passed in item types from the totalling.
     * 
     * @param itemList - list of purap items
     * @param excludedItemTypes - list of item types to exclude from totalling
     * @return
     */
    protected KualiDecimal getTotalExcludingItemTypes(List<PurApItem> itemList, List<String> excludedItemTypes) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (PurApItem item : itemList) {
            if (item.getTotalAmount() != null && item.getTotalAmount().isNonZero()) {
                boolean skipThisItem = false;
                if (excludedItemTypes.contains(item.getItemTypeCode())) {
                    // this item type is excluded
                    skipThisItem = true;
                    break;
                }
                if (skipThisItem) {
                    continue;
                }
                total = total.add(item.getTotalAmount());
            }
        }
        return total;
    }

    /**
     * Flags with an erorr the item totals whos calculated extended price does not equal its extended price.
     * 
     * @param itemList - list of purap items
     */
    protected void flagLineItemTotals(List<PurApItem> itemList) {
        for (PurApItem purApItem : itemList) {
            PaymentRequestItem item = (PaymentRequestItem) purApItem;
            if (item.getItemQuantity() != null && item.getExtendedPrice() !=null) {
                if (item.calculateExtendedPrice().compareTo(item.getExtendedPrice()) != 0) {
                    GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL, item.getItemIdentifierString());
                }
            }
        }
    }

}
