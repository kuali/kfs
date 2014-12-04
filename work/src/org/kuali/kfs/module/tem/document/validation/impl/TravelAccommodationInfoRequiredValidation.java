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

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelAccommodationInfoRequiredValidation extends GenericValidation {
    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelDocument document = (TravelDocument)event.getDocument();
       document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();

        boolean valid = true;
        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.PER_DIEM_EXPENSES);
        final boolean internationalAccommodationInfoRequired = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.INTERNATIONAL_TRIP_REQUIRES_ACCOMMODATION_IND);
        if(tripType !=null && isInternationalTrip(tripType) && internationalAccommodationInfoRequired) {
            //loop through each trip detail estimate and check for accommodation information
            for(PerDiemExpense detail : document.getPerDiemExpenses()) {
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
