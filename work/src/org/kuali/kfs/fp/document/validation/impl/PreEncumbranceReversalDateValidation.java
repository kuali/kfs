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

import static org.kuali.kfs.sys.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.PreEncumbranceDocument;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class PreEncumbranceReversalDateValidation extends GenericValidation {
    private AccountingDocumentRuleHelperService accountingDocumentRuleHelperService;
    private PreEncumbranceDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        java.sql.Date reversalDate = getAccountingDocumentForValidation().getReversalDate();
       // AccountingDocumentRuleHelperService accountingDocumentRuleUtil = getAccountingLineRuleHelperService();
        return accountingDocumentRuleHelperService.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
    }
   
    /**
     * Gets the accountingDocumentRuleHelperService attribute. 
     * @return Returns the accountingDocumentRuleHelperService.
     */
    public AccountingDocumentRuleHelperService getAccountingDocumentRuleHelperService() {
        return accountingDocumentRuleHelperService;
    }

    /**
     * Sets the accountingDocumentRuleHelperService attribute value.
     * @param accountingDocumentRuleHelperService The accountingDocumentRuleHelperService to set.
     */
    public void setAccountingDocumentRuleHelperService(AccountingDocumentRuleHelperService accountingDocumentRuleHelperService) {
        this.accountingDocumentRuleHelperService = accountingDocumentRuleHelperService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public PreEncumbranceDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(PreEncumbranceDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    
   
}
