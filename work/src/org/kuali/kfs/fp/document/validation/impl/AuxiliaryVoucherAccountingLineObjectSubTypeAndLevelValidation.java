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
package org.kuali.module.financial.document.validation.impl;

import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;
import static org.kuali.module.financial.rules.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;

/**
 * Validates the sub object type and object level of an object code entered on an accounting line on an Auxiliary Voucher document.
 */
public class AuxiliaryVoucherAccountingLineObjectSubTypeAndLevelValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private ObjectTypeService objectTypeService;

    /**
     * This method checks to see if there is a valid combination of sub type and object level
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean retval = true;
        
        ObjectType objectType = getObjectType(accountingLineForValidation);

        StringBuffer combinedCodes = new StringBuffer(objectType.getCode()).append(',').append(accountingLineForValidation.getObjectCode().getFinancialObjectSubType().getCode()).append(',').append(accountingLineForValidation.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode());
        ParameterEvaluator evalutator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_COMBINED_CODES);

        retval = !evalutator.equals(combinedCodes.toString());

        if (!retval) {
            String errorObjects[] = { accountingLineForValidation.getObjectCode().getFinancialObjectCode(), accountingLineForValidation.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode(), accountingLineForValidation.getObjectCode().getFinancialObjectSubType().getCode(), objectType.getCode() };
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
        }

        return retval;
    }
    
    /**
     * 
     * @param line
     * @return
     */
    private ObjectType getObjectType(AccountingLine line) {
        line.refreshReferenceObject("objectCode");
        String objectTypeCode = line.getObjectCode().getFinancialObjectTypeCode();
        return getObjectTypeService().getByPrimaryKey(objectTypeCode);
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

    /**
     * Gets the objectTypeService attribute. 
     * @return Returns the objectTypeService.
     */
    public ObjectTypeService getObjectTypeService() {
        return objectTypeService;
    }

    /**
     * Sets the objectTypeService attribute value.
     * @param objectTypeService The objectTypeService to set.
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}
