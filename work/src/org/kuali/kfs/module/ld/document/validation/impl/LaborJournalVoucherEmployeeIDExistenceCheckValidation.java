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

