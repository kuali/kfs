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

import java.util.ArrayList;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentRequestPayDateNotOverThresholdDaysAwayValidation extends GenericValidation {

    private PurapService purapService;
    
    /**
     * This method side-effects a warning, and consequently should not be used in such a way as to cause validation to fail. Returns
     * a boolean for ease of testing. If the threshold days value is positive, the method will test future dates accurately. If the
     * the threshold days value is negative, the method will test past dates.
     * 
     */
    public boolean validate(AttributedDocumentEvent event) {
        PaymentRequestDocument document = (PaymentRequestDocument)event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        int thresholdDays = PurapConstants.PREQ_PAY_DATE_DAYS_BEFORE_WARNING;
        if ((document.getPaymentRequestPayDate() != null) && purapService.isDateMoreThanANumberOfDaysAway(document.getPaymentRequestPayDate(), thresholdDays)) {
            if (ObjectUtils.isNull(GlobalVariables.getMessageList())) {
                GlobalVariables.setMessageList(new MessageList());
            }
            if (!GlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS)) {
                GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
            }            
        }
        
        GlobalVariables.getMessageMap().clearErrorPath();
        
        //always returns true, as this is a warning, not an error
        return true;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

}
