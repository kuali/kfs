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
package org.kuali.kfs.validation;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

/**
 * Validation to check if a reference of a validation 
 */
public class ReferenceExistsValidation extends GenericValidation {
    private PersistableBusinessObject businessObjectToValidate;
    private DictionaryValidationService dictionaryValidationService;
    private DataDictionaryService dataDictionaryService;
    private String referenceName;
    private String responsibleProperty;

    /**
     * 
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        if (dictionaryValidationService.validateReferenceExists(businessObjectToValidate, referenceName)) {
            GlobalVariables.getErrorMap().putError(responsibleProperty, KFSKeyConstants.ERROR_EXISTENCE, new String[] { dataDictionaryService.getAttributeLabel(businessObjectToValidate.getClass(), responsibleProperty) });
            result = false;
        }
        return result;
    }

    /**
     * Gets the businessObjectToValidate attribute. 
     * @return Returns the businessObjectToValidate.
     */
    public PersistableBusinessObject getBusinessObjectToValidate() {
        return businessObjectToValidate;
    }

    /**
     * Sets the businessObjectToValidate attribute value.
     * @param businessObjectToValidate The businessObjectToValidate to set.
     */
    public void setBusinessObjectToValidate(PersistableBusinessObject businessObjectToValidate) {
        this.businessObjectToValidate = businessObjectToValidate;
    }

    /**
     * Gets the referenceName attribute. 
     * @return Returns the referenceName.
     */
    public String getReferenceName() {
        return referenceName;
    }

    /**
     * Sets the referenceName attribute value.
     * @param referenceName The referenceName to set.
     */
    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    /**
     * Gets the responsibleProperty attribute. 
     * @return Returns the responsibleProperty.
     */
    public String getResponsibleProperty() {
        return responsibleProperty;
    }

    /**
     * Sets the responsibleProperty attribute value.
     * @param responsibleProperty The responsibleProperty to set.
     */
    public void setResponsibleProperty(String responsibleProperty) {
        this.responsibleProperty = responsibleProperty;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
