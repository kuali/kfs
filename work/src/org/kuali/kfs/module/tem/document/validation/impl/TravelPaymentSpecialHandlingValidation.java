/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Validates information about the special handling section on the travel payment
 */
public class TravelPaymentSpecialHandlingValidation extends GenericValidation {
    protected TravelPayment travelPaymentForValidation;

    /**
     * Verifies that if the special handling checkbox was clicked, then special handling name and first line of special handling address are filled in
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.TRAVEL_PAYMENT);

        /* special handling name & address required if special handling is indicated */
        if (getTravelPaymentForValidation().isSpecialHandlingCode()) {
            if (StringUtils.isBlank(getTravelPaymentForValidation().getSpecialHandlingPersonName()) || StringUtils.isBlank(getTravelPaymentForValidation().getSpecialHandlingLine1Addr())) {
                errors.putError("."+TemPropertyConstants.TravelPaymentProperties.SPECIAL_HANDLING_PERSON_NAME, KFSKeyConstants.ERROR_SPECIAL_HANDLING);
                isValid = false;
            }
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.TRAVEL_PAYMENT);

        return isValid;
    }

    /**
     * @return the travel payment which should be validated
     */
    public TravelPayment getTravelPaymentForValidation() {
        return travelPaymentForValidation;
    }

    /**
     * Sets the travel payment which should be validated
     * @param travelPaymentForValidation the travel payment which should be validated
     */
    public void setTravelPaymentForValidation(TravelPayment travelPaymentForValidation) {
        this.travelPaymentForValidation = travelPaymentForValidation;
    }
}
