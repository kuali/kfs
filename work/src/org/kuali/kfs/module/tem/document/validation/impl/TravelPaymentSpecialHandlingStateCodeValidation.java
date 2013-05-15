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
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

/**
 * Validates that the special handling state and special handling country align
 */
public class TravelPaymentSpecialHandlingStateCodeValidation extends GenericValidation {
    protected TravelPayment travelPaymentForValidation;
    protected StateService stateService;
    protected DataDictionaryService dataDictionaryService;

    /**
     * Validates that the special handling state and special handling country align
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.TRAVEL_PAYMENT);

        final String countryCode = getTravelPaymentForValidation().getSpecialHandlingCountryCode();
        final String stateCode = getTravelPaymentForValidation().getSpecialHandlingStateCode();
        if (getTravelPaymentForValidation().isSpecialHandlingCode() && StringUtils.isNotBlank(stateCode) && StringUtils.isNotBlank(countryCode)) {
            final State state = getStateService().getState(countryCode, stateCode);
            if (state == null) {
                final String label = getDataDictionaryService().getAttributeLabel(TravelPayment.class, TemPropertyConstants.TravelPaymentProperties.SPECIAL_HANDLING_STATE_CODE);
                final String propertyPath = "." + TemPropertyConstants.TravelPaymentProperties.SPECIAL_HANDLING_STATE_CODE;
                errors.putError(propertyPath, KFSKeyConstants.ERROR_EXISTENCE, label);
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

    /**
     * @return the implementation of the StateService to use
     */
    public StateService getStateService() {
        return stateService;
    }

    /**
     * Sets the implementation of the StateService to use
     * @param stateService the implementation of the StateService to use
     */
    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    /**
     * @return the implementation of the DataDictionaryService to use
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the implementation of the DataDictionaryService to use
     * @param dataDictionaryService the implementation of the DataDictionaryService to use
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
