/*
 * Copyright 2010 The Kuali Foundation.
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

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAuthAccommodationInfoRequiredValidation extends GenericValidation {
    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelAuthorizationDocument taDocument = (TravelAuthorizationDocument)event.getDocument();
        taDocument.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = taDocument.getTripType();

        boolean valid = true;
        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.PER_DIEM_EXPENSES);
        final boolean internationalAccommodationInfoRequired = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.INTERNATIONAL_TRIP_REQUIRES_ACCOMMODATION_IND);
        if(tripType !=null && isInternationalTrip(tripType) && internationalAccommodationInfoRequired) {
            //loop through each trip detail estimate and check for accommodation information
            for(PerDiemExpense detail : taDocument.getPerDiemExpenses()) {
                detail.refreshReferenceObject("accommodationType");
                //accommodation type required
                if(ObjectUtils.isNull(detail.getAccommodationType()) || StringUtils.isBlank(detail.getAccommodationTypeCode())) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.ACCOMM_TYPE, KFSKeyConstants.ERROR_REQUIRED, "Accommodation Type");
                    valid &= false;
                }
                //hotel name required
                if(StringUtils.isBlank(detail.getAccommodationName())) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.ACCOMM_NAME, KFSKeyConstants.ERROR_REQUIRED, "Accommodation Name");
                    valid &= false;
                }

                //address required
                if(StringUtils.isBlank(detail.getAccommodationAddress())) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.ACCOMM_ADDRESS, KFSKeyConstants.ERROR_REQUIRED, "Accommodation Address");
                    valid &= false;
                }
            }


        }

        if(!valid) {
            GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.ACCOMM_TYPE, KFSKeyConstants.ERROR_REQUIRED, "Accommodation Type");

        }
        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.PER_DIEM_EXPENSES);

        return valid;
    }

    /**
     * Determines if the given trip type represents an international trip, by consulting the KFS-TEM / Document / INTERNATIONAL_TRIP_TYPES parameter
     * @param tripType the trip type to determine internationality of
     * @return true if the trip type represents an international trip, false otherwise
     */
    protected boolean isInternationalTrip(TripType tripType) {
        final Collection<String> internationalTripTypes = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.INTERNATIONAL_TRIP_TYPES);
        return internationalTripTypes.contains(tripType.getCode());
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
