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

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestDateForContinueValidation extends GenericValidation {

    private PaymentRequestService paymentRequestService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        // invoice date validation
        java.sql.Date invoiceDate = document.getInvoiceDate();
        if (ObjectUtils.isNotNull(invoiceDate) && paymentRequestService.isInvoiceDateAfterToday(invoiceDate)) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE);
            valid &= false;
        }
        
        GlobalVariables.getMessageMap().clearErrorPath();
        
        return valid;
    }

    public PaymentRequestService getPaymentRequestService() {
        return paymentRequestService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

}
