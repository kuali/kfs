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
package org.kuali.kfs.module.ld.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.GENERIC_CODE_PROPERTY_NAME;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Validates that a labor journal voucher document's accounting lines have valid encumbrance code 
 */
public class LaborJournalVoucherExternalEncumbranceValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that the accounting line in the labor journal voucher document for valid encumbrance code 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        AccountingLine accountingLineForValidation = getAccountingLineForValidation() ;
        if (!externalEncumbranceSpecificBusinessRulesValid(accountingLineForValidation)) {
            result = false ;
        }
        return result ;    
    }

    /**
     * Checks whether employee id exists
     * 
     * @param accountingLineForValidation laborJournalVoucherDetail line will be checked for valid encumbrance code
     * @return True if accountingLineForValidation has the valid encumbrance code, false otherwise.
     */ 
    protected boolean externalEncumbranceSpecificBusinessRulesValid(AccountingLine accountingLineForValidation) {
        boolean externalEncumbranceValid  = true ;
        
        accountingLineForValidation.refreshReferenceObject(KFSPropertyConstants.BALANCE_TYP);
        BalanceType balanceTyp = accountingLineForValidation.getBalanceTyp();
        AccountingDocumentRuleHelperService journalVoucherRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class) ;
        if (!journalVoucherRuleUtil.isValidBalanceType(balanceTyp, GENERIC_CODE_PROPERTY_NAME)) {
            externalEncumbranceValid = false ;
        }
        else if (balanceTyp.isFinBalanceTypeEncumIndicator() && KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(accountingLineForValidation.getEncumbranceUpdateCode())) {
            externalEncumbranceValid = this.isRequiredReferenceFieldsValid(accountingLineForValidation);
        }
            
        return externalEncumbranceValid ;
    }

    /**
     * This method checks that values exist in the three reference fields that are required if the balance type is set to EXTERNAL
     * ENCUMBRANCE.
     * 
     * @param accountingLine The accounting line being validated.
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    protected boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceTypeCode())) {
            valid = false;
        }
        return valid;
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
