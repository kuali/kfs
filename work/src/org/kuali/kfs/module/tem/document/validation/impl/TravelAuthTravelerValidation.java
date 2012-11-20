/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRIP_OVERVIEW;

import java.util.Date;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelAuthTravelerValidation extends GenericValidation {

    //@Override
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(TRIP_OVERVIEW);
        
        TravelDocumentBase doc = (TravelDocumentBase)event.getDocument();
        Date beginDate = doc.getTripBegin();
        Date endDate = doc.getTripEnd();
        TravelerDetail traveler = doc.getTraveler();
        
        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.TRAVELER);
        if (ObjectUtils.isNotNull(traveler) && ObjectUtils.isNotNull(traveler.getTravelerTypeCode())) {
            if (traveler.getTravelerTypeCode().equals(TemConstants.EMP_TRAVELER_TYP_CD) && ObjectUtils.isNull(traveler.getPrincipalId())) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TravelAuthorizationFields.TRAVELER_PRINCIPAL_ID, KFSKeyConstants.ERROR_REQUIRED, "Principal Id");
            }
            
            SpringContext.getBean(PostalCodeValidationService.class).validateAddress(traveler.getCountryCode(), traveler.getStateCode(), traveler.getZipCode(), "stateCode", "zipCode");
        }
        
        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.TRAVELER);
        
        if (endDate != null && beginDate != null && endDate.compareTo(beginDate) < 0) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.TRIP_BEGIN_DT, ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN);
        }
        
        GlobalVariables.getMessageMap().removeFromErrorPath(TRIP_OVERVIEW);
        
        int errCount = GlobalVariables.getMessageMap().getErrorCount();
        if(errCount > 0) {
            return false;
        }
        
        return true;
    }

}
