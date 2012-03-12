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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.PaymentMedium;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlPaymentMediumValidation extends GenericValidation {

    private CashControlDocument cashControlDocument;
    private BusinessObjectService businessObjectService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMediumCode = cashControlDocument.getCustomerPaymentMediumCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("customerPaymentMediumCode", paymentMediumCode);

        PaymentMedium paymentMedium = (PaymentMedium) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentMedium.class, criteria);

        if (paymentMedium == null) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.CUSTOMER_PAYMENT_MEDIUM_CODE, ArKeyConstants.ERROR_PAYMENT_MEDIUM_IS_NOT_VALID);
            isValid = false;
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
