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
