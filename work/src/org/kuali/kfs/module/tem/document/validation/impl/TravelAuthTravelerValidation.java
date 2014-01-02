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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthTravelerValidation extends GenericValidation {
    protected PostalCodeValidationService postalCodeValidationService;

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

            if (shouldValidateAddress(traveler.getCountryCode(), traveler.getStateCode())) {
                getPostalCodeValidationService().validateAddress(traveler.getCountryCode(), traveler.getStateCode(), traveler.getZipCode(), "stateCode", "zipCode");
            }
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.TRAVELER);

        if (endDate != null && beginDate != null && endDate.compareTo(beginDate) < 0) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_BEGIN_DT, ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(TRIP_OVERVIEW);

        int errCount = GlobalVariables.getMessageMap().getErrorCount();
        if(errCount > 0) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the address with the given country code and state code should go through standard postal code validation.  If the country code is the US,
     * or if the state code is not blank or all dashes, then we'd like to go through postal code validation
     * @param countryCode the country code of the address
     * @param stateCode the state code of the address
     * @return true if standard validation should be performed, false otherwise
     */
    protected boolean shouldValidateAddress(String countryCode, String stateCode) {
        return StringUtils.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES, countryCode) || (!StringUtils.isBlank(stateCode) && !(stateCode.matches("^-+$")));
    }

    public PostalCodeValidationService getPostalCodeValidationService() {
        return postalCodeValidationService;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }

}
