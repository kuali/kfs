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

import java.util.Map;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase {

/**
     * Tabs included on Payment Request Documents are:
     *   Invoice
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processInvoiceValidation((PaymentRequestDocument)purapDocument);
       // valid &= processPurchaseOrderIDValidation((PaymentRequestDocument)purapDocument);
        return valid;
    }


/**
     * This method performs any validation for the Invoice tab.
     * 
     * @param preqDocument
     * @return
     */
    public boolean processInvoiceValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        //TODO code validation here
        return valid;
    }
    
    boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {
        //boolean valid = super.processItemValidation(document);
        boolean valid = true;
        PurchaseOrderDocument purchaseOrderDocument = new PurchaseOrderDocument();
        purchaseOrderDocument.setPurapDocumentIdentifier(document.getPurchaseOrderIdentifier());
        Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(purchaseOrderDocument);
        purchaseOrderDocument = (PurchaseOrderDocument) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(PurchaseOrderDocument.class, keys);
        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            //GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
        }
       
        return valid;
    }
    

}
