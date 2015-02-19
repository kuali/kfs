/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

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
            if (ObjectUtils.isNull(KNSGlobalVariables.getMessageList())) {
                KNSGlobalVariables.setMessageList(new MessageList());
            }
            if (!KNSGlobalVariables.getMessageList().contains(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS)) {
                KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
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
