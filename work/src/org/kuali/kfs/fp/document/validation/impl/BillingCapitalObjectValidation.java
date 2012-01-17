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

import org.kuali.kfs.fp.document.InternalBillingDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class BillingCapitalObjectValidation extends GenericValidation {
    private ParameterService parameterService;
    private AccountingLine accountingLineForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        // validate accounting line business object
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(accountingLineForValidation);
        
        // Don't bother running other validations if the accounting line isn't valid
        if(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() < 1) {
            if (accountingLineForValidation.isSourceAccountingLine() && isCapitalObject(accountingLineForValidation)) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_DOCUMENT_IB_CAPITAL_OBJECT_IN_INCOME_SECTION);
                result = false;
            }
            // TODO phase II
            // int pendPurchaseCount = 0; 
            // TODO need to do something with this but I have no idea what
            // if (!SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroupCode) && restrictedCapitalObjectCodes.contains(objectSubTypeCode)
            // && (pendPurchaseCount <= 0))
        }
        return result;
    }

    /**
     * Checks whether the given AccountingLine's ObjectCode is a capital one.
     * 
     * @param accountingLine The accounting line the object code will be retrieved from.
     * @return True if the given accounting line's object code is a capital code, false otherwise.
     */
    protected boolean isCapitalObject(AccountingLine accountingLine) {
        ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(InternalBillingDocument.class, "CAPITAL_OBJECT_SUB_TYPE_CODES", accountingLine.getObjectCode().getFinancialObjectSubTypeCode());
        return evaluator != null ? evaluator.evaluationSucceeds() : false; // can't find the param?  then I guess we don't care...just say that nothing is a capital object
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
