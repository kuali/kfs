/*
 * Copyright 2009 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class VendorCreditMemoItemExtendedPriceValidation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    private CreditMemoItem itemForValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument)event.getDocument();
        
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (itemForValidation.getItemLineNumber() - 1) + "].";
        String errorKey = errorKeyPrefix + PurapPropertyConstants.EXTENDED_PRICE;
        
        if (itemForValidation.getExtendedPrice() != null) {
            if (itemForValidation.getExtendedPrice().isNegative()) {
                String label = dataDictionaryService.getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.EXTENDED_PRICE);
                GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
            }
            if (!cmDocument.isSourceVendor()) {
                // check cm extended price is not greater than total invoiced amount
                KualiDecimal invoicedAmount = null;
                if (cmDocument.isSourceDocumentPurchaseOrder()) {
                    invoicedAmount = itemForValidation.getPoTotalAmount();
                }
                else {
                    invoicedAmount = itemForValidation.getPreqTotalAmount();
                }

                if (invoicedAmount == null) {
                    invoicedAmount = KualiDecimal.ZERO;
                }

                if (itemForValidation.getTotalAmount().isGreaterThan(invoicedAmount)) {
                    GlobalVariables.getErrorMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_EXTENDEDPRICE_TOOMUCH);
                    valid = false;
                }
            }

        }

        return valid;
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
