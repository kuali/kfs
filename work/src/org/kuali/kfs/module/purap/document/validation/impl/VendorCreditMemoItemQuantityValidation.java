/*
 * Copyright 2009 The Kuali Foundation
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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

public class VendorCreditMemoItemQuantityValidation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    private CreditMemoItem itemForValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument)event.getDocument();
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (itemForValidation.getItemLineNumber() - 1) + "].";
        String errorKey = errorKeyPrefix + PurapPropertyConstants.QUANTITY;
        
        if (itemForValidation.getItemQuantity() != null) {
            if (itemForValidation.getItemQuantity().isNegative()) {
                String label = dataDictionaryService.getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getMessageMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }

            // check cm quantity is not greater than invoiced quantity
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, itemForValidation);
            if (itemForValidation.getItemQuantity().isGreaterThan(invoicedQuantity)) {
                GlobalVariables.getMessageMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_QUANTITY_TOOMUCH);
                valid = false;
            }
        }
        else {
            // check if quantity should be required
            KualiDecimal invoicedQuantity = getSourceTotalInvoiceQuantity(cmDocument, itemForValidation);
            if (itemForValidation.getItemType().isQuantityBasedGeneralLedgerIndicator() && (invoicedQuantity != null && invoicedQuantity.isGreaterThan(KualiDecimal.ZERO)) && (itemForValidation.getExtendedPrice() != null && itemForValidation.getExtendedPrice().isGreaterThan(KualiDecimal.ZERO))) {
                String label = dataDictionaryService.getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.QUANTITY);
                GlobalVariables.getMessageMap().putError(errorKey, KFSKeyConstants.ERROR_REQUIRED, label);
                valid = false;
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
    protected KualiDecimal getSourceTotalInvoiceQuantity(VendorCreditMemoDocument cmDocument, CreditMemoItem item) {
        KualiDecimal invoicedQuantity = null;

        if (cmDocument.isSourceDocumentPurchaseOrder()) {
            invoicedQuantity = item.getPoInvoicedTotalQuantity();
        }
        else {
            invoicedQuantity = item.getPreqInvoicedTotalQuantity();
        }

        return invoicedQuantity;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public CreditMemoItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(CreditMemoItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
