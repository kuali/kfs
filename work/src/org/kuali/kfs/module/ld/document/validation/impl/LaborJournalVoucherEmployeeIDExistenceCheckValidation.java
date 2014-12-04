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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that a labor journal voucher document's accounting lines have valid Employee ID 
 */
public class LaborJournalVoucherEmployeeIDExistenceCheckValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation; 
    
    /**
     * Validates that the accounting line in the labor journal voucher document have an existing employee id 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        LaborJournalVoucherDetail laborJournalVoucherDetail = (LaborJournalVoucherDetail) getAccountingLineForValidation();
        String emplid = laborJournalVoucherDetail.getEmplid();
        
        if (StringUtils.isBlank(emplid) || LaborConstants.getDashEmplId().equals(emplid)) {
            return true ;
        }
        if (!employeeIDExistenceCheck(emplid)) {
            result = false ;
        }
        return result ;    
    }

    /**
     * Checks whether employee id exists
     * 
     * @param employeeid employee id is checked against the collection of universal users
     * @return True if the given employee id exists, false otherwise.
     */ 
    protected boolean employeeIDExistenceCheck(String employeeid) {
        
        boolean employeeIDExists  = true ;
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, employeeid);
        
        Collection emplidMatches = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).findPeople(criteria);
        if (emplidMatches == null || emplidMatches.isEmpty()) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(LaborJournalVoucherDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.EMPLID).getLabel();
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.EMPLID, KFSKeyConstants.ERROR_EXISTENCE, label) ;
            employeeIDExists = false ;
        }
            
        return employeeIDExists ;
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

