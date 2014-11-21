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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

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
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        if (dictionaryValidationService.validateReferenceExists(businessObjectToValidate, referenceName)) {
            GlobalVariables.getMessageMap().putError(responsibleProperty, KFSKeyConstants.ERROR_EXISTENCE, new String[] { dataDictionaryService.getAttributeLabel(businessObjectToValidate.getClass(), responsibleProperty) });
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
