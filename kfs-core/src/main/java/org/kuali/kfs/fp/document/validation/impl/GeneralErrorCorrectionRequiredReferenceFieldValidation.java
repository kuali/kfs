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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class GeneralErrorCorrectionRequiredReferenceFieldValidation extends GenericValidation {
    private ParameterService parameterService;
    private AccountingLine accountingLineForValidation;

    protected static String VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";
    protected static String INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE = "INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE";
   
    /**
     * determines if object code sub types are valid with the object type code.
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    
    public boolean validate(AttributedDocumentEvent event) {
        
        GeneralErrorCorrectionDocument document = (GeneralErrorCorrectionDocument)event.getDocument();
        
        boolean valid = true;
        Class alclass = null;
        BusinessObjectEntry boe;

        if (accountingLineForValidation.isSourceAccountingLine()) {
            alclass = document.getSourceAccountingLineClass();
        }
        else if (accountingLineForValidation.isTargetAccountingLine()) {
            alclass = document.getTargetAccountingLineClass();
        }

        boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(alclass.getName());
       
        if (StringUtils.isEmpty(accountingLineForValidation.getReferenceOriginCode())) {
            putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
            valid = false;
        }
        
        if (StringUtils.isEmpty(accountingLineForValidation.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        
        return valid;
    }

    /**
     * Adds a global error for a missing required property. This is used for properties, such as reference origin code, which cannot
     * be required by the DataDictionary validation because not all documents require them.
     * 
     * @param boe
     * @param propertyName
     */
    protected void putRequiredPropertyError(BusinessObjectEntry boe, String propertyName) {

        String label = boe.getAttributeDefinition(propertyName).getShortLabel();
        GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_REQUIRED, label);

    }


    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
