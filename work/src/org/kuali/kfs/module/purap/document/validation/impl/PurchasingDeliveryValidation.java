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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurchasingDeliveryValidation extends GenericValidation {

    DateTimeService dateTimeService;
    BusinessObjectService businessObjectService;
    PostalCodeValidationService postalCodeValidationService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument)event.getDocument();
        
        GlobalVariables.getErrorMap().addToErrorPath(PurapConstants.DELIVERY_TAB_ERRORS);
        if (ObjectUtils.isNotNull(purDocument.getDeliveryRequiredDate())) {
            Date today = dateTimeService.getCurrentSqlDateMidnight();
            Date deliveryRequiredDate = purDocument.getDeliveryRequiredDate();

            if (today.after(deliveryRequiredDate)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.DELIVERY_REQUIRED_DATE, PurapKeyConstants.ERROR_DELIVERY_REQUIRED_DATE_IN_THE_PAST);
            }
        }
        
        postalCodeValidationService.validateAddress(purDocument.getDeliveryCountryCode(), purDocument.getDeliveryStateCode(), purDocument.getDeliveryPostalCode(), PurapPropertyConstants.DELIVERY_STATE_CODE, PurapPropertyConstants.DELIVERY_POSTAL_CODE);
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("campusCode", purDocument.getDeliveryCampusCode());
        
        int match = businessObjectService.countMatching(CampusParameter.class, fieldValues);
        if (match < 1) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.DELIVERY_CAMPUS_CODE, PurapKeyConstants.ERROR_DELIVERY_CAMPUS_INVALID);
        }
        return valid;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }

    
}
