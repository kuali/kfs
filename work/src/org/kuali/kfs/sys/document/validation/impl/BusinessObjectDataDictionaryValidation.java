/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * A validation to have the data dictionary perform its validations upon a business object
 */
public class BusinessObjectDataDictionaryValidation extends GenericValidation {
    private DictionaryValidationService dictionaryValidationService;
    private PersistableBusinessObject businessObjectForValidation;

    /**
     * Validates a business object against the data dictionary
     * <strong>expects a business object to be the first parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        return getDictionaryValidationService().isBusinessObjectValid(businessObjectForValidation);
    }

    /**
     * Gets the dictionaryValidationService attribute. 
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the businessObjectForValidation attribute. 
     * @return Returns the businessObjectForValidation.
     */
    public PersistableBusinessObject getBusinessObjectForValidation() {
        return businessObjectForValidation;
    }

    /**
     * Sets the businessObjectForValidation attribute value.
     * @param businessObjectForValidation The businessObjectForValidation to set.
     */
    public void setBusinessObjectForValidation(PersistableBusinessObject businessObjectForValidation) {
        this.businessObjectForValidation = businessObjectForValidation;
    }
}
