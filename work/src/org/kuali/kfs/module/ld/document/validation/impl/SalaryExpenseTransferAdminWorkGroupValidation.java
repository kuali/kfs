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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Validates the admin work group for the document
 */
public class SalaryExpenseTransferAdminWorkGroupValidation extends GenericValidation {
    private SalaryExpenseTransferDocument accountingDocumentForValidation;

    /**
     * Validates that a valid work group exists 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = getAccountingDocumentForValidation() ;
        
        if (!isValidAdminGroup(salaryExpenseTransferDocument)){
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.WORKGROUP_NAME, LaborConstants.SalaryExpenseTransfer.SET_ADMIN_WORKGROUP_PARM_NM) ;
            result = false ;
        }
        
        return result;
    }

    /**
     * Checks whether the group is a member of the admin group
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */ 
    private boolean isValidAdminGroup(SalaryExpenseTransferDocument accountingDocument) {
        
        boolean isAdmin = false ;
       
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(accountingDocument);        
        isAdmin = documentAuthorizer.isAuthorized(accountingDocument, LaborConstants.LABOR_MODULE_CODE, 
                     LaborConstants.PermissionNames.OVERRIDE_TRANSFER_IMPACTING_EFFORT_CERTIFICATION, 
                                  GlobalVariables.getUserSession().getPerson().getPrincipalId());

        return isAdmin ;
    }
    
    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public SalaryExpenseTransferDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(SalaryExpenseTransferDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}

