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
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

public class VendorCreditMemoItemUnitPriceValidation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    private CreditMemoItem itemForValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        String errorKeyPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (itemForValidation.getItemLineNumber() - 1) + "].";
        String errorKey = errorKeyPrefix + PurapPropertyConstants.ITEM_UNIT_PRICE;
        
        if (itemForValidation.getItemUnitPrice() != null) {
            // verify unit price is not negative
            if (itemForValidation.getItemUnitPrice().signum() == -1) {
                String label = dataDictionaryService.getAttributeErrorLabel(CreditMemoItem.class, PurapPropertyConstants.ITEM_UNIT_PRICE);
                GlobalVariables.getMessageMap().putError(errorKey, PurapKeyConstants.ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE, label);
                valid = false;
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
