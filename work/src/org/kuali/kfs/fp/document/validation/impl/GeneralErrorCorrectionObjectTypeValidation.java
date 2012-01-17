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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class GeneralErrorCorrectionObjectTypeValidation extends GenericValidation {
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
        accountingLineForValidation.refreshReferenceObject("objectCode");
        ObjectCode code = accountingLineForValidation.getObjectCode();
        boolean retVal = true;
       
        if (!ObjectUtils.isNull(code)) {
            ParameterEvaluator parameterEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(
                    GeneralErrorCorrectionDocument.class, 
                    VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, 
                    INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, 
                    code.getFinancialObjectTypeCode(), 
                    code.getFinancialObjectSubTypeCode());
            
            retVal = parameterEvaluator.evaluateAndAddError(
                    SourceAccountingLine.class,
                    "objectCode.financialObjectSubTypeCode",
                    KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        return retVal;
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
