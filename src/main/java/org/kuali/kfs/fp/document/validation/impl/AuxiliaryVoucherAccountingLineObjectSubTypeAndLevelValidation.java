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

import static org.kuali.kfs.fp.document.validation.impl.AuxiliaryVoucherDocumentRuleConstants.RESTRICTED_COMBINED_CODES;
import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE;

import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates the sub object type and object level of an object code entered on an accounting line on an Auxiliary Voucher document.
 */
public class AuxiliaryVoucherAccountingLineObjectSubTypeAndLevelValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private ObjectTypeService objectTypeService;

    /**
     * This method checks to see if there is a valid combination of sub type and object level
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        ObjectType objectType = getObjectType(accountingLineForValidation);
        StringBuffer combinedCodes = new StringBuffer(objectType.getCode()).append(',').append(accountingLineForValidation.getObjectCode().getFinancialObjectSubType().getCode()).append(',').append(accountingLineForValidation.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode());
        if (!/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AuxiliaryVoucherDocument.class, RESTRICTED_COMBINED_CODES, combinedCodes.toString()).evaluationSucceeds()) {
            String errorObjects[] = { accountingLineForValidation.getObjectCode().getFinancialObjectCode(), accountingLineForValidation.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode(), accountingLineForValidation.getObjectCode().getFinancialObjectSubType().getCode(), objectType.getCode() };
            GlobalVariables.getMessageMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE, errorObjects);
            return false;
        }
        return true;
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
