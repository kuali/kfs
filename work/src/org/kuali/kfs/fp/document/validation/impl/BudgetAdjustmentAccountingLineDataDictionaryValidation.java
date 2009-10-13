/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * A validation which uses the AccountingLineRuleHelperService to validate the values on an accounting line
 */
public class BudgetAdjustmentAccountingLineDataDictionaryValidation extends GenericValidation {
    private AccountingLineRuleHelperService ruleHelperService; 
    private AccountingLine accountingLineForValidation;

    /**
     * Okay, okay, so yeah, I could have handled this through a bunch of DD validations.  But it's late Thursday afternoon
     * and someone has already written this code for me...
     * <strong>Expects an accounting line as the first parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        return ruleHelperService.validateAccountingLine(accountingLineForValidation);
    }

    /**
     * Sets the ruleHelperService attribute value.
     * @param ruleHelperService The ruleHelperService to set.
     */
    public void setAccountingLineRuleHelperService(AccountingLineRuleHelperService ruleHelperService) {
        this.ruleHelperService = ruleHelperService;
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
