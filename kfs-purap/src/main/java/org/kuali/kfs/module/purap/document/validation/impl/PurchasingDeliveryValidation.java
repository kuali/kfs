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

import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingDeliveryValidation extends PurchasingProcessRequestorPhoneAndEmailAddressValidation {

    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected PostalCodeValidationService postalCodeValidationService;
    
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument)event.getDocument();

        GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.DELIVERY_TAB_ERRORS);        
        //perform the validation against phone Number
        if (StringUtils.isNotBlank(purDocument.getRequestorPersonPhoneNumber())) {
            if (!validatePhoneNumber(purDocument.getRequestorPersonPhoneNumber())) {
                valid &= false;
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.REQUESTOR_PERSON_PHONE_NUMBER, PurapKeyConstants.ERROR_INVALID_PH0NE_NUMBER);
            }
        }

        //perform the validation against email address
        if (StringUtils.isNotBlank(purDocument.getRequestorPersonEmailAddress())) {
            if (!validateEmailAddress(purDocument.getRequestorPersonEmailAddress())) {
                valid &= false;
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.REQUESTOR_PERSON_EMAIL_ADDRESS, PurapKeyConstants.ERROR_INVALID_EMAIL_ADDRESS);
            }
        }
        
        postalCodeValidationService.validateAddress(purDocument.getDeliveryCountryCode(), purDocument.getDeliveryStateCode(), purDocument.getDeliveryPostalCode(), PurapPropertyConstants.DELIVERY_STATE_CODE, PurapPropertyConstants.DELIVERY_POSTAL_CODE);

        int match = businessObjectService.countMatching(CampusParameter.class, Collections.singletonMap("campusCode", purDocument.getDeliveryCampusCode()));
        if (match < 1) {
            valid = false;
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.DELIVERY_CAMPUS_CODE, PurapKeyConstants.ERROR_DELIVERY_CAMPUS_INVALID);
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        return valid;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }
}
