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

import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.DictionaryValidationService;

/**
 * Runs the data dictionary validation on the travel payment - but only at nodes specified in the spring config
 */
public class TravelPaymentDataDictionaryValidation extends GenericValidation {
    protected TravelPayment travelPaymentForValidation;
    protected String travelPaymentPropertyName;
    protected DictionaryValidationService dictionaryValidationService;

    /**
     * Runs the data dictionary validation on the specified travel payment
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        return getDictionaryValidationService().isBusinessObjectValid(getTravelPaymentForValidation(), KFSPropertyConstants.DOCUMENT+"."+getTravelPaymentPropertyName());
    }

    /**
     * @return the travel document which is being validated
     */
    public TravelPayment getTravelPaymentForValidation() {
        return travelPaymentForValidation;
    }

    /**
     * Sets the travel document which is being validated
     * @param travelDocumentForValidation the travel document which is about to get itself all validated
     */
    public void setTravelPaymentForValidation(TravelPayment travelPaymentForValidation) {
        this.travelPaymentForValidation = travelPaymentForValidation;
    }

    /**
     * @return the property name of the travel payment on the document
     */
    public String getTravelPaymentPropertyName() {
        return travelPaymentPropertyName;
    }

    /**
     * Sets the property name of the travel payment on the document
     * @param travelPaymentPropertyName the property name of the travel payment on the document
     */
    public void setTravelPaymentPropertyName(String travelPaymentPropertyName) {
        this.travelPaymentPropertyName = travelPaymentPropertyName;
    }

    /**
     * @return the implementation of the DictionaryValidationService to use
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the implementation of the DictionaryValidationService to use
     * @param dictionaryValidationService the implementation of the DictionaryValidationService to use
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

}
